package com.uadb.backend.identity.dto;

import com.uadb.backend.identity.enums.Role;
import com.uadb.backend.identity.enums.StatutUtilisateur;

import java.util.Set;

public record UtilisateurResponse(

        Long id,
        String nom,
        String prenom,
        String email,
        String telephone,
        Set<Role> roles,
        StatutUtilisateur statut

) {}
