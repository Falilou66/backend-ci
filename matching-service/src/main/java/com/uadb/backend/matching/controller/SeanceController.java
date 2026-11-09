package com.uadb.backend.matching.controller;

import java.util.List;
import com.uadb.backend.matching.dto.*;
import com.uadb.backend.matching.service.SeanceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/matching/seances")
public class SeanceController {

    private final SeanceService service;

    public SeanceController(SeanceService service) {
        this.service = service;
    }

    private Long utilisateurId(Jwt jwt) {
        Object id = jwt.getClaim("userId");
        if (id instanceof Number number) return number.longValue();
        throw new IllegalStateException("Identifiant utilisateur absent du JWT");
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ETUDIANT','ENSEIGNANT')")
    public SeanceResponse demander(
        @AuthenticationPrincipal Jwt jwt,
        @Valid @RequestBody DemandeSeanceRequest request
    ) {
        return service.demander(utilisateurId(jwt), request);
    }

    @GetMapping("/mes-demandes")
    @PreAuthorize("hasAnyRole('ETUDIANT','ENSEIGNANT')")
    public List<SeanceResponse> mesDemandes(@AuthenticationPrincipal Jwt jwt) {
        return service.mesSeancesEtudiant(utilisateurId(jwt));
    }

    @GetMapping("/mes-seances-tuteur")
    @PreAuthorize("hasRole('TUTEUR')")
    public List<SeanceResponse> mesSeancesTuteur(@AuthenticationPrincipal Jwt jwt) {
        return service.mesSeancesTuteur(utilisateurId(jwt));
    }

    @PutMapping("/{id}/accepter")
    @PreAuthorize("hasRole('TUTEUR')")
    public SeanceResponse accepter(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
        return service.accepter(utilisateurId(jwt), id);
    }

    @PutMapping("/{id}/refuser")
    @PreAuthorize("hasRole('TUTEUR')")
    public SeanceResponse refuser(
        @AuthenticationPrincipal Jwt jwt,
        @PathVariable Long id,
        @Valid @RequestBody(required = false) DecisionSeanceRequest request
    ) {
        return service.refuser(utilisateurId(jwt), id, request);
    }

    @PutMapping("/{id}/annuler")
    public SeanceResponse annuler(
        @AuthenticationPrincipal Jwt jwt,
        @PathVariable Long id,
        @Valid @RequestBody(required = false) DecisionSeanceRequest request
    ) {
        return service.annuler(utilisateurId(jwt), id, request);
    }

    @PutMapping("/{id}/terminer")
    @PreAuthorize("hasRole('TUTEUR')")
    public SeanceResponse terminer(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
        return service.terminer(utilisateurId(jwt), id);
    }
}
