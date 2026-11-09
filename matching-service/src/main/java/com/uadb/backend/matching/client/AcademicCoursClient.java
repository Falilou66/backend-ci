package com.uadb.backend.matching.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.server.ResponseStatusException;

@Component
public class AcademicCoursClient {

    private final RestClient restClient;

    public AcademicCoursClient(
        RestClient.Builder builder,
        @Value("${academic.service.url:http://localhost:8082}") String url
    ) {
        this.restClient = builder.baseUrl(url).build();
    }

    public CoursInfo obtenirCours(Long coursId) {
        try {
            CoursInfo cours = restClient.get()
                .uri("/api/v1/cours/{id}", coursId)
                .retrieve()
                .body(CoursInfo.class);

            if (cours == null || cours.id() == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cours introuvable");
            }
            return cours;
        } catch (RestClientResponseException e) {
            if (e.getStatusCode().is4xxClientError()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Le cours " + coursId + " n'existe pas.");
            }
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                "Academic Service est momentanément indisponible.");
        }
    }

    public void verifierCours(Long coursId) {
        obtenirCours(coursId);
    }

    public record CoursInfo(
        Long id,
        String nom,
        String description,
        Integer volumeHoraire,
        Long filiereId,
        String filiereNom,
        Long niveauId,
        String niveauLibelle
    ) {}
}
