package com.uadb.backend.matching.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import com.uadb.backend.matching.entity.SeanceEntity;
import com.uadb.backend.matching.entity.StatutSeance;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface SeanceRepository extends JpaRepository<SeanceEntity, Long> {

    List<SeanceEntity> findAllByEtudiantIdOrderByDateSeanceDescHeureDebutDesc(Long etudiantId);

    List<SeanceEntity> findAllByTuteurIdOrderByDateSeanceDescHeureDebutDesc(Long tuteurId);

    @Query("""
        select count(s) > 0 from SeanceEntity s
        where s.tuteurId = :tuteurId
          and s.dateSeance = :dateSeance
          and s.statut in :statuts
          and (:idExclu is null or s.id <> :idExclu)
          and s.heureDebut < :heureFin
          and s.heureFin > :heureDebut
        """)
    boolean existeChevauchement(
        @Param("tuteurId") Long tuteurId,
        @Param("dateSeance") LocalDate dateSeance,
        @Param("heureDebut") LocalTime heureDebut,
        @Param("heureFin") LocalTime heureFin,
        @Param("statuts") List<StatutSeance> statuts,
        @Param("idExclu") Long idExclu
    );
}
