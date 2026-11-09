package com.uadb.backend.identity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DecisionCandidatureRequest(
    @NotBlank @Size(max = 1000) String motif
) {}
