# RFC 0001 — Escolha da Nuvem

**Status:** Aceito
**Data:** 2026-10-19

## Contexto

A Fase 3 exige elevar a aplicação (já rodando em Kubernetes desde a Fase 2, via Terraform + EKS) a um "nível de operação corporativa", com API Gateway, Function Serverless, banco gerenciado e observabilidade. Era preciso decidir se continuar na mesma nuvem das fases anteriores ou migrar.

## Alternativas consideradas

| Opção | Prós | Contras |
|---|---|---|
| **AWS** (escolhida) | Infra das Fases 1-2 já é 100% AWS (EKS, ECR, VPC via Terraform); API Gateway + Lambda são os serviços serverless mais maduros do mercado; crédito disponível via AWS Academy Learner Lab | Learner Lab tem SCPs restritivas (ex.: bloqueia leitura de object-lock em buckets S3) e credenciais temporárias (~4h), exigindo atenção operacional extra |
| **GCP** (Cloud Run + Cloud Functions + Cloud SQL) | Cloud Run é mais simples que EKS para apps stateless | Exigiria reescrever toda a infraestrutura Terraform das Fases 1-2 do zero; sem crédito educacional já disponível para o grupo |
| **Azure** (AKS + Functions + Azure Database for PostgreSQL) | Equivalentes diretos a todos os serviços AWS usados | Mesmo problema do GCP — reescrita completa, sem crédito já disponível |

## Decisão

**AWS**, por continuidade com a infraestrutura já validada nas Fases 1-2 (EKS, VPC, ECR via Terraform) e por já termos acesso a crédito educacional (AWS Academy Learner Lab). Migrar de nuvem nesta fase geraria retrabalho de infraestrutura sem nenhum ganho arquitetural que justificasse o custo.

Serviços AWS específicos usados:
- **API Gateway (HTTP API)** — porta de entrada pública e autenticação de borda.
- **Lambda (Node.js 20.x)** — autenticação via CPF e o authorizer do Gateway.
- **RDS PostgreSQL** — banco gerenciado (ver [RFC 0002](0002-escolha-do-banco.md)).
- **EKS** — já em uso desde a Fase 2, mantido.

## Consequências

- O projeto herda as restrições do **AWS Academy Learner Lab**: só a role `LabRole` pré-existente está disponível (sem criação de roles/policies IAM customizadas), e as credenciais expiram em poucas horas — documentado no [guia de deploy](../../guia-deploy-tech-challenge-fase3.pdf).
- A escolha de **AWS API Gateway + Lambda** (em vez de, por exemplo, Kong ou Traefik self-hosted) evita operar mais um componente dentro do EKS, mas acopla a solução de autenticação à AWS — aceitável neste contexto educacional, mas seria uma decisão a revisitar numa migração multi-cloud real.
