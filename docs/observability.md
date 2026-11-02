# Observabilidade — New Relic

Como a aplicação é monitorada e o passo a passo para deixar tudo funcionando. A decisão
de arquitetura está no [ADR 0005](adrs/0005-observabilidade-new-relic.md).

## O que é monitorado e como

| Requisito | Como | Onde está configurado |
|---|---|---|
| Latência das APIs | Agente Java APM (`newrelic.jar`) | [`Dockerfile`](../Dockerfile) + [`k8s/app-deployment.yaml`](../k8s/app-deployment.yaml) |
| CPU/memória do Kubernetes | Helm chart `nri-bundle` (infra + kube-state-metrics + eventos) | Step no [`pipeline.yml`](../.github/workflows/pipeline.yml) |
| Healthcheck | Probes de readiness/liveness (`/actuator/health`) reportadas pelo `nri-bundle` | Alertas na UI do New Relic |
| Uptime | Service Level de disponibilidade + alerta de *loss of signal* no APM | UI do New Relic |
| Falhas em Ordens de Serviço | Alertas NRQL sobre `Transaction` 5xx e `Log` ERROR | UI do New Relic |
| Logs JSON + correlação | Logging estruturado ECS do Spring Boot + [`RequestIdFilter`](../src/main/java/com/fiap/tech_challenge_backend/shared/infrastructure/web/RequestIdFilter.java); o agente envia os logs com `trace.id`/`span.id` | [`application.yml`](../src/main/resources/application.yml) |
| Volume diário de OS | Evento customizado `OrdemServicoCriada` | Ver [seção 6](#6-métricas-de-negócio) |
| Tempo médio por status | Evento customizado `OrdemServicoStatusAlterado` | Ver [seção 6](#6-métricas-de-negócio) |
| Falhas nas integrações | Evento customizado `Integracao` (e-mail e PDF) + dados do agente (banco) | Ver [seção 6](#6-métricas-de-negócio) |

## 1. Pré-requisitos (uma vez só)

1. Criar uma conta grátis em <https://newrelic.com/signup> (100 GB/mês de ingestão no plano free).
2. Copiar a **License key**: menu do usuário → *API keys* → chave do tipo **INGEST - LICENSE**.
3. No GitHub do repositório: *Settings → Secrets and variables → Actions → New repository secret*
   - Nome: `NEW_RELIC_LICENSE_KEY`
   - Valor: a license key copiada.

A região da conta (US/EU) é detectada pela própria chave; não há nada a configurar.

## 2. Deploy

Basta fazer merge na `homolog` ou na `main`. O job `Deploy (EKS)` do pipeline:

1. cria o Secret `newrelic-license` e o ConfigMap `newrelic-config` no namespace `tech-challenge`;
2. instala/atualiza o `nri-bundle` no namespace `newrelic`, usando o nome do cluster lido do SSM do ambiente;
3. aplica os manifests: o Deployment liga o agente via `JAVA_TOOL_OPTIONS`.

Cada ambiente aparece separado no New Relic:

| Ambiente | Branch | App no APM | Label | Cluster |
|---|---|---|---|---|
| Produção | `main` | `tech-challenge-app` | `env:production` | `eks-tech-challenge` |
| Homolog | `homolog` | `tech-challenge-app-homolog` | `env:homolog` | valor de `/tech-challenge-homolog/eks_cluster_name` no SSM |

As consultas e os alertas abaixo usam os nomes de **produção**. Para homolog, troque
`tech-challenge-app` por `tech-challenge-app-homolog` e o `clusterName` pelo cluster de homolog.
`deploymentName` e `containerName` continuam iguais nos dois ambientes.

Em ~2–5 minutos devem aparecer no New Relic:

- **APM & Services → tech-challenge-app** (latência, throughput, erros, traces);
- **Kubernetes → eks-tech-challenge** (CPU, memória, pods, eventos);
- **Logs** (filtrar por `entity.name:"tech-challenge-app"`).

Conferir se o agente subiu:

```bash
kubectl -n tech-challenge logs -l app=tech-challenge-app --tail=200 | Select-String "New Relic"
kubectl -n newrelic get pods
```

> Sem o Secret (ex.: no Kind local), o agente apenas se desativa e a aplicação sobe normalmente.

## 3. Logs estruturados e correlação

- Em produção, cada linha de log no stdout é **JSON (ECS)**; no profile `dev` continua texto.
- Toda requisição recebe um `requestId`, que vem do header `X-Request-Id` ou `X-Correlation-Id` ou é gerado (UUID). Ele:
  - vai para o MDC, então aparece em todo log daquela requisição;
  - volta no header de resposta `X-Request-Id`.
- O agente envia os logs ao New Relic com `trace.id`, `span.id` e `context.requestId`. Abrindo um
  trace em *APM → Distributed tracing*, a aba **Logs** mostra só as linhas daquela requisição.

Consulta útil (*Query your data*):

```sql
SELECT timestamp, level, message FROM Log
WHERE entity.name = 'tech-challenge-app' AND context.requestId = '<id do header X-Request-Id>'
SINCE 1 hour ago
```

## 4. Healthcheck e uptime

As probes do Deployment já chamam `/actuator/health`; o `nri-bundle` envia o estado dos pods.

**Service Level (uptime em %):** *APM → tech-challenge-app → Service levels → Add a service level*
- Tipo: *Availability* (ou SLI customizado):
  - Valid events: `FROM Transaction WHERE appName = 'tech-challenge-app'`
  - Good events: `FROM Transaction WHERE appName = 'tech-challenge-app' AND httpResponseCode < 500`
- Objetivo: **99%** em 7 dias.

Os alertas de healthcheck/uptime estão na tabela abaixo.

## 5. Alertas

1. *Alerts → Alert policies → New alert policy* → nome `tech-challenge`.
2. *Alerts → Destinations → Email* → e-mail do grupo; depois *Workflows → Add workflow*
   ligando a policy `tech-challenge` ao destino.
3. Na policy, *Add a condition → NRQL query* para cada linha:

| Condição | NRQL | Threshold |
|---|---|---|
| Falha no processamento de OS (5xx) | `SELECT count(*) FROM Transaction WHERE appName = 'tech-challenge-app' AND request.uri LIKE '%ordens-servico%' AND httpResponseCode >= '500'` | above 0 por 5 min |
| Erros logados no atendimento | `SELECT count(*) FROM Log WHERE entity.name = 'tech-challenge-app' AND level = 'ERROR' AND logger.name LIKE '%atendimento%'` | above 0 por 5 min |
| Latência alta (p95) | `SELECT percentile(duration, 95) FROM Transaction WHERE appName = 'tech-challenge-app'` | above 1 (segundo) por 5 min |
| Pod não pronto (healthcheck) | `SELECT latest(isReady) FROM K8sPodSample WHERE clusterName = 'eks-tech-challenge' AND deploymentName = 'tech-challenge-app' FACET podName` | below 1 por 3 min |
| Réplicas indisponíveis | `SELECT latest(podsUnavailable) FROM K8sDeploymentSample WHERE clusterName = 'eks-tech-challenge' AND deploymentName = 'tech-challenge-app'` | above 0 por 5 min |
| App parou de reportar (uptime) | `SELECT count(*) FROM Transaction WHERE appName = 'tech-challenge-app'` + em *Signal loss*: **Open new "lost signal" incident** após 5 min | sinal perdido |
| CPU do pod alta | `SELECT average(cpuUsedCores / cpuLimitCores) * 100 FROM K8sContainerSample WHERE clusterName = 'eks-tech-challenge' AND containerName = 'app' FACET podName` | above 80 por 5 min |
| Memória do pod alta | `SELECT average(memoryWorkingSetBytes / memoryLimitBytes) * 100 FROM K8sContainerSample WHERE clusterName = 'eks-tech-challenge' AND containerName = 'app' FACET podName` | above 80 por 5 min |
| Pod reiniciando | `SELECT max(restartCountDelta) FROM K8sContainerSample WHERE clusterName = 'eks-tech-challenge' AND containerName = 'app' FACET podName` | above 0 por 1 min |
| Falha em integração (e-mail/PDF) | `SELECT count(*) FROM Integracao WHERE appName = 'tech-challenge-app' AND sucesso IS false` | above 0 por 5 min |
| Erro de banco | `SELECT count(*) FROM TransactionError WHERE appName = 'tech-challenge-app' AND (error.class LIKE '%SQL%' OR error.class LIKE '%DataAccess%' OR error.class LIKE '%Jdbc%' OR error.class LIKE '%Hibernate%')` | above 0 por 5 min |

> Erros 4xx (validação, OS não encontrada) são ignorados pelo agente
> (`NEW_RELIC_ERROR_COLLECTOR_IGNORE_STATUS_CODES=400-499`), então não geram alerta.

## 6. Métricas de negócio

O New Relic não tira estes dados das requisições, então a aplicação os envia como **eventos
customizados**, pela porta `MetricasNegocioPort`. Só o
[`NewRelicMetricasNegocioAdapter`](../src/main/java/com/fiap/tech_challenge_backend/atendimento/adapters/out/observability/NewRelicMetricasNegocioAdapter.java)
conhece o New Relic. Sem o agente (local, Kind) a API não faz nada, e uma falha no envio nunca afeta a operação.

| Evento | Quando é enviado | Atributos |
|---|---|---|
| `OrdemServicoCriada` | OS criada | `osId`, `urgente`, `requestId` |
| `OrdemServicoStatusAlterado` | OS muda de status (troca só de mecânico não conta) | `osId`, `statusOrigem`, `statusDestino`, `tempoNoStatusSegundos`, `tempoDesdeCriacaoSegundos`, `requestId` |
| `Integracao` | Cada envio de e-mail e cada geração de PDF | `integracao` (`email`/`pdf`), `operacao` (`com_anexo`/`sem_anexo`/`orcamento`), `sucesso`, `duracaoMs`, `erro`, `requestId` |

- **Tempo por status:** `tempoNoStatusSegundos` é o tempo que a OS ficou em `statusOrigem`, medido quando
  ela sai dele. Diagnóstico é `EM_DIAGNOSTICO`, Execução é `EM_EXECUCAO` e Finalização é `FINALIZADA` (até
  `ENTREGUE`). O tempo total até finalizar é `tempoDesdeCriacaoSegundos` com `statusDestino = 'FINALIZADA'`.
- **Onde é capturado:** toda criação e troca de status grava no histórico, então o
  [`OsHistoricoStatusMetricasDecorator`](../src/main/java/com/fiap/tech_challenge_backend/atendimento/adapters/out/observability/OsHistoricoStatusMetricasDecorator.java)
  é o único ponto necessário.
- **Rollback:** eventos de OS só saem depois do commit. Eventos de integração saem na hora, para a falha
  aparecer mesmo se a transação for desfeita.
- **Dados pessoais:** nenhum e-mail, CPF ou placa é enviado. O atributo `erro` traz só o nome da classe da
  exceção, porque a mensagem pode conter o endereço de e-mail.
- **Banco:** não precisa de evento. O agente já envia `databaseDuration` e `databaseCallCount` em
  `Transaction`, *spans* com `category = 'datastore'` e as exceções em `TransactionError`.

OS anteriores ao deploy não entram nessas métricas, e a retenção de eventos customizados da conta limita
quantos dias aparecem.

## 7. Dashboard

O dashboard está versionado em [`newrelic/dashboard.json`](newrelic/dashboard.json). Para criar no New Relic:

1. *Dashboards → Import dashboard*.
2. Colar o conteúdo do arquivo e escolher a conta → *Import dashboard*.

Um único dashboard serve os dois ambientes: no topo há as variáveis **Aplicação**
(`tech-challenge-app` ou `tech-challenge-app-homolog`) e **Cluster** (`eks-tech-challenge` ou
`eks-tech-challenge-homolog`), preenchidas com o que estiver reportando ao New Relic. O padrão é
homolog, que é onde há dados hoje.

| Página | O que mostra |
|---|---|
| Visão geral | Disponibilidade (% sem 5xx), latência p95, req/min, % de 5xx, pods prontos, reinícios, CPU/memória por pod, healthcheck e logs por nível |
| Negócio | Volume diário de OS, tempo médio em Diagnóstico, Execução e Finalização, tempo total até finalizar, fluxo de status, falhas de e-mail e PDF, tempo e erros do banco |
| APIs | Tabela por endpoint (volume, média, p95, % 5xx), endpoints mais lentos, erros (`TransactionError`) e requisições acima de 1 s com `traceId` |
| Ordens de Serviço | Requisições e falhas 5xx em `/api/v1/ordens-servico` e `/api/public/atendimento/ordens`, p95, logs ERROR do atendimento com `requestId` |
| Kubernetes | Pods disponíveis/desejados, uso dos nós, CPU/memória por pod, réplicas do HPA, estado atual dos pods e eventos do namespace |
| Logs | Logs por nível, loggers com mais WARN/ERROR e últimos erros com `requestId`/`trace.id` |

As consultas de API ignoram `/actuator/*`: as probes chamam o healthcheck a cada 10–20 s e
distorceriam latência e volume. O healthcheck tem um gráfico próprio na Visão geral.

As queries fixam o *account ID* `8656379` (conta do grupo no New Relic). Se o dashboard for
importado em outra conta, troque esse número no JSON antes de importar.

Se o dashboard for alterado na UI, exporte de novo (*... → Copy JSON to clipboard*) e atualize o
arquivo no repositório. O caminho inverso não é automático: mudar o arquivo não altera o dashboard que
já está no New Relic. Importe o arquivo novo e apague o antigo na UI.

## 8. Como testar os alertas

1. **Falha de OS:** provocar um 500 num endpoint de `/api/v1/ordens-servico` e conferir o e-mail.
2. **Healthcheck:** apontar a readinessProbe para um path inexistente:
   ```bash
   kubectl -n tech-challenge patch deploy tech-challenge-app --type=json -p '[{"op":"replace","path":"/spec/template/spec/containers/0/readinessProbe/httpGet/path","value":"/nao-existe"}]'
   ```
   Os pods novos ficam *not ready* e disparam "Pod não pronto" e "Réplicas indisponíveis". Para desfazer: `kubectl -n tech-challenge rollout undo deploy tech-challenge-app`.
3. **Uptime:** `kubectl -n tech-challenge scale deploy tech-challenge-app --replicas=0` por ~10 min
   dispara o *loss of signal*; depois volte com `--replicas=2`.

## Troubleshooting

- **App não aparece no APM:** confira se o Secret existe (`kubectl -n tech-challenge get secret newrelic-license`)
  e se o pod foi recriado depois dele (`kubectl -n tech-challenge rollout restart deploy tech-challenge-app`).
- **Cluster não aparece:** `kubectl -n newrelic get pods`. Os pods `newrelic-infrastructure` precisam rodar
  como *privileged* (DaemonSet), o que os nodes do EKS permitem por padrão.
- **`helm upgrade` falha com `conflict with "kube-webhook-certgen"`:** é o webhook de metadata-injection
  brigando com o server-side apply do Helm 4. O pipeline instala com `webhook.enabled=false` para evitar isso;
  as variáveis `NEW_RELIC_METADATA_KUBERNETES_*` que ele injetaria (pod, nó, namespace, cluster) estão
  declaradas direto no `k8s/app-deployment.yaml`, então o APM continua mostrando onde cada requisição rodou.
- **Log duplicado:** o `nri-bundle` é instalado com `logging.enabled=false` justamente para que só o
  agente APM envie logs.
