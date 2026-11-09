#!/bin/bash
set -e

# 1. Dépendances Maven
cat > api-gateway/pom.xml <<'POM'
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">

    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>com.uadb.backend</groupId>
        <artifactId>backend-ci</artifactId>
        <version>1.0.0-SNAPSHOT</version>
        <relativePath>../pom.xml</relativePath>
    </parent>

    <artifactId>api-gateway</artifactId>

    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-gateway-server-webflux</artifactId>
            <version>5.0.4</version>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security-oauth2-resource-server</artifactId>
        </dependency>
    </dependencies>
</project>
POM

# 2. Configuration Gateway
cat > api-gateway/src/main/resources/application.yml <<'YAML'
server:
  port: 8080

spring:
  application:
    name: api-gateway

  cloud:
    gateway:
      server:
        webflux:
          routes:
            - id: identity-service
              uri: http://localhost:8081
              predicates:
                - Path=/api/v1/auth/**,/api/v1/admin/**,/api/v1/utilisateurs/**

            - id: academic-service
              uri: http://localhost:8082
              predicates:
                - Path=/api/v1/filieres/**,/api/v1/niveaux/**,/api/v1/cours/**

            - id: matching-service
              uri: http://localhost:8083
              predicates:
                - Path=/api/v1/matching/**

            - id: resource-service
              uri: http://localhost:8084
              predicates:
                - Path=/api/v1/ressources/**

app:
  jwt:
    secret: ${JWT_SECRET}
YAML

# 3. Configuration de sécurité réactive
mkdir -p api-gateway/src/main/java/com/uadb/backend/gateway/config

cat > api-gateway/src/main/java/com/uadb/backend/gateway/config/GatewaySecurityConfig.java <<'JAVA'
package com.uadb.backend.gateway.config;

import java.util.Base64;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;

import reactor.core.publisher.Flux;

@Configuration
public class GatewaySecurityConfig {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Bean
    public SecretKey gatewayJwtSecretKey() {
        byte[] keyBytes = Base64.getDecoder().decode(jwtSecret);

        if (keyBytes.length < 32) {
            throw new IllegalStateException(
                "La clé JWT doit contenir au moins 256 bits."
            );
        }

        return new SecretKeySpec(keyBytes, "HmacSHA256");
    }

    @Bean
    public ReactiveJwtDecoder reactiveJwtDecoder(
            SecretKey gatewayJwtSecretKey) {

        return NimbusReactiveJwtDecoder
                .withSecretKey(gatewayJwtSecretKey)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }

    @Bean
    public ReactiveJwtAuthenticationConverter jwtAuthenticationConverter() {

        JwtGrantedAuthoritiesConverter authoritiesConverter =
                new JwtGrantedAuthoritiesConverter();

        authoritiesConverter.setAuthoritiesClaimName("roles");
        authoritiesConverter.setAuthorityPrefix("ROLE_");

        ReactiveJwtAuthenticationConverter converter =
                new ReactiveJwtAuthenticationConverter();

        converter.setPrincipalClaimName(JwtClaimNames.SUB);
        converter.setJwtGrantedAuthoritiesConverter(
                jwt -> Flux.fromIterable(authoritiesConverter.convert(jwt))
        );

        return converter;
    }

    @Bean
    public SecurityWebFilterChain gatewaySecurityFilterChain(
            ServerHttpSecurity http,
            ReactiveJwtAuthenticationConverter jwtAuthenticationConverter) {

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .authorizeExchange(exchange -> exchange
                        .pathMatchers(
                                HttpMethod.POST,
                                "/api/v1/auth/inscription",
                                "/api/v1/auth/login"
                        ).permitAll()
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(
                                        jwtAuthenticationConverter
                                )
                        )
                )
                .build();
    }
}
JAVA

echo ""
echo "Configuration de sécurité de la Gateway créée."
