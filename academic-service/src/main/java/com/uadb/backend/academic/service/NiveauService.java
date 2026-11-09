package com.uadb.backend.academic.service;

import com.uadb.backend.academic.dto.*;
import com.uadb.backend.academic.entity.NiveauEntity;
import com.uadb.backend.academic.repository.NiveauRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class NiveauService {
    private final NiveauRepository repository;

    public NiveauService(NiveauRepository repository) {
        this.repository = repository;
    }

    public NiveauResponse creer(NiveauRequest request) {
        if (repository.existsByCodeIgnoreCase(request.code())) {
            throw new IllegalArgumentException("Ce niveau existe déjà.");
        }

        NiveauEntity niveau = new NiveauEntity();
        niveau.setCode(request.code());
        niveau.setLibelle(request.libelle());
        return convertir(repository.save(niveau));
    }

    public List<NiveauResponse> lister() {
        return repository.findAll().stream().map(this::convertir).toList();
    }

    public NiveauResponse modifier(Long id, NiveauRequest request) {
        NiveauEntity niveau = trouver(id);

        boolean codeUtilise = repository.findAll().stream()
            .anyMatch(n -> !n.getId().equals(id)
                && n.getCode().equalsIgnoreCase(request.code()));

        if (codeUtilise) {
            throw new IllegalArgumentException("Ce code de niveau est déjà utilisé.");
        }

        niveau.setCode(request.code());
        niveau.setLibelle(request.libelle());
        return convertir(repository.save(niveau));
    }

    public void supprimer(Long id) {
        repository.delete(trouver(id));
    }

    private NiveauEntity trouver(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Niveau introuvable."));
    }

    private NiveauResponse convertir(NiveauEntity niveau) {
        return new NiveauResponse(niveau.getId(), niveau.getCode(), niveau.getLibelle());
    }
}
