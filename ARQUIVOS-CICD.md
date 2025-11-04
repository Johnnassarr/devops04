# 📁 Guia de Arquivos CI/CD

Este documento lista todos os arquivos relacionados à infraestrutura e CI/CD do projeto.

---

## 🏗️ Arquivos de Infraestrutura

### `setup.sh` ⭐
**Execução:** UMA VEZ (setup inicial)  
**Função:** Cria a infraestrutura base no Azure
- ✅ Resource Group
- ✅ Azure Container Registry (ACR)
- ✅ Banco de Dados PostgreSQL

**Quando usar:**
- No início do projeto
- Ao recriar a infraestrutura do zero

**Comando:**
```bash
./setup.sh
```

---

## 🔄 Arquivo de CI/CD - Azure Pipelines

### `azure-pipelines.yml` ⭐
**Tipo:** Pipeline Completo (CI + CD)  
**Função:** Build, Test, Push e Deploy - tudo em um único arquivo

**Stages:**

#### 1. Build Stage (CI)
- ✅ Checkout do código
- ✅ Build com Maven
- ✅ Testes unitários (com publicação de resultados)
- ✅ Code coverage (JaCoCo)
- ✅ Build da imagem Docker
- ✅ Push para Azure Container Registry

#### 2. Deploy Stage (CD)
- ✅ Pull da imagem mais recente do ACR
- ✅ Criação do deployment manifest
- ✅ Deploy no Azure Container Instances
- ✅ Health check da aplicação
- ✅ Exibição de logs iniciais

**Triggers:**
- Push nas branches: `main`, `develop`
- Pull Requests para: `main`, `develop`
- **Deploy só executa na branch `main`**

**Vantagens:**
- ✅ Um único arquivo para gerenciar
- ✅ Deploy automático após build
- ✅ Visualização clara do fluxo completo
- ✅ Mais simples de manter

---

## 📚 Documentação

### `README.md` ⭐
**Função:** Documentação principal do projeto
- Visão geral
- Stack técnica
- Arquitetura
- Quick start
- Endpoints da API
- Links para outras documentações

**Para quem:**
- Todos os desenvolvedores
- Novos membros da equipe
- Visitantes do repositório

---

### `QUICK-START-AZURE-PIPELINES.md` ⭐
**Função:** Guia rápido (5 minutos)
- Setup em 6 passos
- Checklist
- Problemas comuns

**Para quem:**
- Quem quer começar RAPIDAMENTE
- Quem já conhece Azure DevOps
- Para referência rápida

---

### `AZURE-DEVOPS-SETUP.md` 📖
**Função:** Guia completo e detalhado
- Explicação de cada opção
- Como criar Service Connection
- Como configurar variáveis
- Como criar ambientes
- Troubleshooting detalhado

**Para quem:**
- Quem está aprendendo Azure DevOps
- Quem precisa de configuração avançada
- Quem encontrou problemas

---

### `SETUP-GUIDE.md`
**Função:** Guia de infraestrutura e CI/CD
- O que o setup.sh faz
- Exemplos de pipelines (GitHub Actions)
- Variáveis de ambiente necessárias
- Comandos úteis

**Para quem:**
- Quem quer entender a infraestrutura
- Quem vai usar outros CI/CD (GitHub Actions, etc.)
- Administradores de sistema

---

### `ARQUIVOS-CICD.md` (este arquivo)
**Função:** Índice de todos os arquivos CI/CD
- Lista todos os arquivos
- Explica a função de cada um
- Quando usar cada arquivo

**Para quem:**
- Quem quer uma visão geral
- Quem está perdido com tantos arquivos
- Para referência rápida

---

## 🗂️ Estrutura Visual

```
📦 challenge-java-back-sprint-4/
│
├── 🏗️ INFRAESTRUTURA (executar 1x)
│   └── setup.sh                          ⭐ Setup inicial
│
├── 🔄 CI/CD - AZURE PIPELINES
│   └── azure-pipelines.yml               ⭐ Pipeline completo (CI+CD)
│
├── 📚 DOCUMENTAÇÃO
│   ├── README.md                         ⭐ Documentação principal
│   ├── QUICK-START-AZURE-PIPELINES.md    ⚡ Quick start (5 min)
│   ├── AZURE-DEVOPS-SETUP.md             📖 Guia completo
│   ├── SETUP-GUIDE.md                    🏗️ Guia de infraestrutura
│   └── ARQUIVOS-CICD.md                  📁 Este arquivo
│
├── 🐳 DOCKER
│   ├── Dockerfile                        Imagem da aplicação
│   └── docker-compose.yml                Desenvolvimento local
│
└── 📝 CÓDIGO FONTE
    ├── src/                              Código Java/Spring Boot
    └── pom.xml                           Dependências Maven
```

