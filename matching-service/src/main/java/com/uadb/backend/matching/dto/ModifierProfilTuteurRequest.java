package com.uadb.backend.matching.dto;

import java.util.List;
import jakarta.validation.constraints.*;

public record ModifierProfilTuteurRequest(
    @NotBlank @Size(max = 2000) String description,
    @NotEmpty @Size(max = 20) List<@NotNull @Positive Long> coursIds
) {}
