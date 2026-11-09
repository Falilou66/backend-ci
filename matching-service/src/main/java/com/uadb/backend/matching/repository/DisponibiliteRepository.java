package com.uadb.backend.matching.repository;

import java.util.List;
import java.util.Optional;

import com.uadb.backend.matching.entity.DisponibiliteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DisponibiliteRepository extends JpaRepository<DisponibiliteEntity, Long> {

    List<DisponibiliteEntity> findAllByTuteurIdOrderByJourAscHeureDebutAsc(Long tuteurId);

    Optional<DisponibiliteEntity> findByIdAndTuteurId(Long id, Long tuteurId);

    boolean existsByTuteurIdAndJourAndDisponibleTrueAndHeureDebutLessThanAndHeureFinGreaterThan(
        Long tuteurId,
        java.time.DayOfWeek jour,
        java.time.LocalTime heureFin,
        java.time.LocalTime heureDebut
    );
}
