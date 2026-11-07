package com.uadb.backend.academic.dto;

import jakarta.validation.constraints.NotBlank;

public record NiveauRequest(
        @NotBlank String code,
        @NotBlank String libelle
) {}