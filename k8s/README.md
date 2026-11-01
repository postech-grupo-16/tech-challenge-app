# Kubernetes — Tech Challenge App

Este diretório contém os manifests Kubernetes **só da aplicação** (Fase 3: o
banco de dados foi migrado para RDS, provisionado pelo repositório
[`tech-challenge-infra-db`](https://github.com/Gabrielfsg/tech-challenge-infra-db),
e o cluster EKS pelo repositório
[`tech-challenge-infra-k8s`](https://github.com/Gabrielfsg/tech-challenge-infra-k8s)).
Organizados com [Kustomize](https://kustomize.io/). Este guia ensina a subir
tudo localmente (usando **Kind**) e a validar que a aplicação está
funcionando.

> ⚠️ Para testar localmente via Kind, você precisa de um Postgres acessível
> (ex.: `docker run -d -p 5432:5432 -e POSTGRES_PASSWORD=postgres postgres:16-alpine`)
> e apontar `SPRING_DATASOURCE_URL` no `secret.yaml` para ele. No EKS
> (produção e homolog), o CI cria o Secret com a URL do RDS criado pelo
> `tech-challenge-infra-db`, lida do SSM — o `secret.yaml` não é aplicado lá.

---

## 📦 O que tem aqui

| Arquivo | Recurso | Função |
|---------|---------|--------|
| `namespace.yaml` | Namespace | Isola tudo no namespace `tech-challenge` |
| `configmap.yaml` | ConfigMap | Configurações não sensíveis (host de e-mail, flags) |
| `secret.yaml` | Secret | URL e credenciais do banco, JWT, e-mail — **apenas placeholders**, só para uso local |
| `app-deployment.yaml` | Deployment | A aplicação Spring Boot (2 réplicas) |
| `app-service.yaml` | Service | Acesso interno à API (ClusterIP) |
| `app-ingress.yaml` | Ingress | Exposição HTTP externa (host `tech-challenge.local`) |
| `app-hpa.yaml` | HorizontalPodAutoscaler | Autoscaling por CPU (2 a 6 réplicas) |
| `app-pdb.yaml` | PodDisruptionBudget | Mantém ao menos 1 réplica no ar durante manutenções |
| `kustomization.yaml` | Kustomization | Amarra todos os recursos acima |

---

## ✅ Pré-requisitos

- **Docker Desktop** instalado e rodando
- **kubectl** — `winget install Kubernetes.kubectl`
- **Kind** — `winget install Kubernetes.kind`

> Após instalar via `winget`, **feche e reabra o terminal** para o PATH atualizar.
> Confirme com `kind version` e `kubectl version --client`.

Use o **PowerShell** (não o Prompt de Comando/cmd), e rode os comandos com caminho
relativo (`docker build ... .`, `kubectl apply -k k8s/`) **a partir da raiz do
projeto** (`tech-challenge-backend`).

---

## 🚀 Subindo o cluster (passo a passo)

### 1. Criar o cluster Kind

```bash
kind create cluster --name tech-challenge --image kindest/node:v1.31.2
```

> **Por que fixar `--image kindest/node:v1.31.2`?**
> As imagens mais novas do Kind (Kubernetes 1.36+) exigem **cgroup v2**. No WSL2 do
> Windows, que normalmente roda **cgroup v1**, o control-plane não sobe e o
> `kubeadm init` falha com `connection refused` na porta 6443. A versão 1.31 ainda
> funciona com cgroup v1 e contorna o problema. (Alternativa: habilitar cgroup v2 —
> veja a seção Troubleshooting.)

Confirme que o nó está pronto:

```bash
kubectl get nodes
```

Deve mostrar um nó `Ready`.

### 2. Buildar a imagem da aplicação

Na raiz do projeto (onde está o `Dockerfile`):

```bash
docker build -t tech-challenge-backend:latest .
```

### 3. Carregar a imagem para dentro do Kind

O Kind roda num container isolado e **não enxerga** as imagens do seu Docker
automaticamente. Este passo é obrigatório:

```bash
kind load docker-image tech-challenge-backend:latest --name tech-challenge
```

> Se pular, o pod fica com `ErrImageNeverPull`.

### 4. Aplicar os manifests

```bash
kubectl apply -k k8s/
```

Isso cria o namespace, ConfigMap, Secret, app, Service, Ingress, HPA e PDB.

### 5. Acompanhar os pods subindo

```bash
kubectl -n tech-challenge get pods -w
```

Espere os pods `tech-challenge-app-...` ficarem **`1/1 Running`** com
`RESTARTS 0` (precisa do Postgres acessível na URL configurada — veja o aviso
no topo deste README). Pressione `Ctrl+C` para sair.

---

## 🧪 Testando a aplicação

Faça o `port-forward` do Service da API (deixe este terminal aberto):

```bash
kubectl -n tech-challenge port-forward svc/tech-challenge-app 8080:80
```

Em **outro terminal**, teste o health (deve retornar `200` e `{"status":"UP"}`,
**sem precisar de token**):

```bash
curl http://localhost:8080/actuator/health
```

No navegador:

- Health: <http://localhost:8080/actuator/health>
- Swagger UI: <http://localhost:8080/swagger-ui.html>

### (Opcional) Testar via Ingress

O Kind não traz o ingress-nginx por padrão. Para testar o `app-ingress.yaml`,
instale o controller e mapeie o host `tech-challenge.local` para `127.0.0.1` no
arquivo `C:\Windows\System32\drivers\etc\hosts`. Para um teste rápido, o
`port-forward` acima já é suficiente.

---

## 🔎 Comandos úteis

```bash
# Ver todos os recursos do namespace
kubectl -n tech-challenge get all

# Logs da aplicação (todas as réplicas)
kubectl -n tech-challenge logs -l app=tech-challenge-app --tail=100

# Filtrar só as mensagens (sem as linhas de stacktrace "at ...")
kubectl -n tech-challenge logs -l app=tech-challenge-app --tail=300 | Select-String -Pattern "^\s+at " -NotMatch

# Detalhes / eventos de um pod (bom para investigar probes)
kubectl -n tech-challenge describe pod -l app=tech-challenge-app

# Recarregar os pods após mudar ConfigMap/Secret
kubectl -n tech-challenge rollout restart deployment tech-challenge-app

# Status do autoscaling (precisa do metrics-server no cluster)
kubectl -n tech-challenge get hpa
```

---

## 🧹 Derrubando tudo

Remover só a aplicação (mantém o cluster):

```bash
kubectl delete -k k8s/
```

Remover o cluster inteiro:

```bash
kind delete cluster --name tech-challenge
```

---

## 🛠️ Troubleshooting

### `kubeadm init ... connection refused` ao criar o cluster
Causa: imagem nova do Kind exige **cgroup v2**, mas o WSL2 está em cgroup v1.
- **Solução rápida:** use `--image kindest/node:v1.31.2` (feito no passo 1).
- **Solução definitiva:** habilite cgroup v2. Crie/edite `C:\Users\<seu-usuario>\.wslconfig`:
  ```ini
  [wsl2]
  kernelCommandLine = cgroup_no_v1=all
  ```
  Depois rode `wsl --shutdown`, reabra o Docker Desktop e recrie o cluster.

### Pod do app fica em `0/1 Running` e reinicia em loop
As probes de readiness/liveness estão falhando. Veja os logs filtrados
(comando acima) para achar a causa. Dois problemas já mapeados neste projeto:

1. **Probe recebia 401** — o endpoint `/actuator/health` precisa estar liberado no
   Spring Security. Já tratado em `SecurityConfig` (`/actuator/health/**` com `permitAll`).
2. **Health check de e-mail falhando (503)** — o Actuator tenta autenticar no SMTP
   e falha com credenciais inválidas, derrubando o `/actuator/health`. Já tratado
   no `configmap.yaml` com `MANAGEMENT_HEALTH_MAIL_ENABLED: "false"`.

### `ErrImageNeverPull` no pod
A imagem não foi carregada no Kind. Rode o passo 3
(`kind load docker-image ...`) e reinicie o deployment.

### `docker build` falha com `Dockerfile: no such file or directory`
Você não está na raiz do projeto. Rode `cd` até a pasta `tech-challenge-backend`
antes do `docker build`.

---

## 🔐 Nota sobre segredos

O `secret.yaml` contém **apenas placeholders**. **Nunca** comite credenciais reais
neste arquivo. Em produção, use [Sealed Secrets](https://github.com/bitnami-labs/sealed-secrets),
[External Secrets Operator](https://external-secrets.io/), ou crie o Secret via
linha de comando (fora do Git):

```bash
kubectl -n tech-challenge create secret generic tech-challenge-secrets \
  --from-literal=DB_USER=... --from-literal=DB_PASS=... \
  --from-literal=JWT_SECRET=... --from-literal=MAIL_USERNAME=... \
  --from-literal=MAIL_PASSWORD=...
```
