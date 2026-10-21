# ADR 0001 — Composição entre repositórios via SSM Parameter Store, não Terraform remote state

**Status:** Aceito
**Data:** 2026-10-19

## Contexto

A Fase 3 exige 4 repositórios independentes (`tech-challenge-app`, `tech-challenge-infra-k8s`, `tech-challenge-infra-db`, `tech-challenge-auth-lambda`), cada um com seu próprio pipeline de CI/CD. Três deles têm Terraform (`infra-k8s`, `infra-db`, `auth-lambda`), e dependem de informações uns dos outros: `infra-db` precisa da VPC criada pelo `infra-k8s`; `auth-lambda` precisa da VPC e do RDS.

## Decisão

Cada repositório Terraform **publica** os outputs relevantes no **SSM Parameter Store**, sob `/tech-challenge/*` (ex.: `/tech-challenge/vpc_id`, `/tech-challenge/rds_endpoint`), e os outros **leem** via `data "aws_ssm_parameter"`. Nenhum repositório lê o Terraform state de outro diretamente (`terraform_remote_state`).

```
tech-challenge-infra-k8s  --publica-->  /tech-challenge/vpc_id, subnet_ids, eks_cluster_name
tech-challenge-infra-db   --le-------   /tech-challenge/vpc_id, subnet_ids
tech-challenge-infra-db   --publica-->  /tech-challenge/rds_endpoint, rds_user, rds_password
tech-challenge-auth-lambda --le------   /tech-challenge/vpc_id, subnet_ids, rds_*
tech-challenge-auth-lambda --publica--> /tech-challenge/api_gateway_url, jwt_secret
```

## Alternativas consideradas

| Opção | Prós | Contras |
|---|---|---|
| **SSM Parameter Store** (escolhida) | Cada repo só precisa de permissão de leitura/escrita em parâmetros, não acesso ao bucket S3 de state de outro repo; state continua isolado por repositório; fácil de inspecionar (`aws ssm get-parameter`) | Outputs "soltos" (não tipados pelo Terraform) — erros de nome só aparecem em tempo de apply |
| `terraform_remote_state` apontando pro S3 de outro repo | Outputs tipados, erro em tempo de `plan` se referência não existir | Acopla o backend de um repo ao outro — mudar o bucket de state de um quebra os outros silenciosamente; exige dar acesso de leitura ao bucket S3 de state entre repositórios, ampliando a superfície de permissões |
| Repositório único (monorepo) com múltiplos state files | Sem necessidade de nenhum mecanismo de composição | Contraria o requisito explícito da Fase 3 (4 repositórios separados) |

## Consequências

- **Ordem de aplicação importa**: `infra-k8s` → `infra-db` → `auth-lambda` (documentado no [guia de deploy](../../guia-deploy-tech-challenge-fase3.pdf)). Aplicar fora de ordem falha com "parameter not found" no `data` source, com uma mensagem de erro clara.
- Os 3 repositórios Terraform podem ser **reaplicados independentemente** depois da primeira vez — trocar algo só no `infra-db` não exige tocar nos outros dois, desde que os parâmetros que eles já leram não mudem de nome.
- Segredos (senha do RDS, `jwt_secret`) são publicados como `SecureString` no SSM — criptografados em repouso, mas ainda assim centralizados num único namespace; um comprometimento de permissão SSM exporia múltiplos segredos. Aceitável no escopo deste projeto; numa arquitetura de produção, valeria segregar por namespace com políticas de IAM mais granulares.
