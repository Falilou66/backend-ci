package com.uadb.backend.identity.repository;

import com.uadb.backend.identity.entity.EtudiantEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EtudiantRepository extends JpaRepository<EtudiantEntity, Long> {

    Optional<EtudiantEntity> findByCodePermanent(String codePermanent);

    boolean existsByCodePermanent(String codePermanent);
}