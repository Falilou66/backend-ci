package com.uadb.backend.academic.controller;

import com.uadb.backend.academic.dto.*;
import com.uadb.backend.academic.service.CoursService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public List<CoursResponse> lister() {
        return service.lister();
    }
}
