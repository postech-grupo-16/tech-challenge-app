# Diagrama de Sequência

Dois fluxos: autenticação do cliente via CPF, e abertura de uma ordem de serviço já autenticado. Ambos validados de ponta a ponta na AWS real.

## 1. Autenticação via CPF

```mermaid
sequenceDiagram
    actor Cliente
    participant GW as API Gateway
    participant L as Lambda auth-cpf
    participant DB as RDS (clientes)

    Cliente->>GW: POST /auth/cpf  {"cpf": "52998224725"}
    GW->>L: invoke (AWS_PROXY, payload 2.0)

    L->>L: normaliza + valida dígito verificador do CPF
    alt CPF mal formado
        L-->>GW: 400 {"message": "CPF inválido"}
        GW-->>Cliente: 400
    else CPF válido
        L->>DB: SELECT id, nome, status FROM clientes WHERE cpf_cnpj = $1
        DB-->>L: linha encontrada?

        alt cliente não encontrado
            L-->>GW: 404 {"message": "Cliente não encontrado"}
            GW-->>Cliente: 404
        else status = INATIVO
            L-->>GW: 403 {"message": "Cliente inativo"}
            GW-->>Cliente: 403
        else status = ATIVO
            L->>L: emite JWT HS256\n(sub=cpf, role=CLIENTE,\nclienteId, iss, iat, exp +1h)
            L-->>GW: 200 {"accessToken": "eyJ..."}
            GW-->>Cliente: 200
        end
    end
```

## 2. Consulta/abertura de OS autenticada (rota protegida)

```mermaid
sequenceDiagram
    actor Cliente
    participant GW as API Gateway
    participant AZ as Lambda authorizer
    participant APP as App Spring Boot (EKS)
    participant DB as RDS

    Cliente->>GW: GET /clientes/{id}/ordens\nAuthorization: Bearer <jwt>
    GW->>AZ: invoke (REQUEST authorizer, payload 2.0)
    AZ->>AZ: valida assinatura HS256 + exp + iss

    alt token ausente/inválido/expirado
        AZ-->>GW: {"isAuthorized": false}
        GW-->>Cliente: 401/403
    else token válido
        AZ-->>GW: {"isAuthorized": true, "context": {subject, role}}
        GW->>APP: HTTP_PROXY  GET /clientes/{id}/ordens\n(com o header Authorization original)
        APP->>APP: Spring Security / OAuth2 Resource Server\nvalida o JWT de novo (mesmo segredo HMAC)
        APP->>DB: SELECT ... FROM ordens_servico WHERE cliente_id = ...
        DB-->>APP: linhas
        APP-->>GW: 200 {dados das ordens de serviço}
        GW-->>Cliente: 200
    end
```

### Por que validar o JWT duas vezes (authorizer + Spring Security)

O Lambda authorizer é uma defesa **na borda** (evita que tráfego não autenticado sequer alcance o cluster), mas não substitui a validação da própria aplicação: o ELB do `tech-challenge-app` é publicamente roteável (necessário para o API Gateway alcançá-lo via proxy HTTP), então, sem a segunda validação, alguém com o DNS do ELB poderia pular o Gateway inteiramente. Ver [RFC 0003](rfcs/0003-estrategia-de-autenticacao.md) para a discussão completa, incluindo a limitação conhecida (o ELB ainda aceita conexão direta, só não emite/valida nada sem um JWT válido) e a mitigação futura (VPC Link).

## 3. Abertura de uma nova ordem de serviço (autenticado como funcionário)

```mermaid
sequenceDiagram
    actor Funcionario
    participant APP as App Spring Boot
    participant DB as RDS

    Funcionario->>APP: POST /auth/login {email, senha}
    APP->>DB: SELECT * FROM usuarios WHERE email = ...
    DB-->>APP: usuario + hash bcrypt
    APP->>APP: valida senha (bcrypt) + emite JWT\n(sub=email, role=FUNCIONARIO/ADMIN)
    APP-->>Funcionario: 200 {"accessToken": "eyJ..."}

    Funcionario->>APP: POST /api/v1/ordens-servico/criar\nAuthorization: Bearer <jwt>
    APP->>APP: valida JWT + role
    APP->>DB: INSERT INTO ordens_servico (cliente_id, veiculo_id, mecanico_id, status=DIAGNOSTICO, ...)
    DB-->>APP: id gerado
    APP->>DB: INSERT INTO os_historico_status (ordem_servico_id, status, usuario_id, ...)
    APP-->>Funcionario: 201 {ordem de servico criada}
```

Esse segundo fluxo (funcionário autenticado via e-mail/senha) **não muda** com a Fase 3 — continua usando o `JwtTokenGenerator` original do `tech-challenge-app`. A Lambda só acrescenta uma *segunda via* de autenticação (CPF, para clientes), emitindo tokens no mesmo formato para não exigir nenhuma mudança no `SecurityConfig` existente.
