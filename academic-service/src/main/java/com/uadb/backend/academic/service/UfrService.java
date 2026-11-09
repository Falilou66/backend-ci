package com.uadb.backend.academic.service;

import com.uadb.backend.academic.dto.*;
import com.uadb.backend.academic.entity.UfrEntity;
import com.uadb.backend.academic.repository.UfrRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UfrService {
    private final UfrRepository repository;

    public UfrService(UfrRepository repository) {
        this.repository = repository;
    }

    public UfrResponse creer(UfrRequest request) {
        if (repository.existsByNomIgnoreCase(request.nom())) {
            throw new IllegalArgumentException("Cette UFR existe déjà.");
        }
        UfrEntity ufr = new UfrEntity();
        ufr.setNom(request.nom().trim());
        return convertir(repository.save(ufr));
    }

    public List<UfrResponse> lister() {
        return repository.findAll().stream().map(this::convertir).toList();
    }

    public UfrResponse obtenir(Long id) {
        return convertir(trouver(id));
    }

    public UfrResponse modifier(Long id, UfrRequest request) {
        UfrEntity ufr = trouver(id);
        boolean utilise = repository.findByNomIgnoreCase(request.nom())
            .filter(existing -> !existing.getId().equals(id))
            .isPresent();

        if (utilise) {
            throw new IllegalArgumentException("Ce nom d'UFR est déjà utilisé.");
        }

        ufr.setNom(request.nom().trim());
        return convertir(repository.save(ufr));
    }

    public void supprimer(Long id) {
        UfrEntity ufr = trouver(id);
        repository.delete(ufr);
    }

    private UfrEntity trouver(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("UFR introuvable."));
    }

    private UfrResponse convertir(UfrEntity ufr) {
        return new UfrResponse(ufr.getId(), ufr.getNom(), ufr.getDateCreation());
    }
}
