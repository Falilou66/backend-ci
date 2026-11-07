package com.uadb.backend.identity.repository;

import com.uadb.backend.identity.entity.UtilisateurEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import com.uadb.backend.identity.enums.StatutUtilisateur;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UtilisateurRepository
        extends JpaRepository<UtilisateurEntity, Long> {

    Optional<UtilisateurEntity> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<UtilisateurEntity> findByIdAndEmail(Long id, String email);

    List<UtilisateurEntity> findAllByStatut(StatutUtilisateur statut);

    @Query("""
    SELECT u FROM UtilisateurEntity u
    WHERE LOWER(u.nom) LIKE LOWER(CONCAT('%', :recherche, '%'))
       OR LOWER(u.prenom) LIKE LOWER(CONCAT('%', :recherche, '%'))
       OR LOWER(u.email) LIKE LOWER(CONCAT('%', :recherche, '%'))
    """)
Page<UtilisateurEntity> rechercher(
        @Param("recherche") String recherche,
        Pageable pageable
);
}
