# ADR 0002 — Autoscaling via HPA (CPU + memória), não KEDA ou autoscaling manual

**Status:** Aceito (decisão herdada da Fase 2, mantida na Fase 3)
**Data:** 2026-10-19

## Contexto

A oficina tem tráfego variável (picos quando múltiplos clientes consultam o status das suas ordens de serviço ao mesmo tempo, por exemplo). O desafio pede "Cluster Kubernetes com escalabilidade".

## Decisão

Usar o **HorizontalPodAutoscaler** (`autoscaling/v2`) nativo do Kubernetes, configurado em [`k8s/app-hpa.yaml`](../../k8s/app-hpa.yaml):

```yaml
minReplicas: 2
maxReplicas: 6
metrics:
  - resource: cpu,    averageUtilization: 70
  - resource: memory, averageUtilization: 75
```

O HPA depende do `metrics-server` (add-on instalado pelo `tech-challenge-infra-k8s`, ver [`k8s-metrics-server.tf`](https://github.com/Gabrielfsg/tech-challenge-infra-k8s/blob/main/k8s-metrics-server.tf)) para coletar CPU/memória dos pods.

## Alternativas consideradas

| Opção | Prós | Contras |
|---|---|---|
| **HPA nativo (CPU+memória)** (escolhida) | Nenhuma dependência externa além do `metrics-server`, que já é leve e padrão; suficiente para uma carga HTTP típica de CRUD | Reage só a métricas de recurso (CPU/memória), não a métricas de negócio (ex.: requisições/segundo, fila de processamento) |
| **KEDA** (event-driven autoscaling) | Escalaria por métricas customizadas (ex.: requisições por segundo via Prometheus, profundidade de fila) | Componente adicional a instalar e operar no cluster; sem fila/evento assíncrono no sistema hoje que justifique escalar por isso — CPU/memória já é um proxy razoável de carga para uma API REST síncrona |
| Escalonamento manual (número fixo de réplicas) | Mais simples, sem configuração de métricas | Não atende o requisito de "escalabilidade" do desafio; obrigaria intervenção manual em picos de carga |

## Por que `minReplicas: 2`

Duas réplicas mínimas, mesmo sem carga, garantem que um `rollout` (deploy de nova versão) ou a perda de um nó não derrube a aplicação inteira — complementado pelo `PodDisruptionBudget` ([`k8s/app-pdb.yaml`](../../k8s/app-pdb.yaml)), que impede o Kubernetes de remover réplicas demais ao mesmo tempo durante manutenções do cluster.

## Consequências

- O autoscaling só reage a CPU/memória — um gargalo de I/O (ex.: queries lentas no RDS) não necessariamente dispara novas réplicas, já que a CPU do pod pode continuar baixa enquanto ele espera resposta do banco. Monitorar latência de API diretamente (via New Relic, Fase 5) é o complemento necessário para detectar esse cenário, já que o HPA sozinho não cobre.
- `maxReplicas: 6` limita o pico de escala — adequado ao `desiredSize` do node group (2-3 nós `t3.medium`); escalar além disso exigiria também ajustar o `infra-k8s` (mais nós ou instâncias maiores).
