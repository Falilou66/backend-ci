package com.uadb.backend.resource.service;

import com.uadb.backend.resource.dto.SignalementRequest;
import com.uadb.backend.resource.dto.SignalementResponse;
import com.uadb.backend.resource.entity.RessourceEntity;
import com.uadb.backend.resource.entity.SignalementEntity;
import com.uadb.backend.resource.entity.StatutSignalement;
import com.uadb.backend.resource.repository.RessourceRepository;
import com.uadb.backend.resource.repository.SignalementRepository;
import com.uadb.backend.resource.client.NotificationClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SignalementService {

    private final SignalementRepository repository;
    private final RessourceRepository ressourceRepository;
    private final NotificationClient notificationClient;

    @Value("${NOTIFICATION_ADMIN_IDS:}")
    private String notificationAdminIds;

    public SignalementService(
            SignalementRepository repository,
            RessourceRepository ressourceRepository,
            NotificationClient notificationClient) {
        this.repository = repository;
        this.ressourceRepository = ressourceRepository;
        this.notificationClient = notificationClient;
    }

    @Transactional
    public SignalementResponse creer(Long ressourceId, SignalementRequest request) {
        RessourceEntity ressource = ressourceRepository.findById(ressourceId)
                .orElseThrow(() -> new IllegalArgumentException("Ressource introuvable"));

        boolean dejaSignale = repository.existsByRessourceIdAndUtilisateurIdAndStatut(
                ressourceId,
                request.utilisateurId(),
                StatutSignalement.EN_ATTENTE
        );

        if (dejaSignale) {
            throw new IllegalArgumentException(
                    "Vous avez déjà un signalement en attente pour cette ressource");
        }

        SignalementEntity signalement = new SignalementEntity();
        signalement.setRessource(ressource);
        signalement.setUtilisateurId(request.utilisateurId());
        signalement.setMotif(request.motif());
        signalement.setDescription(request.description());
        signalement.setStatut(StatutSignalement.EN_ATTENTE);

        SignalementEntity sauvegarde = repository.save(signalement);
        SignalementResponse response = convertir(sauvegarde);

        notificationClient.notifier(
                sauvegarde.getUtilisateurId(),
                "SIGNALEMENT_RECU",
                "Signalement enregistré",
                "Votre signalement concernant la ressource « "
                        + ressource.getTitre() + " » a été enregistré.",
                "/ressources/" + ressourceId
        );

        if (notificationAdminIds != null && !notificationAdminIds.isBlank()) {
            for (String valeur : notificationAdminIds.split(",")) {
                try {
                    Long adminId = Long.parseLong(valeur.trim());

                    if (!adminId.equals(sauvegarde.getUtilisateurId())) {
                        notificationClient.notifier(
                                adminId,
                                "NOUVEAU_SIGNALEMENT",
                                "Nouveau signalement à traiter",
                                "La ressource « " + ressource.getTitre()
                                        + " » a reçu un signalement.",
                                "/signalements/" + sauvegarde.getId()
                        );
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Identifiant administrateur invalide : " + valeur);
                }
            }
        }

        return response;
    }

    @Transactional(readOnly = true)
    public List<SignalementResponse> lister(StatutSignalement statut) {
        List<SignalementEntity> signalements = statut == null
                ? repository.findAllByOrderByDateCreationDesc()
                : repository.findByStatutOrderByDateCreationDesc(statut);

        return signalements.stream().map(this::convertir).toList();
    }

    @Transactional(readOnly = true)
    public List<SignalementResponse> parRessource(Long ressourceId) {
        return repository.findByRessourceIdOrderByDateCreationDesc(ressourceId)
                .stream().map(this::convertir).toList();
    }

    @Transactional
    public SignalementResponse traiter(Long id, StatutSignalement statut) {
        if (statut == null || statut == StatutSignalement.EN_ATTENTE) {
            throw new IllegalArgumentException("Statut de traitement invalide");
        }

        SignalementEntity signalement = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Signalement introuvable"));

        signalement.setStatut(statut);
        signalement.setDateTraitement(LocalDateTime.now());

        return convertir(repository.save(signalement));
    }

    private SignalementResponse convertir(SignalementEntity s) {
        return new SignalementResponse(
                s.getId(),
                s.getRessource().getId(),
                s.getRessource().getTitre(),
                s.getUtilisateurId(),
                s.getMotif(),
                s.getDescription(),
                s.getStatut(),
                s.getDateCreation(),
                s.getDateTraitement()
        );
    }
}
