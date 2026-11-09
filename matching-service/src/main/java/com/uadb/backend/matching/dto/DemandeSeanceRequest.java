package com.uadb.backend.matching.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import com.uadb.backend.matching.entity.ModeSeance;
import jakarta.validation.constraints.*;

public record DemandeSeanceRequest(
    @NotNull Long tuteurId,
    @NotNull Long coursId,
    @NotNull @FutureOrPresent LocalDate dateSeance,
    @NotNull LocalTime heureDebut,
    @NotNull LocalTime heureFin,
    @NotNull ModeSeance mode,
    @Size(max = 500) String lieu,
    @Size(max = 2000) String motif
) {}
