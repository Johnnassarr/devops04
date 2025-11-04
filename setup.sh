#!/bin/bash

set -e

# =============================================================================
# CHALLENGE 3 - DevOps & Cloud Computing
# Script de Setup da Infraestrutura Azure (Execução Única)
# =============================================================================
# Este script cria os recursos de infraestrutura que devem existir antes
# dos pipelines CI/CD executarem (Resource Group, ACR e Banco de Dados).
# Deve ser executado apenas uma vez no início do projeto.
# =============================================================================

# Configurações
export RM=556221
export AZURE_RG=rg-challenge3-rm${RM}
export ACR_NAME=acrchallenge3rm${RM}
export LOCATION="East US"
export DB_NAME=mottu-api
export DB_USER=postgres
export DB_PASSWORD=Mottu@2025!Secure

echo ""
echo "========================================================================="
echo "  SETUP DA INFRAESTRUTURA AZURE"
echo "========================================================================="
echo "  RM: ${RM}"
echo "  Resource Group: ${AZURE_RG}"
echo "  ACR: ${ACR_NAME}"
echo "  Location: ${LOCATION}"
echo "  Database: ${DB_NAME}"
echo "========================================================================="
echo ""
echo "IMPORTANTE: Este script deve ser executado apenas UMA VEZ."
echo "Após a execução, os pipelines CI/CD farão o build e deploy."
echo ""
read -p "Deseja continuar? (s/n) " -n 1 -r
echo ""
if [[ ! $REPLY =~ ^[Ss]$ ]]; then
    echo "Operação cancelada."
    exit 0
fi
echo ""

# [1/5] Verificando pré-requisitos
echo "[1/5] Verificando pré-requisitos..."

if ! command -v az &> /dev/null; then
    echo "ERRO: Azure CLI não está instalado!"
    echo "Instale em: https://docs.microsoft.com/cli/azure/install-azure-cli"
    exit 1
fi

echo "✓ Azure CLI instalado"
echo ""

# [2/5] Verificando login no Azure
echo "[2/5] Verificando login no Azure..."

if ! az account show &> /dev/null; then
    echo "ERRO: Não logado no Azure CLI."
    echo "Execute: az login"
    exit 1
fi

ACCOUNT_NAME=$(az account show --query "name" -o tsv)
SUBSCRIPTION_ID=$(az account show --query "id" -o tsv)
echo "✓ Logado no Azure CLI"
echo "  Conta: ${ACCOUNT_NAME}"
echo "  Subscription ID: ${SUBSCRIPTION_ID}"
echo ""

# [3/5] Criando Resource Group
echo "[3/5] Criando Resource Group..."

if az group show --name "${AZURE_RG}" &> /dev/null; then
    echo "⚠ Resource Group já existe: ${AZURE_RG}"
    echo "  Pulando criação..."
else
    echo "  Criando Resource Group: ${AZURE_RG}"
    az group create \
        --name "${AZURE_RG}" \
        --location "${LOCATION}" \
        --output table
    echo "✓ Resource Group criado com sucesso"
fi
echo ""

# [4/5] Criando Azure Container Registry
echo "[4/5] Criando Azure Container Registry..."

if az acr show --name "${ACR_NAME}" --resource-group "${AZURE_RG}" &> /dev/null; then
    echo "⚠ ACR já existe: ${ACR_NAME}"
    echo "  Pulando criação..."
else
    echo "  Criando ACR: ${ACR_NAME}"
    az acr create \
        --resource-group "${AZURE_RG}" \
        --name "${ACR_NAME}" \
        --sku Basic \
        --admin-enabled true \
        --output table
    
    echo "  Aguardando ACR ficar disponível..."
    for i in {1..10}; do
        if az acr show --name "${ACR_NAME}" --resource-group "${AZURE_RG}" &> /dev/null; then
            echo "✓ ACR criado e disponível"
            break
        fi
        echo "  Tentativa ${i}/10..."
        sleep 5
    done
fi

# Obter informações do ACR
ACR_LOGIN_SERVER=$(az acr show --name "${ACR_NAME}" --query "loginServer" -o tsv)
echo "  Login Server: ${ACR_LOGIN_SERVER}"
echo ""

# [5/5] Criando Banco de Dados PostgreSQL
echo "[5/5] Criando Banco de Dados PostgreSQL..."

# Verificar se já existe
if az container show --resource-group "${AZURE_RG}" --name mottu-db-aci &> /dev/null; then
    echo "⚠ Banco de dados já existe: mottu-db-aci"
    DB_IP=$(az container show --resource-group "${AZURE_RG}" --name mottu-db-aci --query "ipAddress.ip" -o tsv)
    echo "  IP existente: ${DB_IP}"
    echo "  Pulando criação..."
else
    # Obter credenciais do ACR para pull da imagem
    echo "  Obtendo credenciais do ACR..."
    ACR_PASSWORD=$(az acr credential show --name "${ACR_NAME}" --query "passwords[0].value" -o tsv)
    
    # NOTA: Para usar esta imagem, você precisa ter feito o push dela anteriormente
    # Caso contrário, use a imagem pública: postgres:15-alpine
    
    # Criar arquivo YAML temporário
    cat > aci-db-setup-temp.yaml << EOF
