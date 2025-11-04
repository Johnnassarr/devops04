# =============================================================================
# Dockerfile para Aplicação Java Spring Boot - Mottu API
# =============================================================================
# Este Dockerfile cria uma imagem otimizada usando multi-stage build
# e executa a aplicação como usuário não-root (requisito 8.2)
# =============================================================================

# -----------------------------------------------------------------------------
# STAGE 1: BUILD
# -----------------------------------------------------------------------------
# Usa imagem oficial do Maven com Java 17 para compilar a aplicação
FROM maven:3.9.6-eclipse-temurin-17-alpine AS builder

# Define diretório de trabalho
WORKDIR /app

# Copia os arquivos de configuração do Maven
COPY pom.xml .

# Baixa as dependências (camada cacheável)
RUN mvn dependency:go-offline -B

# Copia o código fonte
COPY src ./src

# Compila a aplicação (pula os testes para build mais rápido)
RUN mvn clean package -DskipTests -B

# -----------------------------------------------------------------------------
# STAGE 2: RUNTIME
# -----------------------------------------------------------------------------
# Usa imagem oficial JRE minimalista para executar a aplicação
FROM eclipse-temurin:17-jre-alpine

# Metadados da imagem
LABEL maintainer="DevOps Challenge 3 - RM556221"
LABEL description="Mottu API - Sistema de Gerenciamento de Motos e Motoqueiros"
LABEL version="1.0"

# Cria usuário não-root para executar a aplicação (REQUISITO 8.2)
RUN addgroup -S spring && adduser -S spring -G spring

# Define diretório de trabalho
WORKDIR /app

# Copia o JAR compilado do stage anterior
COPY --from=builder /app/target/*.jar app.jar

# Define permissões para o usuário spring
RUN chown -R spring:spring /app

# Muda para o usuário não-root
USER spring

# Expõe a porta da aplicação
EXPOSE 8080

# Variáveis de ambiente (podem ser sobrescritas no deploy)
ENV SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/mottu-api
ENV SPRING_DATASOURCE_USERNAME=postgres
ENV SPRING_DATASOURCE_PASSWORD=1234
ENV JAVA_OPTS="-Xms512m -Xmx1024m"

# Healthcheck para verificar se a aplicação está rodando
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Comando para iniciar a aplicação
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
