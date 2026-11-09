package com.uadb.backend.academic.service;

import com.uadb.backend.academic.dto.*;
import com.uadb.backend.academic.entity.*;
import com.uadb.backend.academic.repository.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CoursService {

    private final CoursRepository coursRepository;
    private final FiliereRepository filiereRepository;
    private final NiveauRepository niveauRepository;

    public CoursService(
        CoursRepository coursRepository,
        FiliereRepository filiereRepository,
        NiveauRepository niveauRepository
    ) {
        this.coursRepository = coursRepository;
        this.filiereRepository = filiereRepository;
        this.niveauRepository = niveauRepository;
    }

    @Transactional
    public CoursResponse creer(CoursRequest request) {
        CoursEntity cours = new CoursEntity();
        appliquer(cours, request);
        return convertir(coursRepository.save(cours));
    }

    @Transactional(readOnly = true)
    public Page<CoursResponse> rechercher(
        Long filiereId,
        Long niveauId,
        String nom,
        int page,
        int size
    ) {
        if (page < 0 || size < 1 || size > 100) {
            throw new IllegalArgumentException(
                "La page doit être positive et la taille comprise entre 1 et 100."
            );
        }

        Pageable pageable = PageRequest.of(
            page, size, Sort.by("nom").ascending()
        );

        String recherche = (nom == null || nom.isBlank()) ? null : nom.trim();

        return coursRepository.rechercher(
            filiereId, niveauId, recherche, pageable
        ).map(this::convertir);
    }

    @Transactional(readOnly = true)
    public CoursResponse obtenir(Long id) {
        return convertir(trouver(id));
    }

    @Transactional
    public CoursResponse modifier(Long id, CoursRequest request) {
        CoursEntity cours = trouver(id);
        appliquer(cours, request);
        return convertir(coursRepository.save(cours));
    }

    @Transactional
    public void supprimer(Long id) {
        coursRepository.delete(trouver(id));
    }

    private void appliquer(CoursEntity cours, CoursRequest request) {
        cours.setNom(request.nom().trim());
        cours.setDescription(request.description());
        cours.setVolumeHoraire(request.volumeHoraire());

        cours.setFiliere(
            filiereRepository.findById(request.filiereId())
                .orElseThrow(() ->
                    new IllegalArgumentException("Filière introuvable."))
        );

        cours.setNiveau(
            niveauRepository.findById(request.niveauId())
                .orElseThrow(() ->
                    new IllegalArgumentException("Niveau introuvable."))
        );
    }

    private CoursEntity trouver(Long id) {
        return coursRepository.findById(id)
            .orElseThrow(() ->
                new IllegalArgumentException("Cours introuvable."));
    }

    private CoursResponse convertir(CoursEntity cours) {
        return new CoursResponse(
            cours.getId(),
            cours.getNom(),
            cours.getDescription(),
            cours.getVolumeHoraire(),
            cours.getFiliere().getId(),
            cours.getFiliere().getNom(),
            cours.getNiveau().getId(),
            cours.getNiveau().getLibelle()
        );
    }
}
