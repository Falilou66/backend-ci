package com.uadb.backend.resource.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;

@Component
public class IdentityClient {

    private final RestClient client;
    private final String internalApiKey;

    public IdentityClient(
            @Value("${IDENTITY_SERVICE_URL:http://localhost:8081}") String url,
            @Value("${INTERNAL_API_KEY:}") String internalApiKey) {
        this.client = RestClient.create(url);
        this.internalApiKey = internalApiKey;
    }

    public List<Long> trouverEtudiants(Long filiereId, Long niveauId) {
        if (internalApiKey.isBlank()) {
            System.err.println("INTERNAL_API_KEY absente : notifications ignorées.");
            return List.of();
        }

        try {
            List<Long> ids = client.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/v1/utilisateurs/etudiants/destinataires")
                            .queryParam("filiereId", filiereId)
                            .queryParam("niveauId", niveauId)
                            .build())
                    .header("X-Internal-Api-Key", internalApiKey)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<Long>>() {});

            return ids == null ? List.of() : ids;
        } catch (RestClientException e) {
            System.err.println("Impossible de récupérer les étudiants : " + e.getMessage());
            return List.of();
        }
    }
}
