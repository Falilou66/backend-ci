
package com.uadb.backend.identity.dto;

import java.util.Set;

public record LoginResponse(
        String accessToken,
        String tokenType,
        long expiresIn,
        Long utilisateurId,
        String nom,
        String prenom,
        String email,
        Set<String> roles
) {}
