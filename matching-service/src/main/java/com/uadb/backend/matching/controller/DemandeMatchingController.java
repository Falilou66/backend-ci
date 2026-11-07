package com.uadb.backend.matching.controller;

import com.uadb.backend.matching.entity.DemandeMatchingEntity;
import com.uadb.backend.matching.dto.*;
import com.uadb.backend.matching.service.DemandeMatchingService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/matching/demandes")
public class DemandeMatchingController {

    private final DemandeMatchingService service;

    public DemandeMatchingController(DemandeMatchingService service) {
        this.service = service;
    }

    @PostMapping
    public DemandeMatchingEntity creer(@Valid @RequestBody DemandeMatchingRequest request) {
        return service.creer(request);
    }

    @GetMapping("/etudiant/{id}")
    public List<DemandeMatchingEntity> demandesEtudiant(@PathVariable Long id) {
        return service.trouverParEtudiant(id);
    }

    @GetMapping("/tuteur/{id}/en-attente")
    public List<DemandeMatchingEntity> demandesTuteur(@PathVariable Long id) {
        return service.trouverDemandesEnAttente(id);
    }

    @PatchMapping("/{id}/decision")
    public DemandeMatchingEntity decider(
            @PathVariable Long id,
            @Valid @RequestBody DecisionRequest request) {
        return service.decider(id, request.accepter());
    }
}
