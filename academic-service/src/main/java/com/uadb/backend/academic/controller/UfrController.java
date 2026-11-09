package com.uadb.backend.academic.controller;

import com.uadb.backend.academic.dto.*;
import com.uadb.backend.academic.service.UfrService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/ufrs")
public class UfrController {
    private final UfrService service;

    public UfrController(UfrService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UfrResponse creer(@Valid @RequestBody UfrRequest request) {
        return service.creer(request);
    }

    @GetMapping
    public List<UfrResponse> lister() {
        return service.lister();
    }

    @GetMapping("/{id}")
    public UfrResponse obtenir(@PathVariable Long id) {
        return service.obtenir(id);
    }

    @PutMapping("/{id}")
    public UfrResponse modifier(
        @PathVariable Long id,
        @Valid @RequestBody UfrRequest request
    ) {
        return service.modifier(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void supprimer(@PathVariable Long id) {
        service.supprimer(id);
    }
}
