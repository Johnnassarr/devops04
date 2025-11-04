# ⚡ Quick Start - Azure Pipelines

## 🚀 Configure em 5 Minutos

Este projeto usa **um único arquivo de pipeline** (`azure-pipelines.yml`) que faz tudo:
- ✅ Build da aplicação Java
- ✅ Testes unitários
- ✅ Build da imagem Docker
- ✅ Push para Azure Container Registry
- ✅ Deploy automático no Azure Container Instances

---

### Passo 1: Setup da Infraestrutura
```bash
# Execute apenas UMA VEZ
./setup.sh
```

**⚠️ IMPORTANTE:** Anote o **IP do banco de dados** que aparece no final!

---

### Passo 2: Criar Service Connection

1. Azure DevOps → **Project Settings** → **Service connections**
2. **New service connection** → **Azure Resource Manager**
3. **Service principal (automatic)**
4. Subscription + Resource Group: `rg-challenge3-rm556221`
5. Nome: `azure-mottu-connection`
6. ✅ **Save**

---

### Passo 3: Criar Pipeline

1. Azure DevOps → **Pipelines** → **New Pipeline**
2. Selecione seu repositório
3. **Existing Azure Pipelines YAML file**
4. Selecione: `/azure-pipelines.yml`
5. Clique em **Variables**

---

### Passo 4: Configurar Variáveis

Adicione estas variáveis:

| Nome | Valor | Secret? |
|------|-------|---------|
| `azureServiceConnection` | `azure-mottu-connection` | ❌ |
| `dbHost` | `<IP-do-banco-do-setup>` | ❌ |
| `dbPort` | `5432` | ❌ |
| `dbName` | `mottu-api` | ❌ |
| `dbUser` | `postgres` | ❌ |
| `dbPassword` | `Mottu@2025!Secure` | ✅ (clique no 🔒) |

---

### Passo 5: Criar Ambiente

1. Azure DevOps → **Pipelines** → **Environments**
2. **New environment**
3. Nome: `production`
4. **Create**

---

### Passo 6: Executar!

1. Clique em **Save and Run**
2. Aguarde a mágica acontecer! ✨

```
CI Stage (Build)
├─ ✓ Checkout
├─ ✓ Build Java
├─ ✓ Testes
├─ ✓ Build Docker
└─ ✓ Push para ACR

CD Stage (Deploy)
├─ ✓ Pull da imagem
├─ ✓ Deploy no ACI
└─ ✓ Health Check
```

---

## 🎯 Resultado

Ao final, você verá:

```
==========================================
✓ DEPLOYMENT SUCCESSFUL
==========================================

Application URL: http://20.10.20.30:8080

API Endpoints:
  - Motos:       http://20.10.20.30:8080/api/moto
  - Motoqueiros: http://20.10.20.30:8080/api/motoqueiro
  - Galpões:     http://20.10.20.30:8080/api/galpao
  - Manutenções: http://20.10.20.30:8080/api/manutencao
==========================================
```

---

## 📋 Checklist Rápido

- [ ] Executei `setup.sh`
- [ ] Anotei o IP do banco
- [ ] Criei a Service Connection
- [ ] Criei o Pipeline
- [ ] Configurei as 6 variáveis
- [ ] Criei o ambiente `production`
- [ ] Executei o pipeline

---

## 🐛 Problemas Comuns

### ❌ "Service connection not found"
**Solução:** Verifique se o nome está exatamente como configurou: `azure-mottu-connection`

### ❌ "Database not found"
**Solução:** Execute o `setup.sh` primeiro!

### ❌ "Health check failed"
**Solução:** Aguarde mais tempo. A aplicação pode demorar 1-2 minutos para iniciar.

---

## 📚 Documentação Completa

Para configuração avançada, consulte:
- **[AZURE-DEVOPS-SETUP.md](AZURE-DEVOPS-SETUP.md)** - Guia completo
- **[SETUP-GUIDE.md](SETUP-GUIDE.md)** - Guia de infraestrutura e CI/CD

---

## 🎉 Pronto!

Agora toda vez que você fizer push no `main`:
1. 🔨 CI vai buildar automaticamente
2. 🚀 CD vai fazer deploy automaticamente
3. ✅ Aplicação estará disponível!

**Happy Coding! 🚀**

