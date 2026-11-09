package com.uadb.backend.matching.service;

import com.uadb.backend.matching.client.AcademicCoursClient;
import com.uadb.backend.matching.dto.ModifierProfilTuteurRequest;
import com.uadb.backend.matching.dto.TuteurProfilResponse;
import com.uadb.backend.matching.entity.StatutProfilTuteur;
import com.uadb.backend.matching.entity.TuteurEntity;
import com.uadb.backend.matching.repository.TuteurRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class TuteurAnnuaireService {

    private final TuteurRepository repository;
    private final AcademicCoursClient academicCoursClient;

    public TuteurAnnuaireService(
        TuteurRepository repository,
        AcademicCoursClient academicCoursClient
    ) {
        this.repository = repository;
        this.academicCoursClient = academicCoursClient;
    }

    @Transactional(readOnly = true)
    public Page<TuteurProfilResponse> rechercher(Long coursId, Pageable pageable) {
        if (coursId != null) {
            academicCoursClient.verifierCours(coursId);
        }
        return repository.rechercher(StatutProfilTuteur.ACTIF, coursId, pageable)
            .map(TuteurProfilResponse::depuis);
    }

    @Transactional(readOnly = true)
    public TuteurProfilResponse consulter(Long id) {
        TuteurEntity tuteur = repository.findByIdAndStatut(id, StatutProfilTuteur.ACTIF)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Profil tuteur introuvable"));
        return TuteurProfilResponse.depuis(tuteur);
    }

    @Transactional(readOnly = true)
    public TuteurProfilResponse consulterMonProfil(Long utilisateurId) {
        TuteurEntity tuteur = repository.findByUtilisateurId(utilisateurId)
            .filter(t -> t.getStatut() == StatutProfilTuteur.ACTIF)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Votre profil tuteur est introuvable"));
        return TuteurProfilResponse.depuis(tuteur);
    }

    @Transactional
    public TuteurProfilResponse modifierMonProfil(
        Long utilisateurId, ModifierProfilTuteurRequest request
    ) {
        TuteurEntity tuteur = repository.findByUtilisateurId(utilisateurId)
            .filter(t -> t.getStatut() == StatutProfilTuteur.ACTIF)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Votre profil tuteur est introuvable"));

        request.coursIds().stream().distinct()
            .forEach(academicCoursClient::verifierCours);

        tuteur.setDescription(request.description().trim());
        tuteur.setCoursIds(request.coursIds().stream().distinct().toList());
        return TuteurProfilResponse.depuis(repository.save(tuteur));
    }
}
