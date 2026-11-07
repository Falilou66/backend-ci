package com.uadb.backend.academic.service;

import com.uadb.backend.academic.dto.*;
import com.uadb.backend.academic.entity.CoursEntity;
import com.uadb.backend.academic.repository.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CoursService {
    private final CoursRepository coursRepository;
    private final FiliereRepository filiereRepository;
    private final NiveauRepository niveauRepository;

    public CoursService(CoursRepository coursRepository,
                        FiliereRepository filiereRepository,
                        NiveauRepository niveauRepository) {
        this.coursRepository = coursRepository;
        this.filiereRepository = filiereRepository;
        this.niveauRepository = niveauRepository;
    }

    public CoursResponse creer(CoursRequest request) {
        CoursEntity cours = new CoursEntity();
        cours.setNom(request.nom());
        cours.setDescription(request.description());
        cours.setVolumeHoraire(request.volumeHoraire());

        cours.setFiliere(filiereRepository.findById(request.filiereId())
            .orElseThrow(() -> new IllegalArgumentException("Filière introuvable.")));

        cours.setNiveau(niveauRepository.findById(request.niveauId())
            .orElseThrow(() -> new IllegalArgumentException("Niveau introuvable.")));

        return convertir(coursRepository.save(cours));
    }

    public List<CoursResponse> lister() {
        return coursRepository.findAll().stream()
            .map(this::convertir)
            .toList();
    }

    private CoursResponse convertir(CoursEntity cours) {
        return new CoursResponse(
            cours.getId(), cours.getNom(), cours.getDescription(),
            cours.getVolumeHoraire(),
            cours.getFiliere().getId(), cours.getFiliere().getNom(),
            cours.getNiveau().getId(), cours.getNiveau().getLibelle()
        );
    }
}
