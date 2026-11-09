package com.uadb.backend.matching.controller;

import java.util.List;

import com.uadb.backend.matching.dto.*;
import com.uadb.backend.matching.service.AvisService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/matching")
public class AvisController {

    private final AvisService avisService;

    public AvisController(AvisService avisService) {
        this.avisService = avisService;
    }

    private Long utilisateurId(Jwt jwt) {
        Object id = jwt.getClaim("userId");
        if (id instanceof Number number) return number.longValue();
        throw new IllegalStateException("Identifiant utilisateur absent du JWT");
    }

    @PostMapping("/seances/{seanceId}/avis")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ETUDIANT','ENSEIGNANT')")
    public AvisResponse deposer(
        @AuthenticationPrincipal Jwt jwt,
        @PathVariable Long seanceId,
        @Valid @RequestBody AvisRequest request
    ) {
        return avisService.deposer(utilisateurId(jwt), seanceId, request);
    }

    @GetMapping("/tuteurs/{tuteurId}/avis")
    public List<AvisResponse> lister(@PathVariable Long tuteurId) {
        return avisService.listerParTuteur(tuteurId);
    }

    @GetMapping("/tuteurs/{tuteurId}/avis/resume")
    public AvisResumeResponse resume(@PathVariable Long tuteurId) {
        return avisService.resume(tuteurId);
    }
}
