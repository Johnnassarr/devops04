# 🚀 Próximos Passos - Deploy Completo

## 📋 Resumo da Situação Atual

✅ **O que já está funcionando:**
- Build do projeto com Maven
- Testes executando com sucesso
- Imagem Docker sendo construída localmente
- Pipeline CI configurado

❌ **O que ainda precisa ser feito:**
- Criar infraestrutura Azure (ACR não existe ainda)
- Configurar credenciais do ACR no pipeline
- Push da imagem para ACR
- Deploy da aplicação no Azure

---

## 🎯 Passo a Passo para Completar o Deploy

### **PASSO 1: Criar a Infraestrutura Azure**

Execute o script `setup.sh` no **Azure Cloud Shell** ou em uma máquina com Azure CLI instalado:

```bash
# 1. Clone o repositório (se ainda não tiver)
git clone https://github.com/Johnnassarr/devops04.git
cd devops04

# 2. Dê permissão de execução
chmod +x setup.sh

# 3. Execute o script
./setup.sh
```

**⚠️ IMPORTANTE:** Anote as informações que o script vai mostrar:
- ACR Login Server
- ACR Username
- ACR Password
- Database Host
- Database Password

---

### **PASSO 2: Configurar Credenciais no Azure DevOps**

Depois de executar o `setup.sh`, você terá as credenciais do ACR. Configure no Azure DevOps:

1. Acesse: https://dev.azure.com/rm556221/challenge04devops
2. Vá em **Pipelines** > Selecione seu pipeline
3. Clique em **Edit**
4. Clique em **Variables** (canto superior direito)
5. Adicione as seguintes variáveis:

| Nome | Valor | Secret? |
|------|-------|---------|
| `acrPassword` | A senha obtida do script | ✅ Sim |
| `dbHost` | O IP do banco obtido do script | ❌ Não |
| `dbPassword` | A senha do banco obtida do script | ✅ Sim |

**Como obter a senha do ACR manualmente** (se necessário):
```bash
az acr credential show --name acrchallenge3rm556221 --query "passwords[0].value" -o tsv
```

---

### **PASSO 3: Executar o Pipeline Novamente**

Após configurar as credenciais:

1. No Azure DevOps, vá em **Pipelines**
2. Clique em **Run pipeline**
3. Selecione a branch `main`
4. Clique em **Run**

**O que deve acontecer agora:**
- ✅ Build e testes: sucesso
- ✅ Build da imagem Docker: sucesso
- ✅ Login no ACR: **agora deve funcionar**
- ✅ Push para ACR: **agora deve funcionar**

---

### **PASSO 4: Habilitar Deploy Automático (Opcional)**

Atualmente, o estágio de Deploy está comentado no pipeline. Para habilitá-lo:

1. **Instale o Azure CLI no seu agente self-hosted:**
   ```bash
   # No Ubuntu/Debian:
   curl -sL https://aka.ms/InstallAzureCLIDeb | sudo bash
   ```

2. **Descomente o estágio de Deploy** no arquivo `azure-pipelines.yml` (linhas 229-243)

3. **Configure Azure Container Instances:**
   - O deploy criará automaticamente um container com sua aplicação
   - Será exposto publicamente com um IP

---

### **PASSO 5: Testar a Aplicação**

#### **Opção A: Deploy Manual (Mais Rápido para Testar)**

Execute no Azure Cloud Shell após o push da imagem:

```bash
# Criar Container Instance
az container create \
  --resource-group rg-challenge3-rm556221 \
  --name mottu-api-container \
  --image acrchallenge3rm556221.azurecr.io/mottu-api:latest \
  --cpu 1 \
  --memory 1.5 \
  --registry-login-server acrchallenge3rm556221.azurecr.io \
  --registry-username acrchallenge3rm556221 \
  --registry-password "<SUA-SENHA-ACR>" \
  --ip-address Public \
  --ports 8080 \
  --environment-variables \
    SPRING_DATASOURCE_URL="jdbc:postgresql://<DB-HOST>:5432/mottu-api" \
    SPRING_DATASOURCE_USERNAME=postgres \
    SPRING_DATASOURCE_PASSWORD="<DB-PASSWORD>"

# Obter o IP público
az container show \
  --resource-group rg-challenge3-rm556221 \
  --name mottu-api-container \
  --query ipAddress.ip \
  --output tsv
```

**Substitua:**
- `<SUA-SENHA-ACR>`: senha obtida no Passo 1
- `<DB-HOST>`: IP do banco obtido no Passo 1
- `<DB-PASSWORD>`: senha do banco obtida no Passo 1

#### **Opção B: Deploy Automático via Pipeline**

Siga o Passo 4 para habilitar o deploy automático.

---

### **PASSO 6: Acessar e Testar os Endpoints**

Após o deploy (manual ou automático), sua aplicação estará rodando:

```
http://<IP-PUBLICO>:8080
```

**Endpoints disponíveis:**
- `GET /` - Home page
- `GET /dashboard` - Dashboard (requer autenticação)
- `GET /api/motos` - API REST de motos
- `GET /api/motoqueiros` - API REST de motoqueiros
- `GET /api/galpoes` - API REST de galpões
- `GET /api/manutencoes` - API REST de manutenções

**Testar com curl:**
```bash
# Obter IP (se não souber)
IP=$(az container show -g rg-challenge3-rm556221 -n mottu-api-container --query ipAddress.ip -o tsv)

# Testar health check
curl http://$IP:8080/actuator/health

# Testar endpoint API
curl http://$IP:8080/api/motos
```

---

## 🔍 Verificar Logs

**Ver logs do container no Azure:**
```bash
az container logs \
  --resource-group rg-challenge3-rm556221 \
  --name mottu-api-container
```

**Ver logs em tempo real:**
```bash
az container attach \
  --resource-group rg-challenge3-rm556221 \
  --name mottu-api-container
```

---

## 🐛 Troubleshooting

### Erro: "no such host" ao fazer push para ACR
**Causa:** O ACR não existe ou não está acessível.
**Solução:** Execute o `setup.sh` (Passo 1)

### Erro: "unauthorized" ao fazer login no ACR
**Causa:** Credenciais incorretas ou não configuradas.
**Solução:** Verifique as variáveis no Azure DevOps (Passo 2)

### Container não inicia
**Causa:** Variáveis de ambiente do banco de dados incorretas.
**Solução:** Verifique os logs e corrija as variáveis de ambiente:
```bash
az container logs -g rg-challenge3-rm556221 -n mottu-api-container
```

### Aplicação não responde
**Causa:** Aplicação pode estar inicializando (demora ~30-60s)
**Solução:** Aguarde e verifique os logs

---

## 📊 Checklist Final

- [ ] Executei `setup.sh` e anotei as credenciais
- [ ] Configurei `acrPassword` no Azure DevOps
- [ ] Configurei `dbHost` e `dbPassword` no Azure DevOps
- [ ] Pipeline executou com sucesso (sem erros de DNS)
- [ ] Imagem foi enviada para ACR (verificar no portal Azure)
- [ ] Container foi criado (manual ou via pipeline)
- [ ] Aplicação está respondendo nos endpoints
- [ ] Testei os endpoints da API

---

## 🎉 Sucesso!

Quando todos os passos acima estiverem completos, você terá:
- ✅ Pipeline CI/CD totalmente funcional
- ✅ Imagens Docker no Azure Container Registry
- ✅ Aplicação rodando no Azure Container Instances
- ✅ Banco de dados PostgreSQL no Azure

**Acesse sua aplicação em:** `http://<IP-PUBLICO>:8080`

