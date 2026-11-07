package com.uadb.backend.identity.dto;

public record AuthentificationResponse(

        String accessToken,
        String tokenType,
        long expiresIn,
        UtilisateurResponse utilisateur

) {}
