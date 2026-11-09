package com.uadb.backend.academic.repository;

import com.uadb.backend.academic.entity.CoursEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CoursRepository extends JpaRepository<CoursEntity, Long> {

    @Query("""
        SELECT c FROM CoursEntity c
        WHERE (:filiereId IS NULL OR c.filiere.id = :filiereId)
        AND (:niveauId IS NULL OR c.niveau.id = :niveauId)
        AND (:nom IS NULL OR LOWER(c.nom) LIKE LOWER(CONCAT('%', :nom, '%')))
    """)
    Page<CoursEntity> rechercher(
        @Param("filiereId") Long filiereId,
        @Param("niveauId") Long niveauId,
        @Param("nom") String nom,
        Pageable pageable
    );
}
