package com.uadb.backend.identity.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import com.uadb.backend.identity.enums.Role;

public record InscriptionRequest(
    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 100)
    String nom,

    @NotBlank(message = "Le prénom est obligatoire")
    @Size(max = 100)
    String prenom,

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Adresse email invalide")
    @Size(max = 150)
    String email,

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères")
    String motDePasse,

    @Size(max = 20)
    String telephone,

    @Size(max = 50)
    String codePermanent,

    Long filiereId,
    Long niveauId,

    @NotNull(message = "Le type de compte est obligatoire")
    Role role,

    @Size(max = 50)
    String matriculeProfessionnel
) {}