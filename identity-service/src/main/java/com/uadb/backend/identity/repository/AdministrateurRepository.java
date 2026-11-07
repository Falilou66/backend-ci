package com.uadb.backend.identity.repository;

import com.uadb.backend.identity.entity.AdministrateurEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdministrateurRepository
        extends JpaRepository<AdministrateurEntity, Long> {
}
