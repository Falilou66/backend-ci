package com.uadb.backend.resource.repository;

import com.uadb.backend.resource.entity.RessourceEntity;
import com.uadb.backend.resource.enums.StatutPublication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RessourceRepository extends JpaRepository<RessourceEntity, Long> {

    List<RessourceEntity> findByCoursIdAndStatutPublicationOrderByDateCreationDesc(
            Long coursId, StatutPublication statut);

    List<RessourceEntity> findByStatutPublicationOrderByDateCreationDesc(
            StatutPublication statut);

    @Query("""
        SELECT r FROM RessourceEntity r
        WHERE r.statutPublication = :statut
        AND (:coursId IS NULL OR r.coursId = :coursId)
        AND (:auteurId IS NULL OR r.auteurId = :auteurId)
        AND (:type IS NULL OR LOWER(r.type) = LOWER(:type))
        AND (:titre IS NULL OR LOWER(r.titre) LIKE LOWER(CONCAT('%', :titre, '%')))
        ORDER BY r.dateCreation DESC
    """)
    Page<RessourceEntity> rechercher(
            @Param("coursId") Long coursId,
            @Param("auteurId") Long auteurId,
            @Param("type") String type,
            @Param("titre") String titre,
            @Param("statut") StatutPublication statut,
            Pageable pageable
    );
}
