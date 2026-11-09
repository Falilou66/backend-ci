package com.uadb.backend.resource.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Optional;

@Component
public class AcademicClient {

    private final RestClient client;

    public AcademicClient(
            @Value("${ACADEMIC_SERVICE_URL:http://localhost:8082}") String url) {
        this.client = RestClient.create(url);
    }

    public Optional<CoursInfo> obtenirCours(Long coursId) {
        try {
            CoursInfo cours = client.get()
                    .uri("/api/v1/cours/{id}", coursId)
                    .retrieve()
                    .body(CoursInfo.class);

            return Optional.ofNullable(cours);
        } catch (RestClientException e) {
            System.err.println("Impossible de récupérer le cours : " + e.getMessage());
            return Optional.empty();
        }
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
