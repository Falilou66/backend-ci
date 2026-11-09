package com.uadb.backend.matching.service;

import java.time.LocalTime;
import java.util.List;

import com.uadb.backend.matching.dto.DisponibiliteRequest;
import com.uadb.backend.matching.dto.DisponibiliteResponse;
import com.uadb.backend.matching.entity.DisponibiliteEntity;
import com.uadb.backend.matching.entity.StatutProfilTuteur;
import com.uadb.backend.matching.entity.TuteurEntity;
import com.uadb.backend.matching.repository.DisponibiliteRepository;
import com.uadb.backend.matching.repository.TuteurRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class DisponibiliteService {

    private final DisponibiliteRepository disponibiliteRepository;
    private final TuteurRepository tuteurRepository;

    public DisponibiliteService(
            DisponibiliteRepository disponibiliteRepository,
            TuteurRepository tuteurRepository) {
        this.disponibiliteRepository = disponibiliteRepository;
        this.tuteurRepository = tuteurRepository;
    }

    @Transactional(readOnly = true)
    public List<DisponibiliteResponse> lister(Long utilisateurId) {
        TuteurEntity tuteur = trouverTuteur(utilisateurId);
        return disponibiliteRepository
                .findAllByTuteurIdOrderByJourAscHeureDebutAsc(tuteur.getId())
                .stream().map(DisponibiliteResponse::depuis).toList();
    }

    @Transactional
    public DisponibiliteResponse ajouter(Long utilisateurId, DisponibiliteRequest request) {
        TuteurEntity tuteur = trouverTuteur(utilisateurId);
        verifierHoraires(request.heureDebut(), request.heureFin());

        verifierChevauchement(tuteur.getId(), request, null);

        DisponibiliteEntity d = new DisponibiliteEntity();
        d.setTuteurId(tuteur.getId());
        appliquer(d, request);
        return DisponibiliteResponse.depuis(disponibiliteRepository.save(d));
    }

    @Transactional
    public DisponibiliteResponse modifier(
            Long utilisateurId, Long disponibiliteId, DisponibiliteRequest request) {
        TuteurEntity tuteur = trouverTuteur(utilisateurId);
        DisponibiliteEntity d = disponibiliteRepository
                .findByIdAndTuteurId(disponibiliteId, tuteur.getId())
                .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Disponibilité introuvable"));

        verifierHoraires(request.heureDebut(), request.heureFin());
        verifierChevauchement(tuteur.getId(), request, disponibiliteId);

        appliquer(d, request);
        return DisponibiliteResponse.depuis(disponibiliteRepository.save(d));
    }

    @Transactional
    public void supprimer(Long utilisateurId, Long disponibiliteId) {
        TuteurEntity tuteur = trouverTuteur(utilisateurId);
        DisponibiliteEntity d = disponibiliteRepository
                .findByIdAndTuteurId(disponibiliteId, tuteur.getId())
                .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Disponibilité introuvable"));
        disponibiliteRepository.delete(d);
    }

    private TuteurEntity trouverTuteur(Long utilisateurId) {
        return tuteurRepository.findByUtilisateurId(utilisateurId)
                .filter(t -> t.getStatut() == StatutProfilTuteur.ACTIF)
                .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Profil tuteur introuvable"));
    }

    private void verifierHoraires(LocalTime debut, LocalTime fin) {
        if (!debut.isBefore(fin)) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "L'heure de fin doit être après l'heure de début");
        }
    }

    private void verifierChevauchement(
            Long tuteurId, DisponibiliteRequest request, Long idExclu) {
        boolean chevauchement = disponibiliteRepository
            .findAllByTuteurIdOrderByJourAscHeureDebutAsc(tuteurId)
            .stream()
            .filter(d -> d.isDisponible())
            .filter(d -> d.getJour() == request.jour())
            .filter(d -> idExclu == null || !d.getId().equals(idExclu))
            .anyMatch(d -> request.heureDebut().isBefore(d.getHeureFin())
                    && request.heureFin().isAfter(d.getHeureDebut()));

        if (request.disponible() && chevauchement) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT, "Cette plage chevauche une disponibilité existante");
        }
    }

    private void appliquer(DisponibiliteEntity d, DisponibiliteRequest request) {
        d.setJour(request.jour());
        d.setHeureDebut(request.heureDebut());
        d.setHeureFin(request.heureFin());
        d.setDisponible(request.disponible());
    }
}
