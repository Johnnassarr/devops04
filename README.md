# README – Projeto Java Challenge Mottu (Entrega 3 + Sprint 4)

## 🚀 CI/CD com Azure DevOps

[![Build Status](https://dev.azure.com/your-org/mottu-api/_apis/build/status/mottu-api?branchName=main)](https://dev.azure.com/your-org/mottu-api/_build/latest?definitionId=1&branchName=main)

## 📋 Índice
- [Visão Geral](#-visão-geral)
- [Quick Start - Azure Pipelines](#-quick-start---azure-pipelines)
- [Endpoints da API](#-endpoints)
- [Regras de Permissão](#-regras-de-permissão)
- [Documentação Adicional](#-documentação-adicional)

---

## 1️⃣ Visão Geral

Este projeto é uma aplicação **Java com Spring Boot** desenvolvida como entrega do **Challenge Mottu – Sprint 4**.  
A aplicação gerencia registros de **Manutenção, Motos, Galpões e Motoqueiros**, permitindo diferentes operações de acordo com o tipo de usuário.

### 🛠️ Stack Técnica

- **Backend:** Java 17 + Spring Boot
- **Banco de Dados:** PostgreSQL
- **Versionamento do Banco:** Flyway (com população automática de dados iniciais)
- **Container Registry:** Azure Container Registry (ACR)
- **Deployment:** Azure Container Instances (ACI)
- **CI/CD:** Azure Pipelines

### 📦 Arquitetura

```
┌─────────────────────────────────────────────────────────┐
│  Azure DevOps (CI/CD)                                   │
│  ├─ CI: Build, Test, Push para ACR                      │
│  └─ CD: Deploy automático no ACI                        │
└─────────────────────────────────────────────────────────┘
                         ↓
┌─────────────────────────────────────────────────────────┐
│  Azure Cloud                                            │
│  ├─ Resource Group: rg-challenge3-rm556221              │
│  ├─ ACR: acrchallenge3rm556221.azurecr.io               │
│  ├─ Database: PostgreSQL (ACI)                          │
│  └─ Application: Mottu API (ACI)                        │
└─────────────────────────────────────────────────────────┘
```

### 🌐 Acesso

- **Execução Local:** `mvn spring-boot:run`
  - Backend (API): [http://localhost:8080/](http://localhost:8080/)  
  - Frontend (Web): [http://localhost:8080/home](http://localhost:8080/home)

- **Execução na Nuvem (Azure):**
  - URL fornecida após o deploy via pipeline

---

## ⚡ Quick Start - Azure Pipelines

### Passo 1: Setup da Infraestrutura (Uma vez)
```bash
./setup.sh
```

### Passo 2: Configurar Pipeline no Azure DevOps
1. Criar Service Connection para Azure
2. Criar Pipeline usando `azure-pipelines.yml`
3. Configurar variáveis (DB host, password, etc.)
4. Executar!

📚 **Guia Completo:** [QUICK-START-AZURE-PIPELINES.md](QUICK-START-AZURE-PIPELINES.md)

📖 **Documentação Detalhada:** [AZURE-DEVOPS-SETUP.md](AZURE-DEVOPS-SETUP.md)

---

## 2. Endpoints

### 2.1 Manutenção
| Método | Endpoint | Descrição | Permissão |
|--------|---------|-----------|-----------|
| GET    | `/manutencoes/listar` | Lista todas as manutenções | Admin e Operador |
| POST   | `/manutencoes/save` | Adiciona uma nova manutenção | Admin |
| PUT    | `/manutencoes/{id}` | Edita manutenção pelo ID | Admin |
| DELETE | `/manutencoes/{id}` | Exclui manutenção pelo ID | Admin |

### 2.2 Motos
| Método | Endpoint | Descrição | Permissão |
|--------|---------|-----------|-----------|
| GET    | `/motos/listar` | Lista todas as motos | Admin e Operador |
| POST   | `/motos/save` | Adiciona uma nova moto | Admin |
| PUT    | `/motos/{id}` | Edita moto pelo ID | Admin |
| DELETE | `/motos/{id}` | Exclui moto pelo ID | Admin |

### 2.3 Galpões
| Método | Endpoint | Descrição | Permissão |
|--------|---------|-----------|-----------|
| GET    | `/galpoes/listar` | Lista todos os galpões | Admin e Operador |
| POST   | `/galpoes/save` | Adiciona um novo galpão | Admin |
| PUT    | `/galpoes/{id}` | Edita galpão pelo ID | Admin |
| DELETE | `/galpoes/{id}` | Exclui galpão pelo ID | Admin |

### 2.4 Motoqueiros
| Método | Endpoint | Descrição | Permissão |
|--------|---------|-----------|-----------|
| GET    | `/motoqueiros/listar` | Lista todos os motoqueiros | Admin e Operador |
| POST   | `/motoqueiros/save` | Adiciona um novo motoqueiro | Admin |
| PUT    | `/motoqueiros/{id}` | Edita motoqueiro pelo ID | Admin |
| DELETE | `/motoqueiros/{id}` | Exclui motoqueiro pelo ID | Admin |

---

## 3. Regras de Permissão
- **Admin:** Pode listar, adicionar, editar e excluir todos os registros.  
- **Operador:** Pode apenas listar registros. Tentativas de adicionar, editar ou excluir resultam em **mensagem de erro de permissão** na aplicação web.  

---

## 4. Fluxo da Aplicação Web
1. Usuário acessa: [http://localhost:8080/home](http://localhost:8080/home)  
2. Tela inicial: **Login / Cadastro**  
3. Após login bem-sucedido:  
   - Redirecionamento para o **Dashboard**  
   - Dashboard exibe todos os grupos (**Motos, Galpões, Motoqueiros, Manutenção**)  
   - Usuário vê os dados conforme sua **permissão**  

**Interações:**  
- **Admin:** pode clicar em **Adicionar / Editar / Excluir**  
- **Operador:** apenas visualiza, sem ação nos botões CRUD  

---

## 5. Banco de Dados
- **Tipo:** PostgreSQL  
- **Versionamento e População Inicial:** Flyway  
- **Funcionalidade:**  
  - Cria tabelas automaticamente  
  - Popula dados padrão para testes iniciais  

---

## 6. Comandos Úteis

### Desenvolvimento Local
```bash
# Rodar a aplicação localmente
mvn spring-boot:run

# Rodar com Docker Compose (app + banco)
docker-compose up

# Build do projeto
mvn clean package

# Executar testes
mvn test
```

### Azure (Produção)
```bash
# Ver logs da aplicação
az container logs --resource-group rg-challenge3-rm556221 --name mottu-api-aci --follow

# Ver status do container
az container show --resource-group rg-challenge3-rm556221 --name mottu-api-aci

# Listar imagens no ACR
az acr repository list --name acrchallenge3rm556221 --output table

# Conectar ao banco de dados
psql -h <DB_IP> -U postgres -d mottu-api
```

---

## 7. Estrutura do Projeto

```
challenge-java-back-sprint-4/
├── src/
│   ├── main/
│   │   ├── java/com/mottu/mottu/
│   │   │   ├── controller/      # Controllers REST e Web
│   │   │   ├── model/           # Entidades e DTOs
│   │   │   ├── repository/      # Repositórios JPA
│   │   │   ├── service/         # Lógica de negócio
│   │   │   └── security/        # Configuração de segurança
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── db/migration/    # Scripts Flyway
│   │       ├── static/          # CSS
│   │       └── templates/       # HTML Thymeleaf
│   └── test/                    # Testes unitários
├── azure-pipelines.yml          # ⭐ Pipeline CI/CD completo
├── setup.sh                     # Setup de infraestrutura (uma vez)
├── Dockerfile                   # Imagem da aplicação
├── docker-compose.yml           # Desenvolvimento local
├── pom.xml                      # Dependências Maven
├── AZURE-DEVOPS-SETUP.md        # Guia completo Azure DevOps
├── QUICK-START-AZURE-PIPELINES.md  # Quick start CI/CD
└── SETUP-GUIDE.md               # Guia de infraestrutura
```

---

## 📚 Documentação Adicional

| Documento | Descrição |
|-----------|-----------|
| [QUICK-START-AZURE-PIPELINES.md](QUICK-START-AZURE-PIPELINES.md) | ⚡ Configuração rápida (5 minutos) |
| [AZURE-DEVOPS-SETUP.md](AZURE-DEVOPS-SETUP.md) | 📖 Guia completo do Azure DevOps |
| [SETUP-GUIDE.md](SETUP-GUIDE.md) | 🏗️ Guia de infraestrutura e CI/CD |

---

## 🎯 Fluxo CI/CD

```
Developer Push (main/develop)
          ↓
    CI Pipeline
    ├─ Checkout
    ├─ Build com Maven
    ├─ Testes unitários
    ├─ Build Docker
    └─ Push para ACR
          ↓
   (Se branch = main)
          ↓
    CD Pipeline
    ├─ Pull da imagem do ACR
    ├─ Deploy no ACI
    ├─ Health check
    └─ Aplicação disponível! ✅
```

---

## 🔐 Segurança

- ✅ Senhas gerenciadas como secrets no Azure DevOps
- ✅ Service Principal com permissões mínimas
- ✅ ACR com autenticação admin habilitada
- ✅ Container Instances com variáveis seguras
- ✅ Spring Security habilitado (roles ADMIN/OPERADOR)

---

## 🧪 Testes

```bash
# Executar todos os testes
mvn test

# Executar com cobertura
mvn clean test jacoco:report

# Ver relatório de cobertura
open target/site/jacoco/index.html
```

---

## 🤝 Contribuindo

1. Faça um fork do projeto
2. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request
6. Aguarde o CI passar ✅
7. Aguarde a revisão do código

---

## 📝 Licença

Este projeto foi desenvolvido para fins educacionais como parte do Challenge Mottu.

---

## 🎬 Vídeo de Apresentação

Vídeo demonstrativo do projeto:  
🎥 [https://www.youtube.com/watch?v=HGVIq_CFf2M](https://www.youtube.com/watch?v=HGVIq_CFf2M)

---

## 📞 Contato

Desenvolvido por **João** (RM 556221)

Para dúvidas sobre o projeto ou configuração do CI/CD, consulte a documentação ou abra uma issue.

---

**🚀 Happy Coding!**
