package com.uadb.backend.identity.controller;

import com.uadb.backend.identity.dto.ChangerMotDePasseRequest;
import com.uadb.backend.identity.dto.LoginResponse;
import com.uadb.backend.identity.service.AuthentificationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class PremiereConnexionController {

    private final AuthentificationService authentificationService;

    public PremiereConnexionController(
            AuthentificationService authentificationService) {
        this.authentificationService = authentificationService;
    }

    @PutMapping("/premiere-connexion/mot-de-passe")
    public ResponseEntity<LoginResponse> changerMotDePasse(
            Authentication authentication,
            @Valid @RequestBody ChangerMotDePasseRequest request) {

        LoginResponse response = authentificationService.changerMotDePasse(
                authentication.getName(), request
        );

        return ResponseEntity.ok(response);
    }
}
