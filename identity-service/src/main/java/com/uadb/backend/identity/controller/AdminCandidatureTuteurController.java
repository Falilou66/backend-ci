package com.uadb.backend.identity.controller;

import com.uadb.backend.identity.dto.*;
import com.uadb.backend.identity.service.CandidatureTuteurService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/candidatures-tuteurs")
@PreAuthorize("hasRole('ADMINISTRATEUR')")
public class AdminCandidatureTuteurController {

    private final CandidatureTuteurService service;

    public AdminCandidatureTuteurController(CandidatureTuteurService service) {
        this.service = service;
    }

    @GetMapping("/en-attente")
    public List<CandidatureTuteurResponse> enAttente() {
        return service.candidaturesEnAttente();
    }

    @PutMapping("/{id}/approuver")
    public CandidatureTuteurResponse approuver(@PathVariable Long id) {
        return service.approuver(id);
    }

    @PutMapping("/{id}/refuser")
    public CandidatureTuteurResponse refuser(
            @PathVariable Long id,
            @Valid @RequestBody DecisionCandidatureRequest request) {
        return service.refuser(id, request);
    }
}