---

## 🎯 Fluxo de Uso Recomendado

### 1️⃣ Setup Inicial (Uma vez)

```bash
# 1. Executar setup de infraestrutura
./setup.sh

# 2. Anotar variáveis geradas (IP do banco, etc.)

# 3. Seguir QUICK-START-AZURE-PIPELINES.md
```

### 2️⃣ Configurar o Pipeline

```
Arquivo: azure-pipelines.yml
Resultado: Build e Deploy automático em um único fluxo
```

### 3️⃣ Desenvolvimento Contínuo

```
1. Developer faz push
2. CI executa automaticamente
3. CD executa automaticamente (ou manual)
4. Aplicação atualizada! 🎉
```

---

## 📋 Checklist de Arquivos

Verifique se tem todos os arquivos necessários:

### Infraestrutura
- [x] `setup.sh` - Setup de infraestrutura

### Pipeline
- [x] `azure-pipelines.yml` - Pipeline completo CI/CD

### Documentação
- [x] `README.md` - Principal
- [x] `QUICK-START-AZURE-PIPELINES.md` - Quick start
- [x] `AZURE-DEVOPS-SETUP.md` - Guia completo
- [x] `SETUP-GUIDE.md` - Infraestrutura
- [x] `ARQUIVOS-CICD.md` - Este arquivo

### Docker
- [x] `Dockerfile` - Imagem app
- [x] `docker-compose.yml` - Local dev

---

## 🔗 Links Rápidos

| Preciso de... | Arquivo |
|---------------|---------|
| Criar infraestrutura | `setup.sh` |
| Configurar pipeline RÁPIDO | `QUICK-START-AZURE-PIPELINES.md` |
| Entender tudo detalhadamente | `AZURE-DEVOPS-SETUP.md` |
| Ver documentação do projeto | `README.md` |
| Arquivo do pipeline | `azure-pipelines.yml` |
| Resolver problemas | `AZURE-DEVOPS-SETUP.md` → Troubleshooting |

---

## 🎓 Para Aprender

### Iniciante em CI/CD?
1. Leia `README.md` - Visão geral
2. Execute `setup.sh` - Crie a infraestrutura
3. Siga `QUICK-START-AZURE-PIPELINES.md` - Configure o pipeline
4. Use `azure-pipelines.yml` - Único arquivo necessário!

### Já conhece CI/CD?
1. Execute `setup.sh`
2. Configure `azure-pipelines.yml` no Azure DevOps
3. Configure variáveis
4. Execute e pronto!

### Quer configuração avançada?
1. Leia `AZURE-DEVOPS-SETUP.md` - Guia completo
2. Configure ambientes, aprovações, gates
3. Customize o `azure-pipelines.yml` conforme necessário

---

## 🚀 Começar Agora

**Passo 1:** Executar setup
```bash
./setup.sh
```

**Passo 2:** Seguir quick start
```bash
# Abrir no navegador
open QUICK-START-AZURE-PIPELINES.md
```

**Passo 3:** Configurar pipeline no Azure DevOps usando `azure-pipelines.yml`

**Passo 4:** Push no repositório e ver a mágica! ✨

---

## ❓ FAQ

### Qual arquivo de pipeline usar?
- **Resposta:** Use `azure-pipelines.yml` - é o único arquivo de pipeline do projeto!
- Ele contém tudo: Build (CI) e Deploy (CD) em um único fluxo

### Preciso executar setup.sh toda vez?
**Não!** Apenas UMA VEZ. Os pipelines fazem o resto.

### Posso usar GitHub Actions?
**Sim!** Consulte `SETUP-GUIDE.md` que tem exemplos de GitHub Actions.

### Como ver os logs?
```bash
az container logs --resource-group rg-challenge3-rm556221 --name mottu-api-aci
```

### Como deletar tudo?
```bash
az group delete --name rg-challenge3-rm556221 --yes
```

---

**Última atualização:** Sprint 4  
**Mantido por:** João (RM 556221)

