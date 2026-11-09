package com.uadb.backend.identity.dto;

public record InscriptionInstitutionnelleResponse(
        Long utilisateurId,
        String nom,
        String prenom,
        String email,
        String role,
        String motDePasseTemporaire,
        String message
) {}
