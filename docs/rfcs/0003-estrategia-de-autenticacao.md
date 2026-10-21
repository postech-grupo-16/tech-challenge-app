# RFC 0003 — Estratégia de Autenticação via CPF

**Status:** Aceito
**Data:** 2026-10-19

## Contexto

Até a Fase 2, toda autenticação (funcionários e clientes) passava pelo mesmo fluxo: e-mail/senha via `POST /auth/login`, validado no próprio `tech-challenge-app`. A Fase 3 exige uma rota de autenticação **via CPF**, implementada como Function Serverless, atrás de um API Gateway protegendo as rotas sensíveis.

Três decisões precisavam ser tomadas: (1) o CPF substitui ou complementa o login existente; (2) que formato de token a Lambda emite; (3) como o API Gateway protege as demais rotas da aplicação.

## Decisão 1 — CPF complementa, não substitui, o login por e-mail/senha

`POST /auth/login` (e-mail/senha, para `ADMIN`/`FUNCIONARIO`) continua existindo sem nenhuma mudança. A autenticação via CPF é uma **segunda via**, específica para clientes, que não exigem senha — só precisam estar cadastrados e com `status = ATIVO`.

**Alternativa rejeitada**: migrar todo mundo (inclusive funcionários) para CPF. Rejeitada porque funcionários precisam de controle de acesso mais granular (perfis `ADMIN`/`FUNCIONARIO`) que depende de senha; forçar CPF para esse público não tem motivação de negócio e quebraria o fluxo já validado nas fases anteriores.

## Decisão 2 — A Lambda emite o mesmo formato de JWT que o app já usa

O `JwtTokenGenerator` do `tech-challenge-app` já emitia JWT HS256 com claims `iss` (issuer), `iat`, `exp`, `sub` (subject) e `role`. Em vez de criar um segundo formato de token (exigindo mudanças no `SecurityConfig` e no `OAuth2ResourceServer`), a Lambda **reproduz exatamente esse formato**, assinando com o mesmo segredo HMAC compartilhado (`jwt_secret`, propagado via SSM Parameter Store e Secret do Kubernetes).

```
subject (sub) = CPF normalizado (11 dígitos)
role           = "CLIENTE"
clienteId      = UUID do cliente (claim extra, conveniência)
iss            = "tech-challenge-backend"
iat / exp      = emitido agora / +1 hora
```

**Alternativa rejeitada**: usar um formato de token diferente (ex.: OIDC completo, com `aud`, `jti`, chaves assimétricas RS256). Rejeitada por complexidade desproporcional ao escopo do desafio — exigiria JWKS endpoint, rotação de chaves, e mudanças profundas no `SecurityConfig` existente, sem benefício de segurança relevante para o volume e criticidade deste sistema.

## Decisão 3 — API Gateway com duas camadas de validação (authorizer + Spring Security)

O API Gateway expõe duas rotas:
- `POST /auth/cpf` — pública, encaminha para a Lambda emissora.
- `ANY /{proxy+}` — protegida por um **Lambda authorizer** (REQUEST, payload 2.0), que valida o JWT antes de encaminhar (proxy HTTP) para o `Service` `LoadBalancer` do `tech-challenge-app` no EKS.

A aplicação **não deixa de validar o JWT** só porque o Gateway já validou — o `OAuth2ResourceServer` do Spring continua ativo, validando o mesmo token de novo. Ver [diagrama de sequência](../sequence-auth-os.md) para o fluxo completo.

**Por que validar duas vezes, em vez de confiar só no Gateway?** Porque o `Service` `LoadBalancer` da aplicação é necessariamente público (o API Gateway precisa alcançá-lo via HTTP), então nada impede, hoje, que alguém descubra o DNS do ELB e chame a aplicação diretamente, pulando o Gateway e o authorizer. A segunda validação (na própria aplicação) é a garantia real de que nenhuma requisição sem um JWT válido é processada — o Gateway é só uma camada de conveniência/observabilidade adicional, não a única linha de defesa.

## Limitação conhecida e trabalho futuro

A exposição direta do ELB da aplicação (necessária para o proxy do Gateway funcionar sem VPC Link) significa que **a rota de bypass do Gateway existe fisicamente**, mesmo que a aplicação rejeite requisições sem JWT válido. Mitigações possíveis, fora do escopo desta fase:

1. **VPC Link** entre o API Gateway e um Network Load Balancer interno (sem IP público) — eliminaria o bypass por completo, mas exige mais componentes de rede (custo e complexidade adicionais no laboratório AWS Academy).
2. **Header secreto compartilhado** (`X-Gateway-Key`) que a aplicação exige em toda requisição, preenchido só pela integração do Gateway — mitigação mais simples, cogitada no planejamento desta fase mas não implementada por não ser estritamente necessária (a segunda validação de JWT já impede uso não autenticado).

## Consequências

- Nenhuma mudança foi necessária no `SecurityConfig` nem no `JwtTokenGenerator` existentes do `tech-challenge-app` — só a adição do campo `clientes.status` (migration `V17`), consumido pela Lambda.
- O segredo HMAC (`jwt_secret`) precisa ser **idêntico** nos dois repositórios (`tech-challenge-auth-lambda` e `tech-challenge-app`) — hoje sincronizado manualmente; automatizar essa sincronização (via SSM, como já é feito para `rds_endpoint`) é um TODO documentado no pipeline do `tech-challenge-app`.
