package com.uadb.backend.matching.controller;

import java.util.List;

import com.uadb.backend.matching.dto.DisponibiliteRequest;
import com.uadb.backend.matching.dto.DisponibiliteResponse;
import com.uadb.backend.matching.service.DisponibiliteService;

import jakarta.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/matching/tuteurs/moi/disponibilites")
@PreAuthorize("hasRole('TUTEUR')")
public class DisponibiliteController {

    private final DisponibiliteService service;

    public DisponibiliteController(DisponibiliteService service) {
        this.service = service;
    }

    @GetMapping
    public List<DisponibiliteResponse> lister(@AuthenticationPrincipal Jwt jwt) {
        return service.lister(utilisateurId(jwt));
    }

    @PostMapping
    public DisponibiliteResponse ajouter(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody DisponibiliteRequest request) {
        return service.ajouter(utilisateurId(jwt), request);
    }

    @PutMapping("/{id}")
    public DisponibiliteResponse modifier(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long id,
            @Valid @RequestBody DisponibiliteRequest request) {
        return service.modifier(utilisateurId(jwt), id, request);
    }

    @DeleteMapping("/{id}")
    public void supprimer(
            @AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
        service.supprimer(utilisateurId(jwt), id);
    }

    private Long utilisateurId(Jwt jwt) {
        Object value = jwt.getClaim("userId");
        if (value instanceof Number number) return number.longValue();
        throw new IllegalStateException("Le token JWT ne contient pas userId.");
    }
}
