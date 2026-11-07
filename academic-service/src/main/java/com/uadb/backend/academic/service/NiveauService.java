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
        return repository.findAll().stream()
                .map(this::convertir)
                .toList();
    }

    private NiveauResponse convertir(NiveauEntity niveau) {
        return new NiveauResponse(
                niveau.getId(),
                niveau.getCode(),
                niveau.getLibelle()
        );
    }
}