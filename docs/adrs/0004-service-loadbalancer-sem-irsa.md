# ADR 0004 — Service tipo LoadBalancer, sem AWS Load Balancer Controller / IRSA

**Status:** Aceito
**Data:** 2026-10-19

## Contexto

O API Gateway (`tech-challenge-auth-lambda`) precisa de uma URL pública e estável para fazer proxy das requisições autenticadas até o `tech-challenge-app`, rodando no EKS. Até a Fase 2, o `Service` da aplicação era `ClusterIP` (só acessível de dentro do cluster, testado via `kubectl port-forward`) e o `Ingress` existia no manifest mas ficava inerte, por falta de um controller instalado.

## Decisão

Trocar o `Service` da aplicação de `ClusterIP` para **`LoadBalancer`** ([`k8s/app-service.yaml`](../../k8s/app-service.yaml)), deixando o próprio EKS provisionar um Elastic Load Balancer automaticamente — sem instalar o **AWS Load Balancer Controller** nem configurar **IRSA/OIDC**.

## Alternativas consideradas

| Opção | Prós | Contras |
|---|---|---|
| **`Service` tipo `LoadBalancer`** (escolhida) | Funciona só com permissões da `LabRole` já associada aos nodes (ver [`launch-template.tf`](https://github.com/Gabrielfsg/tech-challenge-infra-k8s/blob/main/launch-template.tf)); nenhum componente adicional a instalar/operar no cluster | ELB clássico/NLB "cru", sem os recursos avançados do `Ingress` (roteamento por path, TLS termination gerenciado, etc.) |
| AWS Load Balancer Controller + `Ingress` | Suporte completo a `Ingress` (ALB), roteamento por path/host, TLS | Exige **IRSA** (IAM Roles for Service Accounts), que por sua vez exige um **OIDC provider** associado ao cluster — criar isso exigiria permissões IAM que a role `LabRole` do AWS Academy não necessariamente concede, e o projeto já documenta explicitamente (desde a Fase 2) que **não usa IRSA/OIDC** |
| NGINX Ingress Controller (self-hosted) | Não depende de nenhuma integração específica da AWS | Mais um componente a instalar/manter dentro do cluster; ainda precisaria de um `Service LoadBalancer` por baixo para expor o próprio Ingress controller — não elimina o problema, só adiciona uma camada |

## Consequências

- O ELB criado é **público** — IP/DNS acessível da internet, não só do API Gateway. Isso é uma limitação de segurança conhecida, discutida em detalhe no [RFC 0003](../rfcs/0003-estrategia-de-autenticacao.md): a aplicação se defende validando o JWT de novo no `OAuth2ResourceServer`, mas o bypass físico do Gateway continua existindo.
- Sem `Ingress` funcional, não há roteamento por path nem TLS termination gerenciado no nível do Kubernetes — o tráfego entre o API Gateway e o ELB é HTTP simples (sem TLS na perna interna; o cliente final só fala HTTPS com o API Gateway).
- Essa decisão é consistente com a mesma lógica já aplicada ao isolamento do RDS (Security Group em vez de subnets privadas) — ver [RFC 0002](../rfcs/0002-escolha-do-banco.md): minimizar componentes de rede adicionais (NAT Gateway, IRSA/OIDC) que teriam custo ou complexidade desproporcional ao escopo educacional deste projeto.
