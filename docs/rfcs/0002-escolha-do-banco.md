# RFC 0002 — Escolha do Banco de Dados

**Status:** Aceito
**Data:** 2026-10-19

## Contexto

Até a Fase 2, o PostgreSQL rodava como `StatefulSet` dentro do próprio cluster EKS (`tech-challenge-postgres`). A Fase 3 exige um "banco de dados gerenciado" em repositório de infraestrutura próprio. Era preciso decidir qual motor de banco usar e como hospedá-lo.

## Alternativas consideradas (motor)

| Opção | Prós | Contras |
|---|---|---|
| **PostgreSQL** (escolhido) | Já era o motor usado desde a Fase 1; todas as migrations Flyway, tipos (UUID nativo, `CHECK` constraints) e queries já são Postgres; equipe já tem familiaridade | — |
| MySQL | Também maduro, suportado pelo RDS | Migraria 17 migrations Flyway + ajustar sintaxe (`gen_random_uuid()`, `CHECK` constraints têm comportamento diferente) sem nenhum ganho |
| Amazon DynamoDB (NoSQL) | Serverless, escala automática | O domínio é fortemente relacional (ver [diagrama ER](../er-diagram.md)) — modelar OS/orçamento/peças/histórico em documentos exigiria desnormalização pesada e *joins* manuais na aplicação |

## Alternativas consideradas (hospedagem)

| Opção | Prós | Contras |
|---|---|---|
| **RDS gerenciado** (escolhido) | Ciclo de vida independente do cluster EKS; backup automático; patching gerenciado; sem operação manual | Custo contínuo enquanto a instância existir (mitigado: `db.t3.micro`, sem Multi-AZ, sem backup retention — aceitável para ambiente de estudo) |
| Postgres in-cluster (StatefulSet, como nas Fases 1-2) | Sem custo adicional de serviço gerenciado | Dados perdidos a cada vez que o cluster é destruído/recriado (comum no AWS Academy); sem backup automático; viola o requisito explícito da Fase 3 |
| Aurora Serverless v2 (Postgres-compatible) | Escala para zero, custo sob demanda | Complexidade extra (Data API opcional, cold start) sem benefício claro para o volume de tráfego deste projeto educacional |

## Decisão

**PostgreSQL 16 no Amazon RDS**, classe `db.t3.micro`, provisionado pelo repositório `tech-challenge-infra-db`.

Decisões de configuração específicas:
- **`publicly_accessible = false`** — o RDS não tem IP público; só é alcançável de dentro da VPC (pela Lambda e pelos pods do EKS).
- **Isolamento via Security Group, não subnets privadas** — a VPC do `tech-challenge-infra-k8s` só tem subnets públicas (sem NAT Gateway, para reduzir custo no AWS Academy); o isolamento do RDS vem do Security Group (só aceita tráfego de dentro do CIDR da VPC), não de topologia de rede. Ver [ADR 0004](../adrs/0004-service-loadbalancer-sem-irsa.md) para a mesma lógica aplicada à exposição da aplicação.
- **`sslmode=require`** — o RDS força conexão criptografada por padrão (`rds.force_ssl=1`); documentado explicitamente nos dois clientes (JDBC na app, `node-postgres` na Lambda) depois de um incidente real onde a Lambda falhava com `no pg_hba.conf entry ... no encryption` por não negociar SSL por padrão.
- **`skip_final_snapshot = true`, `backup_retention_period = 0`** — escolhas deliberadas para um ambiente de laboratório, reduzindo custo e tempo de `destroy`. Numa instância de produção real, ambas seriam revertidas.

## Consequências

- Destruir e recriar o `tech-challenge-infra-db` **apaga todos os dados** (sem snapshot final) — aceitável no contexto do laboratório, mas documentado como limitação no [guia de deploy](../../guia-deploy-tech-challenge-fase3.pdf).
- O endpoint e as credenciais do RDS são publicados no SSM Parameter Store (`/tech-challenge/rds_*`) para os outros dois repositórios (`tech-challenge-app`, `tech-challenge-auth-lambda`) consumirem sem acoplar Terraform state — ver [ADR 0001](../adrs/0001-padrao-comunicacao-gateway-lambda-app.md).
