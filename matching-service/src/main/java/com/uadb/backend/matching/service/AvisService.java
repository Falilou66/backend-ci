package com.uadb.backend.matching.service;

import java.util.List;

import com.uadb.backend.matching.client.NotificationClient;
import com.uadb.backend.matching.dto.*;
import com.uadb.backend.matching.entity.*;
import com.uadb.backend.matching.repository.*;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AvisService {

    private final AvisRepository avisRepository;
    private final SeanceRepository seanceRepository;
    private final TuteurRepository tuteurRepository;
    private final NotificationClient notificationClient;

    public AvisService(
        AvisRepository avisRepository,
        SeanceRepository seanceRepository,
        TuteurRepository tuteurRepository,
        NotificationClient notificationClient
    ) {
        this.avisRepository = avisRepository;
        this.seanceRepository = seanceRepository;
        this.tuteurRepository = tuteurRepository;
        this.notificationClient = notificationClient;
    }

    @Transactional
    public AvisResponse deposer(Long utilisateurId, Long seanceId, AvisRequest request) {
        SeanceEntity seance = seanceRepository.findById(seanceId)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Séance introuvable"));

        if (!seance.getEtudiantId().equals(utilisateurId)) {
            throw new ResponseStatusException(
                HttpStatus.FORBIDDEN, "Seul l'étudiant ayant demandé la séance peut donner un avis");
        }

        if (seance.getStatut() != StatutSeance.TERMINEE) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT, "Un avis ne peut être publié qu'après une séance terminée");
        }

        if (avisRepository.existsBySeanceId(seanceId)) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT, "Un avis existe déjà pour cette séance");
        }

        TuteurEntity tuteur = tuteurRepository.findById(seance.getTuteurId())
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Profil tuteur introuvable"));

        AvisEntity avis = new AvisEntity();
        avis.setSeanceId(seanceId);
        avis.setTuteurId(tuteur.getId());
        avis.setAuteurId(utilisateurId);
        avis.setNote(request.note());
        avis.setCommentaire(request.commentaire());

        try {
            AvisEntity saved = avisRepository.saveAndFlush(avis);
            notificationClient.notifier(
                tuteur.getUtilisateurId(),
                "AVIS",
                "Nouvel avis reçu",
                "Un étudiant vient de laisser un avis sur votre profil.",
                "/matching/tuteurs/" + tuteur.getId()
            );
            return AvisResponse.depuis(saved);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT, "Un avis existe déjà pour cette séance");
        }
    }

    @Transactional(readOnly = true)
    public List<AvisResponse> listerParTuteur(Long tuteurId) {
        if (!tuteurRepository.existsById(tuteurId)) {
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Profil tuteur introuvable");
        }

        return avisRepository.findAllByTuteurIdOrderByDateCreationDesc(tuteurId)
            .stream()
            .map(AvisResponse::depuis)
            .toList();
    }

    @Transactional(readOnly = true)
    public AvisResumeResponse resume(Long tuteurId) {
        if (!tuteurRepository.existsById(tuteurId)) {
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Profil tuteur introuvable");
        }

        Double moyenne = avisRepository.calculerNoteMoyenne(tuteurId);
        Long nombre = avisRepository.countByTuteurId(tuteurId);

        return new AvisResumeResponse(
            tuteurId,
            moyenne == null ? 0.0 : Math.round(moyenne * 100.0) / 100.0,
            nombre
        );
    }
}
