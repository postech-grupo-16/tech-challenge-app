# ADR 0003 — Split do monorepo em 4 repositórios, sem preservar histórico de commits

**Status:** Aceito
**Data:** 2026-10-19

## Contexto

Até a Fase 2, todo o código (aplicação Spring Boot, Terraform de infraestrutura, manifests Kubernetes) vivia em um único repositório (`postech-16SOAT`, com a aplicação na subpasta `tech-challenge-backend/`). A Fase 3 exige 4 repositórios separados: Lambda, infraestrutura Kubernetes, infraestrutura de banco, e aplicação principal — cada um com CI/CD próprio e branch `main` protegida.

## Decisão

Criar 4 repositórios novos (`tech-challenge-app`, `tech-challenge-infra-k8s`, `tech-challenge-infra-db`, `tech-challenge-auth-lambda`), copiando o conteúdo relevante de cada um a partir do monorepo original (`lucianobsm/postech-16SOAT`), **sem preservar o histórico de commits** (cada repositório novo começa com um único commit inicial).

## Alternativas consideradas

| Opção | Prós | Contras |
|---|---|---|
| **Cópia direta, sem histórico** (escolhida) | Simples e confiável — copia exatamente o conteúdo atual de cada pasta para o repositório certo, sem risco de corromper o conteúdo | Perde o histórico de commits anterior (quem mudou o quê, quando) nesses 4 novos repositórios |
| `git subtree split` (preserva histórico por pasta) | Manteria o histórico de commits de cada subpasta (`infra/`, `src/`, etc.) | Operação de duas camadas (`tech-challenge-backend/` dentro de `postech-16SOAT/`, e depois `infra/` dentro de `tech-challenge-backend/`) é propensa a erro; uma tentativa real durante esta fase encontrou um bug de "filename too long" do Git no Windows que corrompeu parcialmente um `git add` (arquivos marcados como deletados por falha ao fazer `stat` em caminhos longos) — risco real e já observado, não hipotético |
| `git filter-repo` | Ferramenta mais robusta que `subtree` para reescrever histórico seletivamente | Exigiria instalar uma ferramenta Python adicional; para um projeto onde o histórico anterior já está preservado no repositório original (`postech-16SOAT`), o ganho não justificava o risco adicional |

## Consequências

- O histórico de commits anterior **continua acessível** no repositório original (`lucianobsm/postech-16SOAT`) — nada foi perdido, só não foi replicado nos 4 novos repositórios.
- Cada um dos 4 repositórios novos tem um commit inicial único e abrangente, com a mensagem explicando a origem do split — rastreável, mesmo sem histórico linha-a-linha.
- Essa decisão foi validada na prática: a tentativa de clonar/commitar sem o cuidado de habilitar `core.longpaths` no Git (necessário no Windows pelos caminhos longos de pacotes Java) realmente causou um `git add` que marcou ~50 arquivos como deletados — intercedido antes do `push`, e corrigido reclonando com a configuração correta. Ver o [guia de deploy](../../guia-deploy-tech-challenge-fase3.pdf) para o procedimento seguro.
