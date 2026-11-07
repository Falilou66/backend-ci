package com.uadb.backend.academic.repository;

import com.uadb.backend.academic.entity.NiveauEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NiveauRepository extends JpaRepository<NiveauEntity, Long> {
    boolean existsByCodeIgnoreCase(String code);
}