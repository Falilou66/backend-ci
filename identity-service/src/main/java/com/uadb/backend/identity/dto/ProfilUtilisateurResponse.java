package com.uadb.backend.identity.dto;

import java.util.Set;

public record ProfilUtilisateurResponse(
        Long id,
        String nom,
        String prenom,
        String email,
        String telephone,
        String statut,
        Set<String> roles
) {}
