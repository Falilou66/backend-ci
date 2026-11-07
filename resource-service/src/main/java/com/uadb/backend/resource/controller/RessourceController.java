package com.uadb.backend.resource.controller;

import com.uadb.backend.resource.entity.RessourceEntity;
import com.uadb.backend.resource.dto.RessourceRequest;
import com.uadb.backend.resource.service.RessourceService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/ressources")
public class RessourceController {

    private final RessourceService service;

    public RessourceController(RessourceService service) {
        this.service = service;
    }

    @PostMapping
    public RessourceEntity creer(@Valid @RequestBody RessourceRequest request) {
        return service.creer(request);
    }

    @GetMapping
    public List<RessourceEntity> lister() {
        return service.lister();
    }

    @GetMapping("/cours/{coursId}")
    public List<RessourceEntity> parCours(@PathVariable Long coursId) {
        return service.trouverParCours(coursId);
    }

    @PutMapping("/{id}")
    public RessourceEntity modifier(
        @PathVariable Long id,
        @Valid @RequestBody RessourceRequest request) {
        return service.modifier(id, request);
    }

    @DeleteMapping("/{id}")
    public void supprimer(@PathVariable Long id) {
        service.supprimer(id);
    }
}
