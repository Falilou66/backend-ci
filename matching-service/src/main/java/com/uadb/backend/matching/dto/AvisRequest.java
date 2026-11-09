package com.uadb.backend.matching.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;

public record AvisRequest(
    @NotNull @Min(1) @Max(5) Integer note,
    @Size(max = 2000) String commentaire
) {}
