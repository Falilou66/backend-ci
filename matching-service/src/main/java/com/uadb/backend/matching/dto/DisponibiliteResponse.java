package com.uadb.backend.matching.dto;

import java.time.DayOfWeek;
import java.time.LocalTime;

import com.uadb.backend.matching.entity.DisponibiliteEntity;

public record DisponibiliteResponse(
    Long id,
    DayOfWeek jour,
    LocalTime heureDebut,
    LocalTime heureFin,
    boolean disponible
) {
    public static DisponibiliteResponse depuis(DisponibiliteEntity d) {
        return new DisponibiliteResponse(
            d.getId(), d.getJour(), d.getHeureDebut(), d.getHeureFin(), d.isDisponible()
        );
    }
}
