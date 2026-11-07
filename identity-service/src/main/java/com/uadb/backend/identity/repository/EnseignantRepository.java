package com.uadb.backend.identity.repository;

import com.uadb.backend.identity.entity.EnseignantEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EnseignantRepository
        extends JpaRepository<EnseignantEntity, Long> {

    Optional<EnseignantEntity> findByMatriculeProfessionnel(
            String matriculeProfessionnel
    );
}
