package com.uadb.backend.academic.controller;

import com.uadb.backend.academic.dto.*;
import com.uadb.backend.academic.service.NiveauService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/niveaux")
public class NiveauController {
    private final NiveauService service;

    public NiveauController(NiveauService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NiveauResponse creer(@Valid @RequestBody NiveauRequest request) {
        return service.creer(request);
    }

    @GetMapping
    public List<NiveauResponse> lister() {
        return service.lister();
    }

    @PutMapping("/{id}")
    public NiveauResponse modifier(
        @PathVariable Long id,
        @Valid @RequestBody NiveauRequest request
    ) {
        return service.modifier(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void supprimer(@PathVariable Long id) {
        service.supprimer(id);
    }
}
