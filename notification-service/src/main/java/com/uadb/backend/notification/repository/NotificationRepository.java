package com.uadb.backend.notification.repository;

import com.uadb.backend.notification.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository
        extends JpaRepository<NotificationEntity, Long> {

    List<NotificationEntity> findByUtilisateurIdOrderByDateCreationDesc(Long utilisateurId);

    List<NotificationEntity> findByUtilisateurIdAndLuFalseOrderByDateCreationDesc(
            Long utilisateurId);

    long countByUtilisateurIdAndLuFalse(Long utilisateurId);

    void deleteByIdAndUtilisateurId(Long id, Long utilisateurId);
}
