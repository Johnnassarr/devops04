# Azure DevOps Configuration

Este diretório contém informações sobre a configuração do Azure DevOps para este projeto.

## 📋 Pipeline

### Pipeline Completo
- **Arquivo:** `/azure-pipelines.yml`
- **Descrição:** Pipeline único com CI + CD
- **Stages:**
  - Build (CI): Build, Test, Push para ACR
  - Deploy (CD): Deploy no ACI (apenas branch `main`)

## 🔧 Configuração Rápida

Siga o guia: [QUICK-START-AZURE-PIPELINES.md](../QUICK-START-AZURE-PIPELINES.md)

## 📖 Documentação Completa

Consulte: [AZURE-DEVOPS-SETUP.md](../AZURE-DEVOPS-SETUP.md)

## 🏗️ Infraestrutura

Antes de configurar os pipelines, execute:
```bash
./setup.sh
```

## 🔐 Variáveis Necessárias

Configure as seguintes variáveis no Azure DevOps:

| Variável | Valor | Secret? |
|----------|-------|---------|
| azureServiceConnection | Nome da service connection | ❌ |
| dbHost | IP do banco (obtido no setup) | ❌ |
| dbPort | 5432 | ❌ |
| dbName | mottu-api | ❌ |
| dbUser | postgres | ❌ |
| dbPassword | Mottu@2025!Secure | ✅ |

## 🚀 Deploy

O deploy é automático após push na branch `main` quando usando o pipeline unificado.

