package com.uadb.backend.academic.service;

import com.uadb.backend.academic.dto.FiliereRequest;
import com.uadb.backend.academic.dto.FiliereResponse;
import com.uadb.backend.academic.entity.FiliereEntity;
import com.uadb.backend.academic.repository.FiliereRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FiliereService {

    private final FiliereRepository repository;

    public FiliereService(FiliereRepository repository) {
        this.repository = repository;
    }

    public FiliereResponse creer(FiliereRequest request) {
        if (repository.existsByNomIgnoreCase(request.nom())) {
            throw new IllegalArgumentException("Cette filière existe déjà.");
        }

        FiliereEntity filiere = new FiliereEntity();
        filiere.setNom(request.nom());
        filiere.setDescription(request.description());

        return convertir(repository.save(filiere));
    }

    public List<FiliereResponse> lister() {
        return repository.findAll().stream()
                .map(this::convertir)
                .toList();
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

        filiere.setNom(request.nom());
        filiere.setDescription(request.description());

        return convertir(repository.save(filiere));
    }

    public void supprimer(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Filière introuvable.");
        }
        repository.deleteById(id);
    }

    private FiliereResponse convertir(FiliereEntity filiere) {
        return new FiliereResponse(
                filiere.getId(),
                filiere.getNom(),
                filiere.getDescription(),
                filiere.getDateCreation()
        );
    }
}