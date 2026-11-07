package com.uadb.backend.identity.controller;

import com.uadb.backend.identity.dto.UtilisateurAdminResponse;
import com.uadb.backend.identity.service.AdministrationService;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.uadb.backend.identity.dto.ModifierRolesRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMINISTRATEUR')")
public class AdministrationController {

    private final AdministrationService administrationService;

    public AdministrationController(
            AdministrationService administrationService) {
        this.administrationService = administrationService;
    }

    @GetMapping("/inscriptions/en-attente")
    public List<UtilisateurAdminResponse> listerInscriptionsEnAttente() {

        return administrationService.listerInscriptionsEnAttente();
    }
    @PutMapping("/inscriptions/{id}/valider")
    public UtilisateurAdminResponse validerInscription(
            @PathVariable Long id) {

        return administrationService.validerInscription(id);
    }
    @PutMapping("/inscriptions/{id}/refuser")
    public UtilisateurAdminResponse refuserInscription(
            @PathVariable Long id) {

        return administrationService.refuserInscription(id);
    }
    @PutMapping("/utilisateurs/{id}/roles")
public UtilisateurAdminResponse modifierRoles(
        @PathVariable Long id,
        @Valid @RequestBody ModifierRolesRequest request) {

    return administrationService.modifierRoles(id, request);
}
@GetMapping("/utilisateurs")
public Page<UtilisateurAdminResponse> listerUtilisateurs(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int taille) {

    Pageable pageable = PageRequest.of(page, taille);

    return administrationService.listerUtilisateurs(pageable);
}
@GetMapping("/utilisateurs/recherche")
public Page<UtilisateurAdminResponse> rechercherUtilisateurs(
        @RequestParam String recherche,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int taille) {

    Pageable pageable = PageRequest.of(page, taille);

    return administrationService.rechercherUtilisateurs(
            recherche,
            pageable
    );
}
}
