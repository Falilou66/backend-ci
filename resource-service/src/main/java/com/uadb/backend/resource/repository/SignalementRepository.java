package com.uadb.backend.resource.repository;

import com.uadb.backend.resource.entity.SignalementEntity;
import com.uadb.backend.resource.entity.StatutSignalement;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SignalementRepository extends JpaRepository<SignalementEntity, Long> {

    List<SignalementEntity> findByStatutOrderByDateCreationDesc(
            StatutSignalement statut);

    List<SignalementEntity> findAllByOrderByDateCreationDesc();

    List<SignalementEntity> findByRessourceIdOrderByDateCreationDesc(Long ressourceId);

    boolean existsByRessourceIdAndUtilisateurIdAndStatut(
            Long ressourceId,
            Long utilisateurId,
            StatutSignalement statut);
}
