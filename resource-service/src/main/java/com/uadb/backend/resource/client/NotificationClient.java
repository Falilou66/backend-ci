package com.uadb.backend.resource.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class NotificationClient {

    private final RestClient client;
    private final String internalApiKey;

    public NotificationClient(
            @Value("${NOTIFICATION_SERVICE_URL:http://localhost:8085}") String url,
            @Value("${INTERNAL_API_KEY:}") String internalApiKey) {
        this.client = RestClient.create(url);
        this.internalApiKey = internalApiKey;
    }

    public void notifier(Long utilisateurId, String type, String titre,
                         String message, String lien) {
        try {
            client.post()
                    .uri("/api/v1/notifications")
                    .header("X-Internal-Api-Key", internalApiKey)
                    .body(new NotificationPayload(
                            utilisateurId, type, titre, message, lien))
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException e) {
            System.err.println("Notification non envoyée : " + e.getMessage());
        }
    }

    private record NotificationPayload(
            Long utilisateurId,
            String type,
            String titre,
            String message,
            String lien
    ) {}
}
