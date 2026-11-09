package com.uadb.backend.identity.dto;

import jakarta.validation.constraints.*;
import java.util.List;

public record CandidatureTuteurRequest(
    @NotBlank @Size(max = 2000) String description,
    @NotBlank @Size(max = 2000) String motivation,
    @NotEmpty @Size(max = 20) List<@NotNull @Positive Long> coursIds
) {}
