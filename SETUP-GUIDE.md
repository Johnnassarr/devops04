# Guia de Setup e CI/CD

## 📋 Visão Geral

Este projeto agora utiliza uma abordagem moderna com **separação entre infraestrutura e deployment**:

- **setup.sh**: Executa **UMA VEZ** para criar a infraestrutura base
- **Pipelines CI/CD**: Executam **automaticamente** para build e deploy

---

## 🏗️ 1. Setup Inicial (Executar UMA VEZ)

### O que o `setup.sh` faz:

✅ Cria o **Resource Group** no Azure  
✅ Cria o **Azure Container Registry (ACR)**  
✅ Cria o **Banco de Dados PostgreSQL** (como recurso permanente)  
✅ Gera as **variáveis de ambiente** necessárias para CI/CD

### Como executar:

```bash
# 1. Fazer login no Azure
az login

# 2. Executar o setup
./setup.sh
```

### Resultado:

Após a execução, você terá:

```
✓ Resource Group: rg-challenge3-rm556221
✓ ACR: acrchallenge3rm556221.azurecr.io
✓ Banco de Dados PostgreSQL rodando (IP fornecido)
```

---

## 🔄 2. Pipelines CI/CD

### Pipeline CI (Continuous Integration)

**Quando executar:** A cada push no repositório

**O que faz:**
1. Checkout do código
2. Build da aplicação Java com Maven
3. Criar imagem Docker
4. Push da imagem para o ACR

**Exemplo (GitHub Actions - `.github/workflows/ci.yml`):**

```yaml
name: CI - Build and Push

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main ]

jobs:
  build:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v3
    
    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'temurin'
        cache: maven
    
    - name: Build with Maven
      run: mvn clean package -DskipTests
    
    - name: Login to Azure
      uses: azure/login@v1
      with:
        creds: ${{ secrets.AZURE_CREDENTIALS }}
    
    - name: Login to ACR
      run: az acr login --name ${{ secrets.ACR_NAME }}
    
    - name: Build and Push Docker Image
      run: |
        IMAGE_TAG=$(date +%Y%m%d-%H%M%S)-${GITHUB_SHA::7}
        docker build -t ${{ secrets.ACR_LOGIN_SERVER }}/mottu-api:${IMAGE_TAG} .
        docker build -t ${{ secrets.ACR_LOGIN_SERVER }}/mottu-api:latest .
        docker push ${{ secrets.ACR_LOGIN_SERVER }}/mottu-api:${IMAGE_TAG}
        docker push ${{ secrets.ACR_LOGIN_SERVER }}/mottu-api:latest
```

### Pipeline CD (Continuous Deployment)

**Quando executar:** Após sucesso do CI

**O que faz:**
1. Pull da imagem do ACR
2. Deploy no Azure Container Instances
3. Conecta ao banco de dados existente

**Exemplo (GitHub Actions - `.github/workflows/cd.yml`):**

```yaml
name: CD - Deploy to Azure

on:
  workflow_run:
    workflows: ["CI - Build and Push"]
    types:
      - completed
    branches: [ main ]

jobs:
  deploy:
    runs-on: ubuntu-latest
    if: ${{ github.event.workflow_run.conclusion == 'success' }}
    
    steps:
    - name: Login to Azure
      uses: azure/login@v1
      with:
        creds: ${{ secrets.AZURE_CREDENTIALS }}
    
    - name: Get ACR Password
      id: acr_creds
      run: |
        ACR_PASSWORD=$(az acr credential show --name ${{ secrets.ACR_NAME }} --query "passwords[0].value" -o tsv)
        echo "::add-mask::$ACR_PASSWORD"
        echo "ACR_PASSWORD=$ACR_PASSWORD" >> $GITHUB_OUTPUT
    
    - name: Deploy Application
      run: |
        # Remove container existente (se houver)
        az container delete \
          --resource-group ${{ secrets.AZURE_RG }} \
          --name mottu-api-aci \
          --yes || true
        
        # Criar novo container
        cat > aci-app.yaml << EOF
        apiVersion: 2021-09-01
        location: eastus
        name: mottu-api-aci
        properties:
          containers:
          - name: mottu-api
            properties:
              image: ${{ secrets.ACR_LOGIN_SERVER }}/mottu-api:latest
              resources:
                requests:
                  cpu: 1
                  memoryInGb: 1.5
              ports:
              - port: 8080
                protocol: TCP
              environmentVariables:
              - name: SPRING_DATASOURCE_URL
                value: jdbc:postgresql://${{ secrets.DB_HOST }}:5432/${{ secrets.DB_NAME }}
              - name: SPRING_DATASOURCE_USERNAME
                value: ${{ secrets.DB_USER }}
              - name: SPRING_DATASOURCE_PASSWORD
                secureValue: ${{ secrets.DB_PASSWORD }}
              - name: JAVA_OPTS
                value: "-Xms512m -Xmx1024m"
          osType: Linux
          restartPolicy: Always
          ipAddress:
            type: Public
            ports:
            - protocol: TCP
              port: 8080
          imageRegistryCredentials:
          - server: ${{ secrets.ACR_LOGIN_SERVER }}
            username: ${{ secrets.ACR_NAME }}
            password: ${{ steps.acr_creds.outputs.ACR_PASSWORD }}
        tags: null
        type: Microsoft.ContainerInstance/containerGroups
        EOF
        
        az container create \
          --resource-group ${{ secrets.AZURE_RG }} \
          --file aci-app.yaml
    
    - name: Get Application URL
      run: |
        APP_IP=$(az container show \
          --resource-group ${{ secrets.AZURE_RG }} \
          --name mottu-api-aci \
          --query "ipAddress.ip" -o tsv)
        echo "🚀 Application deployed at: http://${APP_IP}:8080"
```

