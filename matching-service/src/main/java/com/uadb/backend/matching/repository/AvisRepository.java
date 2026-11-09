package com.uadb.backend.matching.repository;

import java.util.List;
import java.util.Optional;
import com.uadb.backend.matching.entity.AvisEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AvisRepository extends JpaRepository<AvisEntity, Long> {

    boolean existsBySeanceId(Long seanceId);

    List<AvisEntity> findAllByTuteurIdOrderByDateCreationDesc(Long tuteurId);

    Optional<AvisEntity> findBySeanceId(Long seanceId);

    @Query("""
        select avg(a.note)
        from AvisEntity a
        where a.tuteurId = :tuteurId
        """)
    Double calculerNoteMoyenne(@Param("tuteurId") Long tuteurId);

    long countByTuteurId(Long tuteurId);
}
