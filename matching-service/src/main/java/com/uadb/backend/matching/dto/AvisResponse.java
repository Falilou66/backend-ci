package com.uadb.backend.matching.dto;

import java.time.LocalDateTime;
import com.uadb.backend.matching.entity.AvisEntity;

public record AvisResponse(
    Long id,
    Long seanceId,
    Long tuteurId,
    Long auteurId,
    Integer note,
    String commentaire,
    LocalDateTime dateCreation
) {
    public static AvisResponse depuis(AvisEntity avis) {
        return new AvisResponse(
            avis.getId(),
            avis.getSeanceId(),
            avis.getTuteurId(),
            avis.getAuteurId(),
            avis.getNote(),
            avis.getCommentaire(),
            avis.getDateCreation()
        );
    }
}
