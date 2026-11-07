package com.uadb.backend.academic.repository;

import com.uadb.backend.academic.entity.FiliereEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FiliereRepository
        extends JpaRepository<FiliereEntity, Long> {

    Optional<FiliereEntity> findByNomIgnoreCase(String nom);

    boolean existsByNomIgnoreCase(String nom);
}
