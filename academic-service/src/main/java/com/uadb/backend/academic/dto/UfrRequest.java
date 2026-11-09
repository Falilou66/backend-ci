package com.uadb.backend.academic.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UfrRequest(
    @NotBlank(message = "Le nom de l'UFR est obligatoire.")
    @Size(max = 100)
    String nom
) {}
