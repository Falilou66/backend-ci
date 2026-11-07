package com.uadb.backend.resource.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RessourceRequest(
    @NotBlank @Size(max = 150) String titre,
    @Size(max = 1000) String description,
    @NotBlank @Size(max = 30) String type,
    @NotBlank @Size(max = 1000) String url,
    @NotNull Long coursId,
    @NotNull Long auteurId
) {}
