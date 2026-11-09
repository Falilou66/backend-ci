package com.uadb.backend.resource.controller;

import com.uadb.backend.resource.dto.SignalementRequest;
import com.uadb.backend.resource.dto.SignalementResponse;
import com.uadb.backend.resource.entity.StatutSignalement;
import com.uadb.backend.resource.service.SignalementService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class SignalementController {

    private final SignalementService service;

    public SignalementController(SignalementService service) {
        this.service = service;
    }

    @PostMapping("/ressources/{ressourceId}/signalements")
    @ResponseStatus(HttpStatus.CREATED)
    public SignalementResponse creer(
            @PathVariable Long ressourceId,
            @Valid @RequestBody SignalementRequest request) {
        return service.creer(ressourceId, request);
    }

    @GetMapping("/signalements")
    public List<SignalementResponse> lister(
            @RequestParam(required = false) StatutSignalement statut) {
        return service.lister(statut);
    }

    @GetMapping("/ressources/{ressourceId}/signalements")
    public List<SignalementResponse> parRessource(
            @PathVariable Long ressourceId) {
        return service.parRessource(ressourceId);
    }

    @PutMapping("/signalements/{id}/statut")
    public SignalementResponse traiter(
            @PathVariable Long id,
            @RequestParam StatutSignalement statut) {
        return service.traiter(id, statut);
    }
}
