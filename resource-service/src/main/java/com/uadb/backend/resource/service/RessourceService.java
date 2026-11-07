package com.uadb.backend.resource.service;

import com.uadb.backend.resource.entity.RessourceEntity;
import com.uadb.backend.resource.repository.RessourceRepository;
import com.uadb.backend.resource.dto.RessourceRequest;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class RessourceService {

    private final RessourceRepository repository;

    public RessourceService(RessourceRepository repository) {
        this.repository = repository;
    }

    public RessourceEntity creer(RessourceRequest request) {
        RessourceEntity r = new RessourceEntity();
        remplir(r, request);
        return repository.save(r);
    }

    public List<RessourceEntity> lister() {
        return repository.findAll();
    }

    public List<RessourceEntity> trouverParCours(Long coursId) {
        return repository.findByCoursIdOrderByDateCreationDesc(coursId);
    }

    public RessourceEntity modifier(Long id, RessourceRequest request) {
        RessourceEntity r = repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Ressource introuvable"));
        remplir(r, request);
        return repository.save(r);
    }

    public void supprimer(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Ressource introuvable");
        }
        repository.deleteById(id);
    }

    private void remplir(RessourceEntity r, RessourceRequest request) {
        r.setTitre(request.titre());
        r.setDescription(request.description());
        r.setType(request.type());
        r.setUrl(request.url());
        r.setCoursId(request.coursId());
        r.setAuteurId(request.auteurId());
    }
}
