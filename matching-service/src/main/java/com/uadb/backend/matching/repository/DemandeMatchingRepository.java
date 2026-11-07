package com.uadb.backend.matching.repository;

import com.uadb.backend.matching.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DemandeMatchingRepository
        extends JpaRepository<DemandeMatchingEntity, Long> {

    List<DemandeMatchingEntity> findByEtudiantIdOrderByDateCreationDesc(Long etudiantId);

    List<DemandeMatchingEntity> findByTuteurIdAndStatutOrderByDateCreationDesc(
            Long tuteurId, StatutDemande statut);
}
