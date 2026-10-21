# Documentação Arquitetural — Tech Challenge Fase 3

Índice da documentação exigida pela Fase 3 do Tech Challenge (16SOAT). Toda a arquitetura descrita aqui foi validada de ponta a ponta numa conta real da AWS (ver [guia de deploy e troubleshooting](../guia-deploy-tech-challenge-fase3.pdf)).

## Diagramas

- [Diagrama de Componentes](component-diagram.md) — visão de nuvem: API Gateway, Lambdas, EKS, RDS, SSM.
- [Diagrama de Sequência](sequence-auth-os.md) — autenticação via CPF, chamada autenticada via Gateway, e abertura de OS.
- [Diagrama ER](er-diagram.md) — modelo relacional completo + justificativa formal da escolha do banco.

## RFCs (decisões técnicas)

- [0001 — Escolha da nuvem](rfcs/0001-escolha-da-nuvem.md)
- [0002 — Escolha do banco de dados](rfcs/0002-escolha-do-banco.md)
- [0003 — Estratégia de autenticação](rfcs/0003-estrategia-de-autenticacao.md)

## ADRs (decisões arquiteturais)

- [0001 — Composição entre repositórios via SSM, não Terraform remote state](adrs/0001-padrao-comunicacao-gateway-lambda-app.md)
- [0002 — Autoscaling via HPA](adrs/0002-hpa-autoscaling.md)
- [0003 — Split em 4 repositórios, sem preservar histórico](adrs/0003-split-em-4-repositorios.md)
- [0004 — Service LoadBalancer, sem AWS Load Balancer Controller/IRSA](adrs/0004-service-loadbalancer-sem-irsa.md)

## Repositórios do projeto

| Repositório | Responsabilidade |
|---|---|
| [tech-challenge-app](https://github.com/Gabrielfsg/tech-challenge-app) (este) | API Spring Boot + manifests K8s da aplicação + CI/CD |
| [tech-challenge-infra-k8s](https://github.com/Gabrielfsg/tech-challenge-infra-k8s) | Terraform: VPC, cluster EKS, node group, ECR |
| [tech-challenge-infra-db](https://github.com/Gabrielfsg/tech-challenge-infra-db) | Terraform: banco de dados gerenciado (RDS PostgreSQL) |
| [tech-challenge-auth-lambda](https://github.com/Gabrielfsg/tech-challenge-auth-lambda) | Function Serverless de autenticação via CPF + API Gateway |

## Guia operacional

Para o passo a passo de deploy (sequência correta entre os 4 repositórios, comandos exatos, e os problemas reais encontrados com causa/solução), ver [`guia-deploy-tech-challenge-fase3.pdf`](../guia-deploy-tech-challenge-fase3.pdf).
