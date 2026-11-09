package com.uadb.backend.identity.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record InscriptionInstitutionnelleRequest(
        @NotBlank String matricule,
        @NotBlank @Email String email
) {}
