
package com.uadb.backend.identity.controller;

import com.uadb.backend.identity.dto.LoginRequest;
import com.uadb.backend.identity.dto.LoginResponse;
import com.uadb.backend.identity.service.AuthentificationService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthentificationController {

    private final AuthentificationService authentificationService;

    public AuthentificationController(
            AuthentificationService authentificationService) {
        this.authentificationService = authentificationService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> connecter(
            @Valid @RequestBody LoginRequest request) {

        LoginResponse response =
                authentificationService.connecter(request);

        return ResponseEntity.ok(response);
    }
}
