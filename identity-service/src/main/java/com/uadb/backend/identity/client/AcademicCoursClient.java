package com.uadb.backend.identity.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Component
public class AcademicCoursClient {

    private final RestClient restClient;

    public AcademicCoursClient(
        RestClient.Builder builder,
        @Value("${academic.service.url:http://localhost:8082}") String url
    ) {
        this.restClient = builder.baseUrl(url).build();
    }

    public void verifierCours(Long coursId) {
        try {
            CoursInfo cours = restClient.get()
                .uri("/api/v1/cours/{id}", coursId)
                .retrieve()
                .body(CoursInfo.class);

            if (cours == null || cours.id() == null) {
                throw new IllegalArgumentException("Cours introuvable : " + coursId);
            }
        } catch (RestClientResponseException e) {
            if (e.getStatusCode().is4xxClientError()) {
                throw new IllegalArgumentException("Le cours " + coursId + " n'existe pas.");
            }
            throw new IllegalStateException("Academic Service est momentanément indisponible.", e);
        }
    }

    private record CoursInfo(Long id, String nom) {}
}
