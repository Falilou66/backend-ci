package com.uadb.backend.academic.dto;

import jakarta.validation.constraints.*;

public record CoursRequest(
    @NotBlank @Size(max = 150) String nom,
    @Size(max = 500) String description,
    @NotNull @Positive Integer volumeHoraire,
    @NotNull Long filiereId,
    @NotNull Long niveauId
) {}
