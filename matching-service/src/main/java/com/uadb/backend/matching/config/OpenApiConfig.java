package com.uadb.backend.matching.config;

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
    public OpenAPI matchingServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Matching Service API")
                        .version("1.0.0")
                        .description("Service de matching tutorat - tuteurs, séances, disponibilités, avis")
                        .contact(new Contact()
                                .name("UADB Backend Team")
                                .email("backend@uadb.edu")))
                .servers(List.of(new Server().url("http://localhost:8083").description("Local server")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }
}