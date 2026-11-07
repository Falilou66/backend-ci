package com.uadb.backend.identity.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/acces")
public class AccesController {

    @GetMapping("/etudiant")
    @PreAuthorize("hasRole('ETUDIANT')")
    public Map<String, String> espaceEtudiant() {
        return Map.of(
                "message", "Bienvenue dans l'espace étudiant.",
                "acces", "AUTORISE"
        );
    }

    @GetMapping("/enseignant")
    @PreAuthorize("hasRole('ENSEIGNANT')")
    public Map<String, String> espaceEnseignant() {
        return Map.of(
                "message", "Bienvenue dans l'espace enseignant.",
                "acces", "AUTORISE"
        );
    }

    @GetMapping("/administrateur")
    @PreAuthorize("hasRole('ADMINISTRATEUR')")
    public Map<String, String> espaceAdministrateur() {
        return Map.of(
                "message", "Bienvenue dans l'espace administrateur.",
                "acces", "AUTORISE"
        );
    }
}
