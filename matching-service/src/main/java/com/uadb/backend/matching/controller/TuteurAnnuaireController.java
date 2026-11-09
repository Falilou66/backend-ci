package com.uadb.backend.matching.controller;

import com.uadb.backend.matching.dto.ModifierProfilTuteurRequest;
import com.uadb.backend.matching.dto.TuteurProfilResponse;
import com.uadb.backend.matching.service.TuteurAnnuaireService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/matching/tuteurs")
public class TuteurAnnuaireController {

    private final TuteurAnnuaireService service;

    public TuteurAnnuaireController(TuteurAnnuaireService service) {
        this.service = service;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ETUDIANT','ENSEIGNANT','TUTEUR','ADMINISTRATEUR')")
    public Page<TuteurProfilResponse> rechercher(
        @RequestParam(required = false) Long coursId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        int pageSize = Math.min(Math.max(size, 1), 50);
        Pageable pageable = PageRequest.of(
            Math.max(page, 0), pageSize, Sort.by(Sort.Direction.DESC, "dateCreation"));
        return service.rechercher(coursId, pageable);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ETUDIANT','ENSEIGNANT','TUTEUR','ADMINISTRATEUR')")
    public TuteurProfilResponse consulter(@PathVariable Long id) {
        return service.consulter(id);
    }

    @GetMapping("/moi")
    @PreAuthorize("hasRole('TUTEUR')")
    public TuteurProfilResponse monProfil(@AuthenticationPrincipal Jwt jwt) {
        return service.consulterMonProfil(utilisateurId(jwt));
    }

    @PutMapping("/moi")
    @PreAuthorize("hasRole('TUTEUR')")
    public TuteurProfilResponse modifierMonProfil(
        @AuthenticationPrincipal Jwt jwt,
        @Valid @RequestBody ModifierProfilTuteurRequest request
    ) {
        return service.modifierMonProfil(utilisateurId(jwt), request);
    }

    private Long utilisateurId(Jwt jwt) {
        Object value = jwt.getClaim("userId");
        if (value instanceof Number number) return number.longValue();
        throw new IllegalStateException("Le token JWT ne contient pas userId.");
    }
}
