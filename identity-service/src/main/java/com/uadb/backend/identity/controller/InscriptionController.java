package com.uadb.backend.identity.controller;

import com.uadb.backend.identity.dto.InscriptionRequest;
import com.uadb.backend.identity.dto.UtilisateurResponse;
import com.uadb.backend.identity.service.UtilisateurService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class InscriptionController {

    private final UtilisateurService utilisateurService;

    public InscriptionController(UtilisateurService utilisateurService) {
        this.utilisateurService = utilisateurService;
    }

    @PostMapping("/inscription")
    public ResponseEntity<UtilisateurResponse> inscrireEtudiant(
            @Valid @RequestBody InscriptionRequest request) {

        UtilisateurResponse response =
                utilisateurService.inscrireEtudiant(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}