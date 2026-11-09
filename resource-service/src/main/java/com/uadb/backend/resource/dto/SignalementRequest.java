package com.uadb.backend.resource.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record SignalementRequest(
        @NotNull @Positive Long utilisateurId,
        @NotBlank @Size(max = 100) String motif,
        @Size(max = 1000) String description
) {}
