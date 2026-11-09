package com.uadb.backend.identity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangerMotDePasseRequest(
        @NotBlank
        @Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères.")
        String nouveauMotDePasse
) {}
