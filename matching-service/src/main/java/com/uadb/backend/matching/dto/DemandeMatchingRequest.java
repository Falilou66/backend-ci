package com.uadb.backend.matching.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record DemandeMatchingRequest(
        @NotNull Long etudiantId,
        @NotNull Long tuteurId,
        @NotNull Long coursId,
        @Size(max = 500) String message
) {}
