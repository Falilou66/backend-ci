
FROM eclipse-temurin:25-jdk-alpine AS builder

ARG SERVICE

RUN apk add --no-cache maven

WORKDIR /app

COPY . .

RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -pl ${SERVICE} -am clean package -DskipTests



FROM eclipse-temurin:25-jre-alpine


ARG SERVICE


RUN addgroup -S app && adduser -S app -G app

WORKDIR /app


COPY --from=builder /app/${SERVICE}/target/*.jar app.jar

USER app


ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75"

ENTRYPOINT ["java", "-jar", "app.jar"]