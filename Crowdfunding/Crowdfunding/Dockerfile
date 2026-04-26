# Étape 1: Build avec Maven (optionnel, si tu veux builder dans le conteneur)
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copier uniquement les fichiers de dépendances d'abord (pour profiter du cache Docker)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copier le code source et builder
COPY src ./src
RUN mvn clean package -DskipTests

# Étape 2: Image d'exécution légère
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copier le JAR depuis l'étape de build
COPY --from=build /app/target/*.jar app.jar

# Port exposé (à adapter selon ton application, ex: 8080)
EXPOSE 8084

# Commande de démarrage
ENTRYPOINT ["java", "-jar", "app.jar"]