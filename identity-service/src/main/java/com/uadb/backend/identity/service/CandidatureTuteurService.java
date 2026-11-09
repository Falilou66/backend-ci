package com.uadb.backend.identity.service;

import com.uadb.backend.identity.dto.*;
import com.uadb.backend.identity.client.MatchingTuteurClient;
import com.uadb.backend.identity.client.AcademicCoursClient;
import com.uadb.backend.identity.entity.*;
import com.uadb.backend.identity.enums.*;
import com.uadb.backend.identity.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
public class CandidatureTuteurService {

    private final CandidatureTuteurRepository candidatureRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final MatchingTuteurClient matchingTuteurClient;
    private final AcademicCoursClient academicCoursClient;

    public CandidatureTuteurService(
            CandidatureTuteurRepository candidatureRepository,
            UtilisateurRepository utilisateurRepository,
            MatchingTuteurClient matchingTuteurClient,
            AcademicCoursClient academicCoursClient) {
        this.candidatureRepository = candidatureRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.matchingTuteurClient = matchingTuteurClient;
        this.academicCoursClient = academicCoursClient;
    }

    @Transactional
    public CandidatureTuteurResponse deposer(
            Long utilisateurId, CandidatureTuteurRequest request) {

        UtilisateurEntity utilisateur = utilisateurRepository.findById(utilisateurId)
            .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable."));

        if (utilisateur.getStatut() != StatutUtilisateur.ACTIF) {
            throw new IllegalArgumentException("Le compte doit être actif.");
        }

        if (!utilisateur.getRoles().contains(Role.ETUDIANT)
                && !utilisateur.getRoles().contains(Role.ENSEIGNANT)) {
            throw new IllegalArgumentException(
                "Seuls les étudiants et enseignants peuvent candidater."
            );
        }

        if (utilisateur.getRoles().contains(Role.TUTEUR)
                || candidatureRepository.existsByUtilisateurIdAndStatutIn(
                    utilisateurId,
                    List.of(StatutCandidatureTuteur.EN_ATTENTE,
                            StatutCandidatureTuteur.APPROUVEE))) {
            throw new IllegalArgumentException(
                "Une candidature est déjà en cours ou a déjà été approuvée."
            );
        }

        CandidatureTuteurEntity candidature = new CandidatureTuteurEntity();
        candidature.setUtilisateurId(utilisateurId);
        candidature.setDescription(request.description().trim());
        candidature.setMotivation(request.motivation().trim());
        List<Long> coursIds = request.coursIds().stream().distinct().toList();
        coursIds.forEach(academicCoursClient::verifierCours);
        candidature.setCoursIds(coursIds);

        return convertir(candidatureRepository.save(candidature), utilisateur);
    }

    @Transactional(readOnly = true)
    public List<CandidatureTuteurResponse> candidaturesEnAttente() {
        return candidatureRepository
            .findAllByStatutOrderByDateCreationAsc(StatutCandidatureTuteur.EN_ATTENTE)
            .stream()
            .map(c -> convertirAvecUtilisateur(c))
            .toList();
    }

    @Transactional(readOnly = true)
    public List<CandidatureTuteurResponse> mesCandidatures(Long utilisateurId) {
        return candidatureRepository
            .findAllByUtilisateurIdOrderByDateCreationDesc(utilisateurId)
            .stream()
            .map(c -> convertirAvecUtilisateur(c))
            .toList();
    }

    @Transactional
    public CandidatureTuteurResponse approuver(Long candidatureId) {
        CandidatureTuteurEntity candidature = chargerEnAttente(candidatureId);

        UtilisateurEntity utilisateur = utilisateurRepository.findById(
            candidature.getUtilisateurId()
        ).orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable."));

        if (utilisateur.getStatut() != StatutUtilisateur.ACTIF) {
            throw new IllegalArgumentException("Le compte utilisateur n'est pas actif.");
        }

        matchingTuteurClient.provisionner(
            utilisateur.getId(),
            candidature.getDescription(),
            candidature.getCoursIds()
        );

        utilisateur.getRoles().add(Role.TUTEUR);
        utilisateurRepository.save(utilisateur);

        candidature.setStatut(StatutCandidatureTuteur.APPROUVEE);
        candidature.setMotifRefus(null);
        candidature.setDateDecision(LocalDateTime.now());

        return convertir(candidatureRepository.save(candidature), utilisateur);
    }

    @Transactional
    public CandidatureTuteurResponse refuser(
            Long candidatureId, DecisionCandidatureRequest request) {
        CandidatureTuteurEntity candidature = chargerEnAttente(candidatureId);

        UtilisateurEntity utilisateur = utilisateurRepository.findById(
            candidature.getUtilisateurId()
        ).orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable."));

        candidature.setStatut(StatutCandidatureTuteur.REFUSEE);
        candidature.setMotifRefus(request.motif().trim());
        candidature.setDateDecision(LocalDateTime.now());

        return convertir(candidatureRepository.save(candidature), utilisateur);
    }

    private CandidatureTuteurEntity chargerEnAttente(Long id) {
        CandidatureTuteurEntity candidature = candidatureRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Candidature introuvable."));

        if (candidature.getStatut() != StatutCandidatureTuteur.EN_ATTENTE) {
            throw new IllegalArgumentException("Cette candidature a déjà été traitée.");
        }
        return candidature;
    }

    private CandidatureTuteurResponse convertirAvecUtilisateur(
            CandidatureTuteurEntity candidature) {
        UtilisateurEntity utilisateur = utilisateurRepository
            .findById(candidature.getUtilisateurId())
            .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable."));
        return convertir(candidature, utilisateur);
    }

    private CandidatureTuteurResponse convertir(
            CandidatureTuteurEntity c, UtilisateurEntity u) {
        return new CandidatureTuteurResponse(
            c.getId(), u.getId(), u.getNom(), u.getPrenom(), u.getEmail(),
            c.getDescription(), c.getMotivation(), c.getCoursIds(),
            c.getStatut().name(), c.getMotifRefus(),
            c.getDateCreation(), c.getDateDecision()
        );
    }
}
