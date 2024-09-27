# Use uma imagem base do Maven para construir a aplicação
FROM maven:3.8.3-openjdk-17 as Build

# Define o diretório de trabalho dentro do container
WORKDIR /app

# Copia o arquivo pom.xml e o diretório src para o diretório de trabalho
COPY pom.xml .
COPY src ./src

# Executa o comando Maven para construir a aplicação
RUN mvn clean package -DskipTests

# Use uma imagem base do OpenJDK para executar a aplicação
FROM openjdk:17-jdk-slim

# Define o diretório de trabalho dentro do container
WORKDIR /app

# Copia o JAR construído da fase anterior para o diretório de trabalho
COPY --from=Build /app/target/*.jar app.jar

# Expõe a porta em que a aplicação será executada
EXPOSE 6969

# Define o comando padrão para executar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]
