package com.uadb.backend.academic.service;

import com.uadb.backend.academic.dto.*;
import com.uadb.backend.academic.entity.*;
import com.uadb.backend.academic.repository.*;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class FiliereService {
    private final FiliereRepository repository;
    private final UfrRepository ufrRepository;

    public FiliereService(FiliereRepository repository, UfrRepository ufrRepository) {
        this.repository = repository;
        this.ufrRepository = ufrRepository;
    }

    public FiliereResponse creer(FiliereRequest request) {
        if (repository.existsByNomIgnoreCase(request.nom())) {
            throw new IllegalArgumentException("Cette filière existe déjà.");
        }

        FiliereEntity filiere = new FiliereEntity();
        filiere.setNom(request.nom().trim());
        filiere.setDescription(request.description());
        filiere.setUfr(trouverUfr(request.ufrId()));

        return convertir(repository.save(filiere));
    }

    public List<FiliereResponse> lister() {
        return repository.findAll().stream().map(this::convertir).toList();
    }

    public FiliereResponse modifier(Long id, FiliereRequest request) {
        FiliereEntity filiere = repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Filière introuvable."));

        boolean nomUtilise = repository.findByNomIgnoreCase(request.nom())
            .filter(existing -> !existing.getId().equals(id))
            .isPresent();

        if (nomUtilise) {
            throw new IllegalArgumentException("Ce nom est déjà utilisé.");
        }

        filiere.setNom(request.nom().trim());
        filiere.setDescription(request.description());
        filiere.setUfr(trouverUfr(request.ufrId()));

        return convertir(repository.save(filiere));
    }

    public void supprimer(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Filière introuvable.");
        }
        repository.deleteById(id);
    }

    private UfrEntity trouverUfr(Long id) {
        return ufrRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("UFR introuvable."));
    }

    private FiliereResponse convertir(FiliereEntity filiere) {
        UfrEntity ufr = filiere.getUfr();
        return new FiliereResponse(
            filiere.getId(),
            filiere.getNom(),
            filiere.getDescription(),
            filiere.getDateCreation(),
            ufr != null ? ufr.getId() : null,
            ufr != null ? ufr.getNom() : null
        );
    }
}
