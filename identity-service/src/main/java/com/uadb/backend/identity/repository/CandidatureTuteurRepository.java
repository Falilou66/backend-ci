package com.uadb.backend.identity.repository;

import com.uadb.backend.identity.entity.CandidatureTuteurEntity;
import com.uadb.backend.identity.enums.StatutCandidatureTuteur;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Collection;
import java.util.List;

public interface CandidatureTuteurRepository
        extends JpaRepository<CandidatureTuteurEntity, Long> {

    boolean existsByUtilisateurIdAndStatutIn(
        Long utilisateurId,
        Collection<StatutCandidatureTuteur> statuts
    );

    List<CandidatureTuteurEntity> findAllByStatutOrderByDateCreationAsc(
        StatutCandidatureTuteur statut
    );

    List<CandidatureTuteurEntity> findAllByUtilisateurIdOrderByDateCreationDesc(
        Long utilisateurId
    );
}
