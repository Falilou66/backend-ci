package com.uadb.backend.identity.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import java.util.List;

@Component
public class MatchingTuteurClient {

    private final RestClient restClient;

    @Value("${internal.api.key:}")
    private String internalApiKey;

    public MatchingTuteurClient(
        RestClient.Builder builder,
        @Value("${matching.service.url:http://localhost:8083}") String url
    ) {
        this.restClient = builder.baseUrl(url).build();
    }

    public void provisionner(Long utilisateurId, String description, List<Long> coursIds) {
        if (internalApiKey == null || internalApiKey.isBlank()) {
            throw new IllegalStateException("La clé INTERNAL_API_KEY n'est pas configurée.");
        }

        restClient.post()
            .uri("/api/v1/matching/interne/tuteurs")
            .header("X-Internal-Api-Key", internalApiKey)
            .body(new ProvisionnerRequest(utilisateurId, description, coursIds))
            .retrieve()
            .toBodilessEntity();
    }

    private record ProvisionnerRequest(
        Long utilisateurId,
        String description,
        List<Long> coursIds
    ) {}
}
