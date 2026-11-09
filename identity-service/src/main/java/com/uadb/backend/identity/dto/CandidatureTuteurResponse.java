package com.uadb.backend.identity.dto;

import java.time.LocalDateTime;
import java.util.List;

public record CandidatureTuteurResponse(
    Long id,
    Long utilisateurId,
    String nom,
    String prenom,
    String email,
    String description,
    String motivation,
    List<Long> coursIds,
    String statut,
    String motifRefus,
    LocalDateTime dateCreation,
    LocalDateTime dateDecision
) {}
