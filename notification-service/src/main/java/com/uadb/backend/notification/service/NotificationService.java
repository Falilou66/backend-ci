package com.uadb.backend.notification.service;

import com.uadb.backend.notification.dto.NotificationRequest;
import com.uadb.backend.notification.entity.NotificationEntity;
import com.uadb.backend.notification.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository repository;

    public NotificationService(NotificationRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public NotificationEntity creer(NotificationRequest request) {
        NotificationEntity notification = new NotificationEntity();
        notification.setUtilisateurId(request.utilisateurId());
        notification.setType(request.type());
        notification.setTitre(request.titre());
        notification.setMessage(request.message());
        notification.setLien(request.lien());
        return repository.save(notification);
    }

    @Transactional(readOnly = true)
    public List<NotificationEntity> lister(Long utilisateurId) {
        return repository.findByUtilisateurIdOrderByDateCreationDesc(utilisateurId);
    }

    @Transactional(readOnly = true)
    public List<NotificationEntity> listerNonLues(Long utilisateurId) {
        return repository.findByUtilisateurIdAndLuFalseOrderByDateCreationDesc(
                utilisateurId);
    }

    @Transactional(readOnly = true)
    public long compterNonLues(Long utilisateurId) {
        return repository.countByUtilisateurIdAndLuFalse(utilisateurId);
    }

    @Transactional
    public NotificationEntity marquerCommeLue(Long id, Long utilisateurId) {
        NotificationEntity notification = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Notification introuvable"));

        if (!notification.getUtilisateurId().equals(utilisateurId)) {
            throw new IllegalArgumentException(
                    "Cette notification n'appartient pas à cet utilisateur");
        }

        notification.setLu(true);
        return repository.save(notification);
    }

    @Transactional
    public void marquerToutesCommeLues(Long utilisateurId) {
        List<NotificationEntity> notifications =
                repository.findByUtilisateurIdAndLuFalseOrderByDateCreationDesc(
                        utilisateurId);

        notifications.forEach(notification -> notification.setLu(true));
        repository.saveAll(notifications);
    }

    @Transactional
    public void supprimer(Long id, Long utilisateurId) {
        NotificationEntity notification = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Notification introuvable"));

        if (!notification.getUtilisateurId().equals(utilisateurId)) {
            throw new IllegalArgumentException(
                    "Cette notification n'appartient pas à cet utilisateur");
        }

        repository.delete(notification);
    }
}
