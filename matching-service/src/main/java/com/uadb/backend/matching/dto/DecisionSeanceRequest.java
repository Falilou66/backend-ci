package com.uadb.backend.matching.dto;

import jakarta.validation.constraints.Size;

public record DecisionSeanceRequest(
    @Size(max = 1000) String motif
) {}