---

## 🔐 3. Secrets/Variáveis Necessárias

Configure estas variáveis no seu sistema de CI/CD (GitHub Secrets, Azure DevOps Variables, etc.):

### GitHub Actions Secrets:

```
AZURE_CREDENTIALS          # Credenciais para az login
AZURE_RG                  # rg-challenge3-rm556221
ACR_NAME                  # acrchallenge3rm556221
ACR_LOGIN_SERVER          # acrchallenge3rm556221.azurecr.io
DB_HOST                   # IP do banco (obtido após setup)
DB_PORT                   # 5432
DB_NAME                   # mottu-api
DB_USER                   # postgres
DB_PASSWORD               # Mottu@2025!Secure
```

### Como obter AZURE_CREDENTIALS:

```bash
az ad sp create-for-rbac \
  --name "mottu-github-actions" \
  --role contributor \
  --scopes /subscriptions/<SUBSCRIPTION_ID>/resourceGroups/<RESOURCE_GROUP> \
  --sdk-auth
```

---

## 📊 4. Fluxo Completo

```
┌─────────────────────────────────────────────────────────────┐
│  SETUP INICIAL (Uma vez)                                    │
├─────────────────────────────────────────────────────────────┤
│  1. Executar ./setup.sh                                     │
│  2. Anotar variáveis de ambiente geradas                    │
│  3. Configurar secrets no GitHub/Azure DevOps               │
└─────────────────────────────────────────────────────────────┘
                           ↓
┌─────────────────────────────────────────────────────────────┐
│  DESENVOLVIMENTO (Contínuo)                                 │
├─────────────────────────────────────────────────────────────┤
│  1. Developer faz push no Git                               │
│  2. Pipeline CI é acionado automaticamente                  │
│     - Build da aplicação                                    │
│     - Criação da imagem Docker                              │
│     - Push para ACR                                         │
│  3. Pipeline CD é acionado automaticamente                  │
│     - Deploy no ACI                                         │
│     - Conecta ao banco existente                            │
│  4. Aplicação está disponível no novo IP                    │
└─────────────────────────────────────────────────────────────┘
```

---

## 🧹 5. Limpeza

### Remover apenas a aplicação (manter infra):
```bash
az container delete --resource-group rg-challenge3-rm556221 --name mottu-api-aci --yes
```

### Remover toda a infraestrutura:
```bash
az group delete --name rg-challenge3-rm556221 --yes --no-wait
```

---

## 📝 6. Comandos Úteis

### Ver logs da aplicação:
```bash
az container logs --resource-group rg-challenge3-rm556221 --name mottu-api-aci --follow
```

### Ver logs do banco:
```bash
az container logs --resource-group rg-challenge3-rm556221 --name mottu-db-aci --follow
```

### Verificar status dos containers:
```bash
az container show --resource-group rg-challenge3-rm556221 --name mottu-api-aci --query "instanceView.state" -o tsv
```

### Listar imagens no ACR:
```bash
az acr repository list --name acrchallenge3rm556221 --output table
az acr repository show-tags --name acrchallenge3rm556221 --repository mottu-api --output table
```

### Conectar ao banco:
```bash
psql -h <DB_IP> -U postgres -d mottu-api
```

---

## 🎯 Vantagens desta Abordagem

✅ **Separação de responsabilidades**: Setup vs Deploy  
✅ **Infraestrutura imutável**: Criada uma vez, não muda  
✅ **Deploy automatizado**: CI/CD cuida do resto  
✅ **Rollback fácil**: Basta fazer deploy de uma imagem anterior  
✅ **Histórico de versões**: Todas as imagens ficam no ACR  
✅ **Segurança**: Credenciais gerenciadas por secrets  

---

## 🚀 Começando Agora

1. Execute o setup:
   ```bash
   ./setup.sh
   ```

2. Configure os secrets no seu repositório

3. Crie os arquivos de pipeline (`.github/workflows/` ou Azure DevOps)

4. Faça um push e veja a mágica acontecer! ✨

---

**Dúvidas?** Consulte a documentação oficial:
- [GitHub Actions](https://docs.github.com/actions)
- [Azure DevOps Pipelines](https://docs.microsoft.com/azure/devops/pipelines)
- [Azure Container Registry](https://docs.microsoft.com/azure/container-registry)

