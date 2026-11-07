package com.uadb.backend.academic.dto;

public record CoursResponse(
    Long id,
    String nom,
    String description,
    Integer volumeHoraire,
    Long filiereId,
    String filiereNom,
    Long niveauId,
    String niveauLibelle
) {}
