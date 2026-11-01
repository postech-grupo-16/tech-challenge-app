# ADR 0002 — Autoscaling via HPA (CPU), não KEDA ou autoscaling manual

**Status:** Aceito (decisão herdada da Fase 2, mantida na Fase 3; métrica de memória removida em 2026-11-01, ver [Revisão](#revisão-2026-11-01--remoção-da-métrica-de-memória))
**Data:** 2026-10-19

## Contexto

A oficina tem tráfego variável (picos quando múltiplos clientes consultam o status das suas ordens de serviço ao mesmo tempo, por exemplo). O desafio pede "Cluster Kubernetes com escalabilidade".

## Decisão

Usar o **HorizontalPodAutoscaler** (`autoscaling/v2`) nativo do Kubernetes, configurado em [`k8s/app-hpa.yaml`](../../k8s/app-hpa.yaml):

```yaml
minReplicas: 2
maxReplicas: 6
metrics:
  - resource: cpu, averageUtilization: 70
```

O HPA depende do `metrics-server` (add-on instalado pelo `tech-challenge-infra-k8s`, ver [`k8s-metrics-server.tf`](https://github.com/Gabrielfsg/tech-challenge-infra-k8s/blob/main/k8s-metrics-server.tf)) para coletar o uso de CPU dos pods.

## Alternativas consideradas

| Opção | Prós | Contras |
|---|---|---|
| **HPA nativo (CPU)** (escolhida) | Nenhuma dependência externa além do `metrics-server`, que já é leve e padrão; suficiente para uma carga HTTP típica de CRUD | Reage só a CPU, não a métricas de negócio (ex.: requisições/segundo, fila de processamento) |
| HPA nativo (CPU + memória) | Também escalaria por consumo de memória | Em Java a memória não acompanha a carga: a JVM reserva heap ao subir e não devolve, então a métrica fica alta mesmo ociosa e cria réplicas sem necessidade (ver Revisão abaixo) |
| **KEDA** (event-driven autoscaling) | Escalaria por métricas customizadas (ex.: requisições por segundo via Prometheus, profundidade de fila) | Componente adicional a instalar e operar no cluster; sem fila/evento assíncrono no sistema hoje que justifique escalar por isso — CPU já é um proxy razoável de carga para uma API REST síncrona |
| Escalonamento manual (número fixo de réplicas) | Mais simples, sem configuração de métricas | Não atende o requisito de "escalabilidade" do desafio; obrigaria intervenção manual em picos de carga |

## Por que `minReplicas: 2`

Duas réplicas mínimas, mesmo sem carga, garantem que um `rollout` (deploy de nova versão) ou a perda de um nó não derrube a aplicação inteira — complementado pelo `PodDisruptionBudget` ([`k8s/app-pdb.yaml`](../../k8s/app-pdb.yaml)), que impede o Kubernetes de remover réplicas demais ao mesmo tempo durante manutenções do cluster.

## Revisão (2026-11-01) — remoção da métrica de memória

**O que aconteceu:** depois de adicionar o New Relic ([ADR 0005](0005-observabilidade-new-relic.md)), o deploy em homolog falhou no `rollout status` por timeout. O cluster de homolog tem um único nó `t3.medium`, que ficou com 95% da CPU e 92% da memória reservadas (o `nri-bundle` reserva ~600m de CPU e ~800 MB). Durante o rollout o HPA subiu de 2 para 3 réplicas por causa da **memória**, com a CPU em 3%. A terceira réplica ficou `Pending` (`Insufficient cpu, Insufficient memory`) e o rollout nunca terminou.

**Causa:** a JVM reserva heap ao iniciar e quase não devolve. Com o agente APM, cada pod passou a ficar perto de 65% dos 512Mi reservados mesmo sem carga, e qualquer pico (ex.: o start de pods novos) passava do alvo de 75%. Escalar não reduz a memória dos pods existentes, então a métrica não cumpria o papel de medir carga.

**Decisão:** o HPA passa a usar **só CPU**. Com 2 réplicas, o rollout (2 + 1 pod extra do `maxSurge`) cabe no nó atual.

## Consequências

- O autoscaling só reage a CPU — um gargalo de I/O (ex.: queries lentas no RDS) não necessariamente dispara novas réplicas, já que a CPU do pod pode continuar baixa enquanto ele espera resposta do banco. Monitorar a latência de API pelo New Relic ([ADR 0005](0005-observabilidade-new-relic.md)) é o complemento necessário para detectar esse cenário.
- Falta de memória num pod não dispara escala; se acontecer, aparece como restart por `OOMKilled` (alerta "Pod reiniciando" no New Relic) e a correção é ajustar `resources.limits.memory` no Deployment.
- O nó único de homolog continua com pouca folga: se a CPU realmente subir e o HPA pedir uma 3ª réplica, ela não cabe. Para isso é preciso aumentar o node group (`desiredSize` 1 → 2) no `tech-challenge-infra-k8s`.
- `maxReplicas: 6` limita o pico de escala — escalar até esse valor exige mais nós ou instâncias maiores no `infra-k8s`.
