package com.uadb.backend.matching.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import com.uadb.backend.matching.entity.*;

public record SeanceResponse(
    Long id,
    Long etudiantId,
    Long tuteurId,
    Long coursId,
    LocalDate dateSeance,
    LocalTime heureDebut,
    LocalTime heureFin,
    ModeSeance mode,
    String lieu,
    String motif,
    StatutSeance statut,
    String motifDecision,
    LocalDateTime dateCreation,
    LocalDateTime dateDecision
) {
    public static SeanceResponse depuis(SeanceEntity s) {
        return new SeanceResponse(
            s.getId(), s.getEtudiantId(), s.getTuteurId(), s.getCoursId(),
            s.getDateSeance(), s.getHeureDebut(), s.getHeureFin(),
            s.getMode(), s.getLieu(), s.getMotif(), s.getStatut(),
            s.getMotifDecision(), s.getDateCreation(), s.getDateDecision()
        );
    }
}
