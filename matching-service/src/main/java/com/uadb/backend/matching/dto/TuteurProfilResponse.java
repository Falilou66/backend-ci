package com.uadb.backend.matching.dto;

import java.time.LocalDateTime;
import java.util.List;
import com.uadb.backend.matching.entity.TuteurEntity;
import com.uadb.backend.matching.entity.StatutProfilTuteur;

public record TuteurProfilResponse(
    Long id,
    Long utilisateurId,
    String description,
    List<Long> coursIds,
    StatutProfilTuteur statut,
    LocalDateTime dateCreation
) {
    public static TuteurProfilResponse depuis(TuteurEntity tuteur) {
        return new TuteurProfilResponse(
            tuteur.getId(),
            tuteur.getUtilisateurId(),
            tuteur.getDescription(),
            tuteur.getCoursIds(),
            tuteur.getStatut(),
            tuteur.getDateCreation()
        );
    }
}
