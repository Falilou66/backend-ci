package com.uadb.backend.matching.service;

import com.uadb.backend.matching.dto.ProvisionnerTuteurRequest;
import com.uadb.backend.matching.client.AcademicCoursClient;
import com.uadb.backend.matching.entity.TuteurEntity;
import com.uadb.backend.matching.repository.TuteurRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TuteurInterneService {

    private final TuteurRepository repository;
    private final AcademicCoursClient academicCoursClient;

    public TuteurInterneService(
        TuteurRepository repository,
        AcademicCoursClient academicCoursClient
    ) {
        this.repository = repository;
        this.academicCoursClient = academicCoursClient;
    }

    @Transactional
    public void provisionner(ProvisionnerTuteurRequest request) {
        request.coursIds().stream().distinct()
            .forEach(academicCoursClient::verifierCours);

        if (repository.findByUtilisateurId(request.utilisateurId()).isPresent()) {
            return;
        }

        TuteurEntity tuteur = new TuteurEntity();
        tuteur.setUtilisateurId(request.utilisateurId());
        tuteur.setDescription(request.description().trim());
        tuteur.setCoursIds(request.coursIds().stream().distinct().toList());
        repository.save(tuteur);
    }
}
