package com.uadb.backend.notification.controller;

import com.uadb.backend.notification.dto.NotificationRequest;
import com.uadb.backend.notification.entity.NotificationEntity;
import com.uadb.backend.notification.service.NotificationService;
import jakarta.validation.Valid;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService service;
    private final String internalApiKey;

    public NotificationController(
            NotificationService service,
            @Value("${internal.api.key:}") String internalApiKey) {
        this.service = service;
        this.internalApiKey = internalApiKey;
    }

    @PostMapping
    public ResponseEntity<NotificationEntity> creer(
            @Valid @RequestBody NotificationRequest request,
            @RequestHeader(value = "X-Internal-Api-Key", required = false) String apiKey) {
        verifierCleInterne(apiKey);
        return ResponseEntity.status(HttpStatus.CREATED).body(service.creer(request));
    }

    @GetMapping("/utilisateur/{utilisateurId}")
    public List<NotificationEntity> lister(
            @PathVariable Long utilisateurId,
            @AuthenticationPrincipal Jwt jwt) {
        verifierProprietaire(utilisateurId, jwt);
        return service.lister(utilisateurId);
    }

    @GetMapping("/utilisateur/{utilisateurId}/non-lues")
    public List<NotificationEntity> listerNonLues(
            @PathVariable Long utilisateurId,
            @AuthenticationPrincipal Jwt jwt) {
        verifierProprietaire(utilisateurId, jwt);
        return service.listerNonLues(utilisateurId);
    }

    @GetMapping("/utilisateur/{utilisateurId}/compteur")
    public Map<String, Long> compterNonLues(
            @PathVariable Long utilisateurId,
            @AuthenticationPrincipal Jwt jwt) {
        verifierProprietaire(utilisateurId, jwt);
        return Map.of("nonLues", service.compterNonLues(utilisateurId));
    }

    @PatchMapping("/{id}/lue")
    public NotificationEntity marquerCommeLue(
            @PathVariable Long id,
            @RequestParam Long utilisateurId,
            @AuthenticationPrincipal Jwt jwt) {
        verifierProprietaire(utilisateurId, jwt);
        return service.marquerCommeLue(id, utilisateurId);
    }

    @PatchMapping("/utilisateur/{utilisateurId}/tout-lire")
    public ResponseEntity<Void> toutLire(
            @PathVariable Long utilisateurId,
            @AuthenticationPrincipal Jwt jwt) {
        verifierProprietaire(utilisateurId, jwt);
        service.marquerToutesCommeLues(utilisateurId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimer(
            @PathVariable Long id,
            @RequestParam Long utilisateurId,
            @AuthenticationPrincipal Jwt jwt) {
        verifierProprietaire(utilisateurId, jwt);
        service.supprimer(id, utilisateurId);
        return ResponseEntity.noContent().build();
    }

    private void verifierProprietaire(Long utilisateurId, Jwt jwt) {
        Object claim = jwt.getClaim("userId");
        if (claim == null || !claim.toString().equals(utilisateurId.toString())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Accès interdit aux notifications d'un autre utilisateur.");
        }
    }

    private void verifierCleInterne(String apiKey) {
        if (internalApiKey == null || internalApiKey.isBlank()
                || apiKey == null
                || !MessageDigest.isEqual(
                    internalApiKey.getBytes(StandardCharsets.UTF_8),
                    apiKey.getBytes(StandardCharsets.UTF_8))) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Clé API interne invalide.");
        }
    }
}
