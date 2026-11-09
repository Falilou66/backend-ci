package com.uadb.backend.resource.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI resourceServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Resource Service API")
                        .version("1.0.0")
                        .description("Gestion des ressources pédagogiques, fichiers (MinIO), signalements")
                        .contact(new Contact()
                                .name("UADB Backend Team")
                                .email("backend@uadb.edu")))
                .servers(List.of(new Server().url("http://localhost:8084").description("Local server")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }
}