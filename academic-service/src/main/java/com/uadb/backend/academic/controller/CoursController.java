package com.uadb.backend.academic.controller;

import com.uadb.backend.academic.dto.*;
import com.uadb.backend.academic.service.CoursService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cours")
public class CoursController {

    private final CoursService service;

    public CoursController(CoursService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CoursResponse creer(@Valid @RequestBody CoursRequest request) {
        return service.creer(request);
    }

    @GetMapping
    public Page<CoursResponse> lister(
        @RequestParam(required = false) Long filiereId,
        @RequestParam(required = false) Long niveauId,
        @RequestParam(required = false) String nom,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        return service.rechercher(filiereId, niveauId, nom, page, size);
    }

    @GetMapping("/{id}")
    public CoursResponse obtenir(@PathVariable Long id) {
        return service.obtenir(id);
    }

    @PutMapping("/{id}")
    public CoursResponse modifier(
        @PathVariable Long id,
        @Valid @RequestBody CoursRequest request
    ) {
        return service.modifier(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void supprimer(@PathVariable Long id) {
        service.supprimer(id);
    }
}
