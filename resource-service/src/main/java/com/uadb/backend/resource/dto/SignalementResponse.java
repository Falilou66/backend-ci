package com.uadb.backend.resource.dto;

import com.uadb.backend.resource.entity.StatutSignalement;
import java.time.LocalDateTime;

public record SignalementResponse(
        Long id,
        Long ressourceId,
        String titreRessource,
        Long utilisateurId,
        String motif,
        String description,
        StatutSignalement statut,
        LocalDateTime dateCreation,
        LocalDateTime dateTraitement
) {}
