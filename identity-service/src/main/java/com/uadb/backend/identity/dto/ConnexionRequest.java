package com.uadb.backend.identity.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ConnexionRequest(

        @NotBlank(message = "L'email est obligatoire")
        @Email(message = "Adresse email invalide")
        String email,

        @NotBlank(message = "Le mot de passe est obligatoire")
        String motDePasse

) {}
