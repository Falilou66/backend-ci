package com.uadb.backend.identity.controller;

import com.uadb.backend.identity.dto.InscriptionInstitutionnelleRequest;
import com.uadb.backend.identity.dto.InscriptionInstitutionnelleResponse;
import com.uadb.backend.identity.service.InscriptionInstitutionnelleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class InscriptionInstitutionnelleController {

    private final InscriptionInstitutionnelleService service;

    public InscriptionInstitutionnelleController(
            InscriptionInstitutionnelleService service) {
        this.service = service;
    }

    @PostMapping("/inscription-institutionnelle")
    public ResponseEntity<InscriptionInstitutionnelleResponse> inscrire(
            @Valid @RequestBody InscriptionInstitutionnelleRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.inscrire(request));
    }
}
