# ADR 0005 — Observabilidade com New Relic (agente APM + nri-bundle)

**Status:** Aceito
**Data:** 2026-10-24

## Contexto

Até aqui a única visibilidade sobre a aplicação no EKS era o `/actuator/health` usado pelas probes e o `kubectl logs` em texto puro, sem nenhuma forma de ligar as linhas de log de uma mesma requisição. A fase exige monitorar latência das APIs, consumo de CPU/memória do Kubernetes, healthcheck/uptime, alertas para falhas no processamento de Ordens de Serviço e logs estruturados (JSON) com correlação entre requisições.

## Decisão

Usar o **New Relic** (plano free) com o menor número de peças possível:

- **Agente Java APM** (`newrelic.jar`), embutido na imagem ([`Dockerfile`](../../Dockerfile)) e ligado via `JAVA_TOOL_OPTIONS` no [`app-deployment.yaml`](../../k8s/app-deployment.yaml). Configurado só por variáveis de ambiente. Ele entrega latência, erros e distributed tracing, e também **encaminha os logs** já com `trace.id`/`span.id` (*logs in context*).
- **Helm chart `nri-bundle`**, instalado pelo [`pipeline.yml`](../../.github/workflows/pipeline.yml) com `helm upgrade --install` (idempotente), para métricas de CPU/memória, estado dos pods/probes e eventos do cluster. O módulo de logs (Fluent Bit) fica desligado para não duplicar o que o agente já envia. O webhook de *metadata injection* também fica desligado (`webhook.enabled=false`): ele quebrava o `helm upgrade` no Helm 4 (conflito de server-side apply com o `kube-webhook-certgen`) As variáveis que ele injetaria (`NEW_RELIC_METADATA_KUBERNETES_*`: pod, nó, namespace, cluster, deployment, container) são declaradas direto no Deployment, via Downward API e o ConfigMap `newrelic-config`, então o APM continua mostrando em qual pod/nó cada requisição rodou.
- **Logs JSON** com o *structured logging* nativo do Spring Boot 3.4+ (formato ECS) e um [`RequestIdFilter`](../../src/main/java/com/fiap/tech_challenge_backend/shared/infrastructure/web/RequestIdFilter.java) que põe um `requestId` no MDC e o devolve no header `X-Request-Id`.
- **Healthcheck/uptime** pelas probes já existentes (via `nri-bundle`), mais Service Level e alerta de *loss of signal* no APM. Sem monitor sintético externo, porque o DNS do Load Balancer muda a cada recriação do cluster no AWS Academy.
- **Alertas** como condições NRQL na UI (ver [`docs/observability.md`](../observability.md)).

## Alternativas consideradas

| Opção | Prós | Contras |
|---|---|---|
| **Agente APM + `nri-bundle`** (escolhida) | Nenhuma dependência nova no `pom.xml`; logs correlacionados ao trace sem código; um único `helm` para o cluster | Agente proprietário (lock-in no New Relic); +~60 MB de memória por pod |
| OpenTelemetry (SDK/agent OTel + Collector) enviando ao New Relic via OTLP | Padrão aberto, troca de backend sem mexer na app | Mais peças para operar (Collector, configuração de pipelines); correlação de logs exige configuração extra |
| Prometheus + Grafana + Loki self-hosted | Sem custo de SaaS | Vários componentes para instalar e manter no cluster, e o cluster do Academy é efêmero (perderia o histórico) |
| Monitor sintético (Ping) no `/actuator/health` | Testa o caminho externo completo (ELB → app) | Exige URL pública estável; o DNS do ELB muda a cada recriação do cluster |

## Consequências

- A license key fica só no GitHub Secrets (`NEW_RELIC_LICENSE_KEY`) e num Secret do K8s criado pelo pipeline, nunca versionada. Sem ela, o agente se desativa e a aplicação sobe normalmente (ex.: Kind local).
- Homolog e produção aparecem separados no New Relic: o pipeline cria o ConfigMap `newrelic-config` com o nome da app (`tech-challenge-app` ou `tech-challenge-app-homolog`) e o label `env`, e o `nri-bundle` usa o nome do cluster lido do SSM de cada ambiente.
- Logs de produção passam a ser JSON. O profile `dev` mantém texto legível.
- Erros 4xx não contam como falha no APM (`NEW_RELIC_ERROR_COLLECTOR_IGNORE_STATUS_CODES=400-499`), então os alertas refletem só falhas reais (5xx).
- Alertas, Service Level e dashboard são configurados na UI do New Relic, não em código. Se o projeto crescer, a evolução natural é versioná-los com o provider Terraform do New Relic.
