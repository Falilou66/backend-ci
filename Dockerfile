# Dockerfile commun à tous les microservices du backend
# Usage : docker build --build-arg SERVICE=identity-service -t jangalma-identity-service .
# Le contexte de build est la RACINE du projet, pour que Maven voie le pom parent.

# ---------- Étage 1 : compilation avec Maven ----------
FROM eclipse-temurin:25-jdk-alpine AS builder

# Nom du module Maven à construire (eureka-server, api-gateway, identity-service, ...)
ARG SERVICE

RUN apk add --no-cache maven

WORKDIR /app

# On copie tout le projet : le pom parent + tous les modules
COPY . .

# On compile UNIQUEMENT le service demandé (et ce dont il dépend)
# Le cache garde les dépendances Maven entre les builds des 7 services
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -pl ${SERVICE} -am clean package -DskipTests

# ---------- Étage 2 : image finale, légère ----------
FROM eclipse-temurin:25-jre-alpine

# Il faut redéclarer l'argument : chaque étage a ses propres ARG
ARG SERVICE

# Correctif de sécurité : applique les mises à jour des paquets Alpine (comme pour le frontend)
RUN apk upgrade --no-cache

# Utilisateur sans privilèges (sécurité : on n'exécute pas l'appli en root)
RUN addgroup -S app && adduser -S app -G app

WORKDIR /app

# On récupère UNIQUEMENT le .jar produit à l'étage 1
COPY --from=builder /app/${SERVICE}/target/*.jar app.jar

USER app

# Java utilise au plus 75 % de la mémoire accordée au conteneur
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75"

ENTRYPOINT ["java", "-jar", "app.jar"]
