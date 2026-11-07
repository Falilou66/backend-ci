package com.uadb.backend.academic.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record FiliereRequest(

        @NotBlank(message = "Le nom de la filière est obligatoire.")
        @Size(max = 100)
        String nom,

        @Size(max = 500)
        String description

) {}