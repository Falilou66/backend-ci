package com.uadb.backend.identity.controller;

import com.uadb.backend.identity.dto.InscriptionRequest;
import com.uadb.backend.identity.dto.LoginRequest;
import com.uadb.backend.identity.dto.LoginResponse;
import com.uadb.backend.identity.service.AuthentificationService;
import com.uadb.backend.identity.service.UtilisateurService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class InscriptionController {

    private final UtilisateurService utilisateurService;
    private final AuthentificationService authentificationService;

    public InscriptionController(
            UtilisateurService utilisateurService,
            AuthentificationService authentificationService) {
        this.utilisateurService = utilisateurService;
        this.authentificationService = authentificationService;
    }

    @PostMapping("/inscription")
    public ResponseEntity<LoginResponse> inscrire(
            @Valid @RequestBody InscriptionRequest request) {

        utilisateurService.inscrire(request);
        LoginResponse response = authentificationService.connecter(
                new LoginRequest(request.email(), request.motDePasse())
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}