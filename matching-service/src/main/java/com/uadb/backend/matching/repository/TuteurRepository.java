package com.uadb.backend.matching.repository;

import java.util.Optional;
import com.uadb.backend.matching.entity.StatutProfilTuteur;
import com.uadb.backend.matching.entity.TuteurEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TuteurRepository extends JpaRepository<TuteurEntity, Long> {

    Optional<TuteurEntity> findByUtilisateurId(Long utilisateurId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select t from TuteurEntity t where t.id = :id")
    Optional<TuteurEntity> verrouillerParId(@Param("id") Long id);

    Optional<TuteurEntity> findByIdAndStatut(Long id, StatutProfilTuteur statut);

    boolean existsByUtilisateurId(Long utilisateurId);

    @Query(
        value = "select distinct t from TuteurEntity t left join t.coursIds c " +
                "where t.statut = :statut and " +
                "(:coursId is null or c = :coursId)",
        countQuery = "select count(distinct t) from TuteurEntity t left join t.coursIds c " +
                "where t.statut = :statut and " +
                "(:coursId is null or c = :coursId)"
    )
    Page<TuteurEntity> rechercher(
        @Param("statut") StatutProfilTuteur statut,
        @Param("coursId") Long coursId,
        Pageable pageable
    );
}
