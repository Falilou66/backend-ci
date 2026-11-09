package com.uadb.backend.matching.service;

import java.time.*;
import java.util.List;

import com.uadb.backend.matching.client.NotificationClient;
import com.uadb.backend.matching.client.AcademicCoursClient;
import com.uadb.backend.matching.dto.*;
import com.uadb.backend.matching.entity.*;
import com.uadb.backend.matching.repository.*;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class SeanceService {

    private static final List<StatutSeance> STATUTS_OCCUPANTS =
        List.of(StatutSeance.EN_ATTENTE, StatutSeance.CONFIRMEE);

    private final SeanceRepository seanceRepository;
    private final TuteurRepository tuteurRepository;
    private final DisponibiliteRepository disponibiliteRepository;
    private final NotificationClient notificationClient;
    private final AcademicCoursClient academicCoursClient;

    public SeanceService(
        SeanceRepository seanceRepository,
        TuteurRepository tuteurRepository,
        DisponibiliteRepository disponibiliteRepository,
        NotificationClient notificationClient,
        AcademicCoursClient academicCoursClient
    ) {
        this.seanceRepository = seanceRepository;
        this.tuteurRepository = tuteurRepository;
        this.disponibiliteRepository = disponibiliteRepository;
        this.notificationClient = notificationClient;
        this.academicCoursClient = academicCoursClient;
    }

    @Transactional
    public SeanceResponse demander(Long etudiantId, DemandeSeanceRequest request) {
        TuteurEntity tuteur = tuteurRepository.verrouillerParId(request.tuteurId())
            .filter(t -> t.getStatut() == StatutProfilTuteur.ACTIF)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Profil tuteur introuvable"));

        if (tuteur.getUtilisateurId().equals(etudiantId)) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Vous ne pouvez pas réserver une séance avec vous-même");
        }

        academicCoursClient.verifierCours(request.coursId());

        if (!tuteur.getCoursIds().contains(request.coursId())) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Ce tuteur ne propose pas d'accompagnement pour ce cours."
            );
        }

        verifierCreneau(request.dateSeance(), request.heureDebut(), request.heureFin());
        verifierModeEtLieu(request.mode(), request.lieu());
        verifierDisponibilite(tuteur.getId(), request.dateSeance(),
            request.heureDebut(), request.heureFin());

        if (seanceRepository.existeChevauchement(
            tuteur.getId(), request.dateSeance(), request.heureDebut(),
            request.heureFin(), STATUTS_OCCUPANTS, null)) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT, "Le tuteur a déjà une séance sur ce créneau");
        }

        SeanceEntity s = new SeanceEntity();
        s.setEtudiantId(etudiantId);
        s.setTuteurId(tuteur.getId());
        s.setCoursId(request.coursId());
        s.setDateSeance(request.dateSeance());
        s.setHeureDebut(request.heureDebut());
        s.setHeureFin(request.heureFin());
        s.setMode(request.mode());
        s.setLieu(request.lieu());
        s.setMotif(request.motif());

        SeanceEntity saved = seanceRepository.save(s);
        notificationClient.notifier(tuteur.getUtilisateurId(), "SEANCE",
            "Nouvelle demande de séance",
            "Un étudiant vous a envoyé une demande de séance.",
            "/matching/seances/" + saved.getId());
        return SeanceResponse.depuis(saved);
    }

    @Transactional(readOnly = true)
    public List<SeanceResponse> mesSeancesEtudiant(Long utilisateurId) {
        return seanceRepository.findAllByEtudiantIdOrderByDateSeanceDescHeureDebutDesc(
            utilisateurId).stream().map(SeanceResponse::depuis).toList();
    }

    @Transactional(readOnly = true)
    public List<SeanceResponse> mesSeancesTuteur(Long utilisateurId) {
        TuteurEntity tuteur = trouverTuteurParUtilisateur(utilisateurId);
        return seanceRepository.findAllByTuteurIdOrderByDateSeanceDescHeureDebutDesc(
            tuteur.getId()).stream().map(SeanceResponse::depuis).toList();
    }

    @Transactional
    public SeanceResponse accepter(Long utilisateurId, Long seanceId) {
        SeanceEntity s = trouverSeance(seanceId);
        TuteurEntity tuteur = tuteurRepository.verrouillerParId(s.getTuteurId())
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Profil tuteur introuvable"));

        verifierProprietaireTuteur(tuteur, utilisateurId);
        verifierStatut(s, StatutSeance.EN_ATTENTE);
        verifierCreneau(s.getDateSeance(), s.getHeureDebut(), s.getHeureFin());

        if (seanceRepository.existeChevauchement(
            s.getTuteurId(), s.getDateSeance(), s.getHeureDebut(),
            s.getHeureFin(), STATUTS_OCCUPANTS, s.getId())) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT, "Ce créneau est déjà pris par une autre séance");
        }

        s.setStatut(StatutSeance.CONFIRMEE);
        s.setDateDecision(LocalDateTime.now());
        SeanceEntity saved = seanceRepository.save(s);
        notificationClient.notifier(s.getEtudiantId(), "SEANCE",
            "Séance confirmée", "Votre demande de séance a été acceptée.",
            "/matching/seances/" + s.getId());
        return SeanceResponse.depuis(saved);
    }

    @Transactional
    public SeanceResponse refuser(Long utilisateurId, Long seanceId, DecisionSeanceRequest request) {
        SeanceEntity s = trouverSeance(seanceId);
        TuteurEntity tuteur = trouverTuteur(s.getTuteurId());
        verifierProprietaireTuteur(tuteur, utilisateurId);
        verifierStatut(s, StatutSeance.EN_ATTENTE);

        s.setStatut(StatutSeance.REFUSEE);
        s.setMotifDecision(request == null ? null : request.motif());
        s.setDateDecision(LocalDateTime.now());
        SeanceEntity saved = seanceRepository.save(s);
        notificationClient.notifier(s.getEtudiantId(), "SEANCE",
            "Séance refusée", "Votre demande de séance a été refusée.",
            "/matching/seances/" + s.getId());
        return SeanceResponse.depuis(saved);
    }

    @Transactional
    public SeanceResponse annuler(Long utilisateurId, Long seanceId, DecisionSeanceRequest request) {
        SeanceEntity s = trouverSeance(seanceId);
        TuteurEntity tuteur = trouverTuteur(s.getTuteurId());

        if (!s.getEtudiantId().equals(utilisateurId)
            && !tuteur.getUtilisateurId().equals(utilisateurId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès refusé");
        }

        if (s.getStatut() != StatutSeance.EN_ATTENTE
            && s.getStatut() != StatutSeance.CONFIRMEE) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT, "Cette séance ne peut plus être annulée");
        }

        s.setStatut(StatutSeance.ANNULEE);
        s.setMotifDecision(request == null ? null : request.motif());
        s.setDateDecision(LocalDateTime.now());
        SeanceEntity saved = seanceRepository.save(s);

        Long destinataire = s.getEtudiantId().equals(utilisateurId)
            ? tuteur.getUtilisateurId() : s.getEtudiantId();
        notificationClient.notifier(destinataire, "SEANCE",
            "Séance annulée", "Une séance a été annulée.",
            "/matching/seances/" + s.getId());
        return SeanceResponse.depuis(saved);
    }

    @Transactional
    public SeanceResponse terminer(Long utilisateurId, Long seanceId) {
        SeanceEntity s = trouverSeance(seanceId);
        TuteurEntity tuteur = trouverTuteur(s.getTuteurId());
        verifierProprietaireTuteur(tuteur, utilisateurId);
        verifierStatut(s, StatutSeance.CONFIRMEE);

        LocalDateTime fin = LocalDateTime.of(s.getDateSeance(), s.getHeureFin());
        if (LocalDateTime.now().isBefore(fin)) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "La séance ne peut être terminée avant son heure de fin");
        }

        s.setStatut(StatutSeance.TERMINEE);
        s.setDateDecision(LocalDateTime.now());
        SeanceEntity saved = seanceRepository.save(s);
        notificationClient.notifier(s.getEtudiantId(), "SEANCE",
            "Séance terminée", "Votre séance est marquée comme terminée.",
            "/matching/seances/" + s.getId());
        return SeanceResponse.depuis(saved);
    }

    private void verifierDisponibilite(
        Long tuteurId, LocalDate date, LocalTime debut, LocalTime fin) {
        boolean disponible = disponibiliteRepository
            .findAllByTuteurIdOrderByJourAscHeureDebutAsc(tuteurId)
            .stream()
            .anyMatch(d -> d.isDisponible()
                && d.getJour() == date.getDayOfWeek()
                && !debut.isBefore(d.getHeureDebut())
                && !fin.isAfter(d.getHeureFin()));

        if (!disponible) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT, "Le créneau ne correspond pas aux disponibilités du tuteur");
        }
    }

    private void verifierCreneau(LocalDate date, LocalTime debut, LocalTime fin) {
        if (date == null || debut == null || fin == null || !debut.isBefore(fin)) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Date ou horaires de séance invalides");
        }
        if (LocalDateTime.of(date, debut).isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "La séance doit être programmée dans le futur");
        }
    }

    private void verifierModeEtLieu(ModeSeance mode, String lieu) {
        if (mode == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mode de séance obligatoire");
        }
        if (mode == ModeSeance.PRESENTIEL && (lieu == null || lieu.isBlank())) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Le lieu est obligatoire pour une séance en présentiel");
        }
    }

    private void verifierStatut(SeanceEntity s, StatutSeance attendu) {
        if (s.getStatut() != attendu) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT, "Le statut actuel de la séance ne permet pas cette action");
        }
    }

    private void verifierProprietaireTuteur(TuteurEntity tuteur, Long utilisateurId) {
        if (!tuteur.getUtilisateurId().equals(utilisateurId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès refusé");
        }
    }

    private SeanceEntity trouverSeance(Long id) {
        return seanceRepository.findById(id).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Séance introuvable"));
    }

    private TuteurEntity trouverTuteur(Long id) {
        return tuteurRepository.findById(id).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profil tuteur introuvable"));
    }

    private TuteurEntity trouverTuteurParUtilisateur(Long utilisateurId) {
        return tuteurRepository.findByUtilisateurId(utilisateurId).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profil tuteur introuvable"));
    }
}
