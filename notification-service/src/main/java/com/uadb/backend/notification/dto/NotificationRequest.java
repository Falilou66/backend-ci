package com.uadb.backend.notification.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record NotificationRequest(
        @NotNull @Positive Long utilisateurId,
        @NotBlank @Size(max = 50) String type,
        @NotBlank @Size(max = 150) String titre,
        @NotBlank @Size(max = 1000) String message,
        @Size(max = 500) String lien
) {}
