package com.uadb.backend.academic.controller;

import com.uadb.backend.academic.dto.FiliereRequest;
import com.uadb.backend.academic.dto.FiliereResponse;
import com.uadb.backend.academic.service.FiliereService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/filieres")
public class FiliereController {

    private final FiliereService service;

    public FiliereController(FiliereService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FiliereResponse creer(@Valid @RequestBody FiliereRequest request) {
        return service.creer(request);
    }

    @GetMapping
    public List<FiliereResponse> lister() {
        return service.lister();
    }

    @PutMapping("/{id}")
    public FiliereResponse modifier(
            @PathVariable Long id,
            @Valid @RequestBody FiliereRequest request) {
        return service.modifier(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void supprimer(@PathVariable Long id) {
        service.supprimer(id);
    }
}