apiVersion: 2021-09-01
location: eastus
name: mottu-db-aci
properties:
  containers:
  - name: postgres
    properties:
      image: postgres:15-alpine
      resources:
        requests:
          cpu: 1
          memoryInGb: 1.5
      ports:
      - port: 5432
        protocol: TCP
      environmentVariables:
      - name: POSTGRES_DB
        value: ${DB_NAME}
      - name: POSTGRES_USER
        value: ${DB_USER}
      - name: POSTGRES_PASSWORD
        secureValue: ${DB_PASSWORD}
  osType: Linux
  restartPolicy: Always
  ipAddress:
    type: Public
    ports:
    - protocol: TCP
      port: 5432
tags:
  environment: production
  managed-by: setup-script
type: Microsoft.ContainerInstance/containerGroups
EOF

    echo "  Criando container PostgreSQL..."
    az container create \
        --resource-group "${AZURE_RG}" \
        --file aci-db-setup-temp.yaml
    
    echo "  Aguardando banco de dados inicializar..."
    sleep 20
    
    # Verificar status
    for i in {1..12}; do
        STATE=$(az container show \
            --resource-group "${AZURE_RG}" \
            --name mottu-db-aci \
            --query "containers[0].instanceView.currentState.state" -o tsv 2>/dev/null || echo "Unknown")
        
        if [ "$STATE" = "Running" ]; then
            echo "✓ Banco de dados está rodando"
            break
        fi
        
        echo "  Estado: ${STATE} - Aguardando... (${i}/12)"
        sleep 5
    done
    
    DB_IP=$(az container show --resource-group "${AZURE_RG}" --name mottu-db-aci --query "ipAddress.ip" -o tsv)
    echo "✓ Banco de dados criado com sucesso"
    echo "  IP do banco: ${DB_IP}"
    
    # Limpar arquivo temporário
    rm -f aci-db-setup-temp.yaml
fi
echo ""

# Resumo Final
echo "========================================================================="
echo "  SETUP CONCLUÍDO COM SUCESSO"
echo "========================================================================="
echo ""
echo "RECURSOS CRIADOS:"
echo ""
echo "1. RESOURCE GROUP"
echo "   Nome: ${AZURE_RG}"
echo "   Location: ${LOCATION}"
echo ""
echo "2. AZURE CONTAINER REGISTRY (ACR)"
echo "   Nome: ${ACR_NAME}"
echo "   Login Server: ${ACR_LOGIN_SERVER}"
echo "   Admin Enabled: true"
echo ""
echo "3. BANCO DE DADOS POSTGRESQL"
echo "   Container: mottu-db-aci"
echo "   IP: ${DB_IP}"
echo "   Porta: 5432"
echo "   Database: ${DB_NAME}"
echo "   Usuário: ${DB_USER}"
echo "   Senha: ${DB_PASSWORD}"
echo ""
echo "========================================================================="
echo "  VARIÁVEIS DE AMBIENTE PARA CI/CD"
echo "========================================================================="
echo ""
echo "Configure estas variáveis em seus pipelines CI/CD:"
echo ""
echo "# Azure Resources"
echo "AZURE_RG=${AZURE_RG}"
echo "ACR_NAME=${ACR_NAME}"
echo "ACR_LOGIN_SERVER=${ACR_LOGIN_SERVER}"
echo "LOCATION=${LOCATION}"
echo ""
echo "# Database Connection"
echo "DB_HOST=${DB_IP}"
echo "DB_PORT=5432"
echo "DB_NAME=${DB_NAME}"
echo "DB_USER=${DB_USER}"
echo "DB_PASSWORD=${DB_PASSWORD}"
echo ""
echo "# ACR Credentials (obtenha usando az acr credential show)"
echo "ACR_USERNAME=${ACR_NAME}"
echo "ACR_PASSWORD=<use: az acr credential show --name ${ACR_NAME} --query passwords[0].value -o tsv>"
echo ""
echo "========================================================================="
echo "  PRÓXIMOS PASSOS"
echo "========================================================================="
echo ""
echo "1. Configure os pipelines CI/CD com as variáveis acima"
echo ""
echo "2. O pipeline de CI deve:"
echo "   - Fazer build da aplicação"
echo "   - Criar imagem Docker"
echo "   - Fazer push para o ACR: ${ACR_LOGIN_SERVER}"
echo ""
echo "3. O pipeline de CD deve:"
echo "   - Fazer pull da imagem do ACR"
echo "   - Fazer deploy no Azure Container Instances"
echo "   - Conectar ao banco de dados existente: ${DB_IP}"
echo ""
echo "========================================================================="
echo "  COMANDOS ÚTEIS"
echo "========================================================================="
echo ""
echo "# Ver logs do banco de dados"
echo "az container logs --resource-group ${AZURE_RG} --name mottu-db-aci"
echo ""
echo "# Conectar ao banco via psql"
echo "psql -h ${DB_IP} -U ${DB_USER} -d ${DB_NAME}"
echo ""
echo "# Listar repositórios no ACR"
echo "az acr repository list --name ${ACR_NAME} --output table"
echo ""
echo "# Obter credenciais do ACR"
echo "az acr credential show --name ${ACR_NAME}"
echo ""
echo "# Fazer login no ACR (para testes locais)"
echo "az acr login --name ${ACR_NAME}"
echo ""
echo "# Ver todos os recursos do Resource Group"
echo "az resource list --resource-group ${AZURE_RG} --output table"
echo ""
echo "# REMOVER TODA A INFRAESTRUTURA (cuidado!)"
echo "az group delete --name ${AZURE_RG} --yes --no-wait"
echo ""
echo "========================================================================="
echo ""

