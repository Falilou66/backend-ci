package com.uadb.backend.academic.dto;

import java.time.LocalDateTime;

public record FiliereResponse(
    Long id,
    String nom,
    String description,
    LocalDateTime dateCreation,
    Long ufrId,
    String ufrNom
) {}
