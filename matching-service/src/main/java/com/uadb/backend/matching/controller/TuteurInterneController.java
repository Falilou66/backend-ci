package com.uadb.backend.matching.controller;

import com.uadb.backend.matching.dto.ProvisionnerTuteurRequest;
import com.uadb.backend.matching.service.TuteurInterneService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@RestController
@RequestMapping("/api/v1/matching/interne")
public class TuteurInterneController {

    private final TuteurInterneService service;

    @Value("${internal.api.key:}")
    private String internalApiKey;

    public TuteurInterneController(TuteurInterneService service) {
        this.service = service;
    }

    @PostMapping("/tuteurs")
    @ResponseStatus(HttpStatus.CREATED)
    public void provisionner(
            @RequestHeader(
                value = "X-Internal-Api-Key",
                required = false
            ) String apiKey,
            @Valid @RequestBody ProvisionnerTuteurRequest request) {

        if (internalApiKey == null || internalApiKey.isBlank()
                || apiKey == null
                || !MessageDigest.isEqual(
                    internalApiKey.getBytes(StandardCharsets.UTF_8),
                    apiKey.getBytes(StandardCharsets.UTF_8))) {
            throw new ResponseStatusException(
                HttpStatus.FORBIDDEN, "Accès interservice refusé."
            );
        }

        service.provisionner(request);
    }
}
