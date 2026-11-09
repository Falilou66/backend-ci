package com.uadb.backend.identity.institution;

public record ReferenceInstitutionnelle(
        String matricule,
        String nom,
        String prenom,
        String email,
        String telephone,
        Long filiereId,
        Long niveauId,
        Long departementId,
        String type,
        boolean actif
) {}
