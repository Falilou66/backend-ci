package com.uadb.backend.matching.dto;

import java.time.DayOfWeek;
import java.time.LocalTime;

import jakarta.validation.constraints.NotNull;

public record DisponibiliteRequest(
    @NotNull DayOfWeek jour,
    @NotNull LocalTime heureDebut,
    @NotNull LocalTime heureFin,
    boolean disponible
) {}
