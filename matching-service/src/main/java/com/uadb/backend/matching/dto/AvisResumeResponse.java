package com.uadb.backend.matching.dto;

public record AvisResumeResponse(
    Long tuteurId,
    Double noteMoyenne,
    Long nombreAvis
) {}
