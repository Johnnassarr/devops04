# 🔵 Guia de Configuração do Azure DevOps

## 📋 Índice

1. [Arquivos de Pipeline Disponíveis](#arquivos-de-pipeline-disponíveis)
2. [Pré-requisitos](#pré-requisitos)
3. [Configuração Inicial](#configuração-inicial)
4. [Opção 1: Pipeline Unificado (Recomendado)](#opção-1-pipeline-unificado-recomendado)
5. [Opção 2: Pipelines Separados (CI e CD)](#opção-2-pipelines-separados-ci-e-cd)
6. [Configuração de Variáveis](#configuração-de-variáveis)
7. [Service Connection do Azure](#service-connection-do-azure)
8. [Ambientes](#ambientes)
9. [Executando os Pipelines](#executando-os-pipelines)
10. [Troubleshooting](#troubleshooting)

---

## 📁 Arquivo de Pipeline

Este projeto usa **um único arquivo de pipeline** que gerencia todo o fluxo CI/CD:

| Arquivo | Descrição |
|---------|-----------|
| `azure-pipelines.yml` | Pipeline completo (CI + CD) - Build, Test, Push e Deploy |

**Vantagens do pipeline unificado:**
- ✅ Mais simples de configurar e manter
- ✅ Deploy automático após build bem-sucedido
- ✅ Visualização clara de todo o fluxo em um lugar
- ✅ Menos arquivos para gerenciar

---

## ✅ Pré-requisitos

Antes de configurar os pipelines, certifique-se de:

- [x] Ter uma conta no Azure DevOps
- [x] Ter um projeto criado no Azure DevOps
- [x] Ter executado o **`setup.sh`** para criar a infraestrutura
- [x] Ter o código em um repositório Git (Azure Repos, GitHub, etc.)
- [x] Ter uma assinatura do Azure ativa

---

## 🚀 Configuração Inicial

### 1. Criar o Repositório no Azure DevOps

1. Acesse seu projeto no Azure DevOps
2. Vá em **Repos** → **Files**
3. Faça push do código para o repositório

```bash
# Se ainda não tem o remote configurado
git remote add origin https://dev.azure.com/<organization>/<project>/_git/<repo>
git push -u origin main
```

### 2. Executar o Setup da Infraestrutura

Antes de configurar os pipelines, execute o setup:

```bash
./setup.sh
```

**IMPORTANTE:** Anote as variáveis geradas ao final da execução. Você precisará delas!

---

## 🚀 Configuração do Pipeline

### Sobre o Pipeline

O arquivo `azure-pipelines.yml` contém dois stages:

1. **Build Stage (CI)**
   - Checkout do código
   - Build com Maven
   - Testes unitários (com publicação de resultados)
   - Build da imagem Docker
   - Push para Azure Container Registry

2. **Deploy Stage (CD)**
   - Acionado automaticamente após Build (apenas na branch `main`)
   - Pull da imagem mais recente do ACR
   - Deploy no Azure Container Instances
   - Health check da aplicação

### Passos para Configurar

#### 1. Criar o Pipeline

1. No Azure DevOps, vá em **Pipelines** → **Create Pipeline**
2. Selecione onde está seu código (Azure Repos Git, GitHub, etc.)
3. Selecione seu repositório
4. Escolha **"Existing Azure Pipelines YAML file"**
5. Selecione o arquivo: **`/azure-pipelines.yml`**
6. Clique em **Continue**

#### 2. Configurar Variáveis

Antes de executar, clique em **Variables** e adicione:

##### Variáveis Normais:
```
azureServiceConnection = <nome-da-service-connection>
dbHost = <IP-do-banco-obtido-no-setup>
dbPort = 5432
dbName = mottu-api
dbUser = postgres
```

##### Variáveis Secretas (clique no cadeado 🔒):
```
dbPassword = Mottu@2025!Secure
```

#### 3. Salvar e Executar

1. Clique em **Save and Run**
2. Adicione uma mensagem de commit
3. Clique em **Save and Run** novamente
4. Aguarde a execução!

---

## 🔐 Service Connection do Azure

### Criar Service Connection

1. No Azure DevOps, vá em **Project Settings** (canto inferior esquerdo)
2. Vá em **Service connections**
3. Clique em **New service connection**
4. Selecione **Azure Resource Manager**
5. Selecione **Service principal (automatic)**

### Configurar Permissões

1. **Authentication method**: Service Principal (automatic)
2. **Scope level**: Subscription
3. **Subscription**: Selecione sua assinatura Azure
4. **Resource group**: Selecione `rg-challenge3-rm556221`
5. **Service connection name**: `azure-mottu-connection` (ou outro nome)
6. Marque: **Grant access permission to all pipelines**
7. Clique em **Save**

### Anotar o Nome

Anote o nome da service connection criada. Você usará em:
```
azureServiceConnection = azure-mottu-connection
```

---

## 🌍 Ambientes

Para melhor controle de deploy, crie um ambiente:

### Criar Ambiente "production"

1. Vá em **Pipelines** → **Environments**
2. Clique em **New environment**
3. **Name**: `production`
4. **Description**: `Production environment for Mottu API`
5. Clique em **Create**

### Adicionar Aprovações (Opcional)

1. Clique no ambiente criado
2. Clique nos **3 pontinhos** (⋮) → **Approvals and checks**
3. Clique em **Approvals**
4. Adicione aprovadores
5. Configure número mínimo de aprovações
6. Salve

Agora, todo deploy para produção exigirá aprovação manual! 🎯

---

## ⚙️ Configuração de Variáveis

### Método 1: Variáveis do Pipeline

Configurar diretamente no pipeline (já mostrado acima).

### Método 2: Variable Groups (Recomendado)

Para compartilhar variáveis entre múltiplos pipelines:

#### Criar Variable Group

1. Vá em **Pipelines** → **Library**
2. Clique em **+ Variable group**
3. **Variable group name**: `mottu-api-config`

#### Adicionar Variáveis

Adicione as seguintes variáveis:

```
azureRG = rg-challenge3-rm556221
acrName = acrchallenge3rm556221
acrLoginServer = acrchallenge3rm556221.azurecr.io
location = eastus
dbHost = <IP-obtido-no-setup>
dbPort = 5432
dbName = mottu-api
dbUser = postgres
dbPassword = Mottu@2025!Secure (clique no cadeado 🔒)
```

#### Usar no Pipeline

Adicione no início do seu arquivo YAML:

```yaml
variables:
- group: mottu-api-config
- name: imageName
  value: 'mottu-api'
```

### Método 3: Azure Key Vault (Mais Seguro)

Para produção, use Azure Key Vault:

1. Crie um Key Vault no Azure
2. Adicione os secrets (senhas, connection strings)
3. No Azure DevOps:
   - Vá em **Library** → **+ Variable group**
   - Marque **Link secrets from an Azure key vault as variables**
   - Selecione sua subscription e Key Vault
   - Adicione os secrets necessários

---

## 🎬 Executando o Pipeline

### Execução Automática

O pipeline é acionado automaticamente quando:

- **Trigger:** Push na branch `main` ou `develop` (ou Pull Request)
- **Build Stage:** Sempre executa em todas as branches
- **Deploy Stage:** Executa automaticamente apenas na branch `main` após Build bem-sucedido

### Execução Manual

#### Via Interface

1. Vá em **Pipelines**
2. Selecione o pipeline
3. Clique em **Run pipeline**
4. Selecione a branch
5. Clique em **Run**

#### Via CLI

```bash
# Instalar Azure DevOps CLI
az extension add --name azure-devops

# Configurar
az devops configure --defaults organization=https://dev.azure.com/<org> project=<project>

# Executar pipeline
az pipelines run --name azure-pipelines
```

---

## 🔍 Monitoramento

### Ver Logs

1. Vá em **Pipelines**
2. Clique na execução do pipeline
3. Clique no job/stage
4. Clique na task para ver os logs detalhados

### Ver Status do Deploy

Após o deploy, o pipeline mostra:

```
==========================================
✓ DEPLOYMENT SUCCESSFUL
==========================================

Application URL: http://<IP>:8080
Container State: Running
Image: acrchallenge3rm556221.azurecr.io/mottu-api:123
Build ID: 123

API Endpoints:
  - Motos:       http://<IP>:8080/api/moto
  - Motoqueiros: http://<IP>:8080/api/motoqueiro
  - Galpões:     http://<IP>:8080/api/galpao
  - Manutenções: http://<IP>:8080/api/manutencao
==========================================
```

### Ver Logs da Aplicação

```bash
az container logs --resource-group rg-challenge3-rm556221 --name mottu-api-aci --follow
```

---

## 🐛 Troubleshooting

### Erro: "Service connection not found"

**Solução:** Verifique se:
1. A service connection foi criada
2. O nome está correto na variável `azureServiceConnection`
3. A service connection tem permissão no Resource Group

### Erro: "Database not found"

**Solução:**
1. Verifique se executou o `setup.sh`
2. Confirme que o banco está rodando:
```bash
az container show --resource-group rg-challenge3-rm556221 --name mottu-db-aci
```

### Erro: "ACR login failed"

**Solução:**
1. Verifique se o ACR existe
2. Confirme que a service connection tem permissão no ACR
3. Tente login manual:
```bash
az acr login --name acrchallenge3rm556221
```

### Erro: "Health check failed"

**Solução:**
1. Aguarde mais tempo (aplicação pode demorar para iniciar)
2. Verifique os logs:
```bash
az container logs --resource-group rg-challenge3-rm556221 --name mottu-api-aci
```
3. Verifique a conexão com o banco

### Pipeline fica "pendente"

**Solução:**
1. Verifique se há agentes disponíveis
2. Se usando self-hosted agents, verifique se estão online
3. Para Microsoft-hosted agents, aguarde na fila

### Variáveis não são encontradas

**Solução:**
1. Verifique se as variáveis foram criadas
2. Confirme que estão no scope correto (pipeline ou variable group)
3. Verifique se o variable group está referenciado no YAML

---

## 📊 Estrutura Completa do Fluxo

```
┌──────────────────────────────────────────────────────────────┐
│ 1. SETUP INICIAL (Manual - Uma vez)                         │
│    - Executar setup.sh                                       │
│    - Criar Service Connection                                │
│    - Configurar variáveis/secrets                            │
└──────────────────────────────────────────────────────────────┘
                           ↓
┌──────────────────────────────────────────────────────────────┐
│ 2. DESENVOLVIMENTO (Automático)                              │
│                                                              │
│    Developer push → CI Pipeline                              │
│         ↓                                                    │
│    ✓ Checkout                                                │
│    ✓ Build com Maven                                         │
│    ✓ Testes                                                  │
│    ✓ Build Docker                                            │
│    ✓ Push para ACR                                           │
│         ↓                                                    │
│    (Se branch = main) → CD Pipeline                          │
│         ↓                                                    │
│    ⏸️  Aprovação (se configurada)                            │
│         ↓                                                    │
│    ✓ Pull da imagem                                          │
│    ✓ Deploy no ACI                                           │
│    ✓ Health check                                            │
│    ✓ Aplicação disponível! 🎉                                │
└──────────────────────────────────────────────────────────────┘
```

---

## 🎯 Checklist de Configuração

Use este checklist para garantir que tudo está configurado:

- [ ] Executou `setup.sh` e anotou as variáveis
- [ ] Criou o repositório no Azure DevOps
- [ ] Fez push do código para o repositório
- [ ] Criou a Service Connection do Azure
- [ ] Criou o ambiente "production"
- [ ] Criou o pipeline (unificado OU separados)
- [ ] Configurou as variáveis necessárias
- [ ] Configurou as variáveis secretas (dbPassword)
- [ ] Testou o pipeline com uma execução manual
- [ ] Verificou que o deploy foi bem-sucedido
- [ ] Testou a aplicação no navegador
- [ ] Configurou aprovações (opcional)

---

## 📚 Recursos Adicionais

- [Documentação Azure Pipelines](https://docs.microsoft.com/azure/devops/pipelines/)
- [YAML Schema Reference](https://docs.microsoft.com/azure/devops/pipelines/yaml-schema)
- [Azure CLI Reference](https://docs.microsoft.com/cli/azure/)
- [Docker Tasks](https://docs.microsoft.com/azure/devops/pipelines/tasks/build/docker)

---

## 🆘 Precisa de Ajuda?

Se encontrar problemas:

1. Verifique os logs detalhados no Azure DevOps
2. Consulte a seção [Troubleshooting](#troubleshooting)
3. Verifique se todos os recursos existem no Azure:
   ```bash
   az resource list --resource-group rg-challenge3-rm556221 --output table
   ```

---

**Boa sorte com seu CI/CD! 🚀**

