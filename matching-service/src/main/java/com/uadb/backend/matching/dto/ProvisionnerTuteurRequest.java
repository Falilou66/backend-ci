package com.uadb.backend.matching.dto;

import jakarta.validation.constraints.*;
import java.util.List;

public record ProvisionnerTuteurRequest(
    @NotNull @Positive Long utilisateurId,
    @NotBlank @Size(max = 2000) String description,
    @NotEmpty @Size(max = 20) List<@NotNull @Positive Long> coursIds
) {}
