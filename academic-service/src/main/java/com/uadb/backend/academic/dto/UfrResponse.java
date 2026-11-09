package com.uadb.backend.academic.dto;

import java.time.LocalDateTime;

public record UfrResponse(
    Long id,
    String nom,
    LocalDateTime dateCreation
) {}
