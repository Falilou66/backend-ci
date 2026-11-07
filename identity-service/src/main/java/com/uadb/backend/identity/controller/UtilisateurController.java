package com.uadb.backend.identity.controller;

import com.uadb.backend.identity.dto.ProfilUtilisateurResponse;
import com.uadb.backend.identity.service.UtilisateurService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import com.uadb.backend.identity.dto.ModifierProfilRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/v1/utilisateurs")
public class UtilisateurController {

    private final UtilisateurService utilisateurService;

    public UtilisateurController(UtilisateurService utilisateurService) {
        this.utilisateurService = utilisateurService;
    }

    @GetMapping("/me")
    public ResponseEntity<ProfilUtilisateurResponse> consulterProfil(
            @AuthenticationPrincipal Jwt jwt) {

        ProfilUtilisateurResponse profil =
                utilisateurService.consulterProfil(jwt.getSubject());

        return ResponseEntity.ok(profil);
    }

    @PutMapping("/me")
public ResponseEntity<ProfilUtilisateurResponse> modifierProfil(
        @AuthenticationPrincipal Jwt jwt,
        @Valid @RequestBody ModifierProfilRequest request) {

    ProfilUtilisateurResponse profil =
            utilisateurService.modifierProfil(
                    jwt.getSubject(),
                    request
            );

    return ResponseEntity.ok(profil);
}
}
