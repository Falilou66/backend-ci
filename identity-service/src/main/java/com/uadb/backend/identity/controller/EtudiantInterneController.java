package com.uadb.backend.identity.controller;

import com.uadb.backend.identity.enums.StatutUtilisateur;
import com.uadb.backend.identity.repository.EtudiantRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;

@RestController
@RequestMapping("/api/v1/utilisateurs/etudiants")
public class EtudiantInterneController {

    private final EtudiantRepository repository;
    private final String internalApiKey;

    public EtudiantInterneController(
            EtudiantRepository repository,
            @Value("${internal.api.key:}") String internalApiKey) {
        this.repository = repository;
        this.internalApiKey = internalApiKey;
    }

    @GetMapping("/destinataires")
    public ResponseEntity<List<Long>> trouverDestinataires(
            @RequestHeader(value = "X-Internal-Api-Key", required = false)
            String apiKey,
            @RequestParam Long filiereId,
            @RequestParam Long niveauId) {

        if (internalApiKey.isBlank() || apiKey == null ||
                !MessageDigest.isEqual(
                        internalApiKey.getBytes(StandardCharsets.UTF_8),
                        apiKey.getBytes(StandardCharsets.UTF_8))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<Long> ids = repository.trouverIdsActifsParFiliereEtNiveau(
                filiereId,
                niveauId,
                StatutUtilisateur.ACTIF
        );

        return ResponseEntity.ok(ids);
    }
}
