# Tech Challenge — App

API REST para gerenciamento de ordens de serviço em oficina mecânica, desenvolvida com Spring Boot 3, PostgreSQL e arquitetura em camadas com Clean Architecture.

Este repositório contém **só a aplicação principal**, que roda em Kubernetes (EKS). Faz parte de um conjunto de 4 repositórios da Fase 3 do Tech Challenge (16SOAT):

| Repositório | Responsabilidade |
|---|---|
| **tech-challenge-app** (este) | API Spring Boot + manifests K8s da aplicação + CI/CD |
| [tech-challenge-infra-k8s](https://github.com/Gabrielfsg/tech-challenge-infra-k8s) | Terraform: VPC, cluster EKS, node group, ECR |
| [tech-challenge-infra-db](https://github.com/Gabrielfsg/tech-challenge-infra-db) | Terraform: banco de dados gerenciado (RDS PostgreSQL) |
| [tech-challenge-auth-lambda](https://github.com/Gabrielfsg/tech-challenge-auth-lambda) | Function Serverless de autenticação via CPF + API Gateway |

## 📐 Documentação arquitetural

- [`docs/`](docs/README.md) — diagrama de componentes, diagrama de sequência, diagrama ER + justificativa do banco, RFCs e ADRs.
- [`guia-deploy-tech-challenge-fase3.pdf`](guia-deploy-tech-challenge-fase3.pdf) — passo a passo completo de deploy (sequência entre os 4 repositórios, comandos exatos, problemas reais encontrados e como testar o fluxo de autenticação).

## Visão Geral

Sistema completo para:
- Autenticação e controle de acesso (usuários internos via e-mail/senha; clientes via CPF, através do `tech-challenge-auth-lambda`)
- Gerenciamento de clientes e veículos
- Criação e acompanhamento de ordens de serviço (OS)
- Gerenciamento de orçamentos e aprovações
- Controle de peças e estoque
- Rastreamento de histórico de status e operações

## Tecnologias

| Tecnologia | Versão | Uso |
|-----------|---------|-----|
| Java | 21 | Linguagem principal |
| Spring Boot | 3.5.15-SNAPSHOT | Framework web |
| PostgreSQL | 16 | Banco de dados (RDS em produção, container local em dev) |
| Spring Security | 6.x | Autenticação e autorização |
| OAuth2 Resource Server | 6.x | Validação de JWT |
| Spring Data JPA | 3.x | Persistência |
| Flyway | 11.7.2 | Migrações de banco |
| SpringDoc OpenAPI | 2.8.16 | Documentação Swagger |
| Maven | 3.x | Gerenciador de dependências |
| Docker + Docker Compose | Latest | Containerização |

## Como Executar Localmente

```bash
cp .env.example .env
docker compose up --build
```

- **API**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **Health Check**: http://localhost:8080/actuator/health

## Deploy em Kubernetes (EKS)

Pré-requisito: cluster já provisionado pelo [tech-challenge-infra-k8s](https://github.com/Gabrielfsg/tech-challenge-infra-k8s) e banco já provisionado pelo [tech-challenge-infra-db](https://github.com/Gabrielfsg/tech-challenge-infra-db).

```bash
aws eks update-kubeconfig --region us-east-1 --name eks-tech-challenge
kubectl apply -k k8s/
```

Veja [`k8s/README.md`](k8s/README.md) para o passo a passo completo, incluindo teste local via Kind.

## CI/CD

O pipeline ([`.github/workflows/pipeline.yml`](.github/workflows/pipeline.yml)) builda, testa (unitários + integração), publica a imagem no ECR e faz deploy automático no EKS a cada push na `main`. A branch `main` é protegida — merges só via Pull Request.

Análise estática de código via [SonarCloud](https://sonarcloud.io/project/overview?id=gabrielfsg_tech-challenge-app) ([`.github/workflows/sonar.yml`](.github/workflows/sonar.yml)), rodando em todo push na `main`/`feature/**` e em Pull Requests.

## Testes

```bash
./mvnw test
```

Gerar relatório de cobertura:

```bash
./mvnw clean test jacoco:report
```

## Documentação das APIs

- Swagger: http://localhost:8080/swagger-ui.html (local) ou pelo endpoint exposto em produção
- Coleções Postman: [`postman/`](postman/)
- Coleção Insomnia: [`Insomnia_Collection_TechChallenge.json`](Insomnia_Collection_TechChallenge.json)

## Autenticação

- **Usuários internos** (funcionários/admin): `POST /auth/login` com e-mail/senha, emite JWT HS256.
- **Clientes**: autenticam via CPF através do API Gateway + [tech-challenge-auth-lambda](https://github.com/Gabrielfsg/tech-challenge-auth-lambda), que consulta o status do cliente no RDS e emite um JWT no mesmo formato.

## Estrutura do Projeto

```
src/
├── main/java/com/fiap/tech_challenge_backend/
│   ├── acesso/          # Autenticação (usuários internos)
│   ├── cadastro/        # Clientes e veículos
│   ├── atendimento/     # Ordens de serviço, orçamentos, relatórios
│   ├── estoque/         # Peças e insumos
│   ├── acompanhamento/  # Consulta de OS pelo cliente
│   └── shared/          # Código compartilhado (security, exceptions, value objects)
└── test/                # Testes unitários, integração (Testcontainers) e BDD (Cucumber)
```

---

Desenvolvido como Tech Challenge — Pós-Graduação em Arquitetura de Software (FIAP/16SOAT).
