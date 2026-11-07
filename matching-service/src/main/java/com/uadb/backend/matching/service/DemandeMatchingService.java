package com.uadb.backend.matching.service;

import com.uadb.backend.matching.entity.*;
import com.uadb.backend.matching.repository.DemandeMatchingRepository;
import com.uadb.backend.matching.dto.DemandeMatchingRequest;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DemandeMatchingService {

    private final DemandeMatchingRepository repository;

    public DemandeMatchingService(DemandeMatchingRepository repository) {
        this.repository = repository;
    }

    public DemandeMatchingEntity creer(DemandeMatchingRequest request) {
        DemandeMatchingEntity demande = new DemandeMatchingEntity();
        demande.setEtudiantId(request.etudiantId());
        demande.setTuteurId(request.tuteurId());
        demande.setCoursId(request.coursId());
        demande.setMessage(request.message());
        return repository.save(demande);
    }

    public List<DemandeMatchingEntity> trouverParEtudiant(Long etudiantId) {
        return repository.findByEtudiantIdOrderByDateCreationDesc(etudiantId);
    }

    public List<DemandeMatchingEntity> trouverDemandesEnAttente(Long tuteurId) {
        return repository.findByTuteurIdAndStatutOrderByDateCreationDesc(
                tuteurId, StatutDemande.EN_ATTENTE);
    }

    public DemandeMatchingEntity decider(Long id, boolean accepter) {
        DemandeMatchingEntity demande = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Demande introuvable"));

        if (demande.getStatut() != StatutDemande.EN_ATTENTE) {
            throw new IllegalArgumentException("Cette demande a déjà été traitée");
        }

        demande.setStatut(accepter
                ? StatutDemande.ACCEPTEE
                : StatutDemande.REFUSEE);

        return repository.save(demande);
    }
}
