package com.uadb.backend.academic.repository;

import com.uadb.backend.academic.entity.UfrEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UfrRepository extends JpaRepository<UfrEntity, Long> {
    boolean existsByNomIgnoreCase(String nom);
    Optional<UfrEntity> findByNomIgnoreCase(String nom);
}
