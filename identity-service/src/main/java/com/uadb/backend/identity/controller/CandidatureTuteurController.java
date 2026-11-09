package com.uadb.backend.identity.controller;

import com.uadb.backend.identity.dto.*;
import com.uadb.backend.identity.service.CandidatureTuteurService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/tutorat/candidature")
public class CandidatureTuteurController {

    private final CandidatureTuteurService service;

    public CandidatureTuteurController(CandidatureTuteurService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ETUDIANT','ENSEIGNANT')")
    public CandidatureTuteurResponse deposer(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CandidatureTuteurRequest request) {
        return service.deposer(jwt.getClaim("userId"), request);
    }

    @GetMapping("/moi")
    @PreAuthorize("hasAnyRole('ETUDIANT','ENSEIGNANT','TUTEUR')")
    public List<CandidatureTuteurResponse> mesCandidatures(
            @AuthenticationPrincipal Jwt jwt) {
        return service.mesCandidatures(jwt.getClaim("userId"));
    }
}
