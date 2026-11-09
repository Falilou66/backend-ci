package com.uadb.backend.identity.repository;

import com.uadb.backend.identity.entity.EtudiantEntity;
import com.uadb.backend.identity.enums.StatutUtilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EtudiantRepository extends JpaRepository<EtudiantEntity, Long> {

    Optional<EtudiantEntity> findByCodePermanent(String codePermanent);

    boolean existsByCodePermanent(String codePermanent);

    @Query("""
        SELECT e.id
        FROM EtudiantEntity e
        WHERE e.filiereId = :filiereId
        AND e.niveauId = :niveauId
        AND e.utilisateur.statut = :statut
    """)
    List<Long> trouverIdsActifsParFiliereEtNiveau(
            @Param("filiereId") Long filiereId,
            @Param("niveauId") Long niveauId,
            @Param("statut") StatutUtilisateur statut
    );
}
