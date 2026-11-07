package com.uadb.backend.identity.service;

import com.uadb.backend.identity.dto.UtilisateurAdminResponse;
import com.uadb.backend.identity.entity.UtilisateurEntity;
import com.uadb.backend.identity.enums.StatutUtilisateur;
import com.uadb.backend.identity.repository.UtilisateurRepository;
import com.uadb.backend.identity.dto.ModifierRolesRequest;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.stream.Collectors;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdministrationService {

    private final UtilisateurRepository utilisateurRepository;

    public AdministrationService(
            UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    @Transactional(readOnly = true)
    public List<UtilisateurAdminResponse> listerInscriptionsEnAttente() {

        List<UtilisateurEntity> utilisateurs =
                utilisateurRepository.findAllByStatut(
                        StatutUtilisateur.EN_ATTENTE_VALIDATION
                );

        return utilisateurs.stream()
                .map(utilisateur -> new UtilisateurAdminResponse(
                        utilisateur.getId(),
                        utilisateur.getNom(),
                        utilisateur.getPrenom(),
                        utilisateur.getEmail(),
                        utilisateur.getTelephone(),
                        utilisateur.getStatut().name(),
                        utilisateur.getRoles()
                                .stream()
                                .map(Enum::name)
                                .collect(Collectors.toSet())
                ))
                .toList();
    }
    @Transactional
        public UtilisateurAdminResponse validerInscription(Long utilisateurId) {

        UtilisateurEntity utilisateur = utilisateurRepository
                .findById(utilisateurId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Utilisateur introuvable."
                        )
                );

        if (utilisateur.getStatut() != StatutUtilisateur.EN_ATTENTE_VALIDATION) {
                throw new IllegalArgumentException(
                        "Cette inscription a déjà été traitée."
                );
        }

        utilisateur.setStatut(StatutUtilisateur.ACTIF);

        UtilisateurEntity utilisateurEnregistre =
                utilisateurRepository.save(utilisateur);

        return new UtilisateurAdminResponse(
                utilisateurEnregistre.getId(),
                utilisateurEnregistre.getNom(),
                utilisateurEnregistre.getPrenom(),
                utilisateurEnregistre.getEmail(),
                utilisateurEnregistre.getTelephone(),
                utilisateurEnregistre.getStatut().name(),
                utilisateurEnregistre.getRoles()
                        .stream()
                        .map(Enum::name)
                        .collect(java.util.stream.Collectors.toSet())
        );
        }
        @Transactional
public UtilisateurAdminResponse refuserInscription(Long utilisateurId) {

    UtilisateurEntity utilisateur = utilisateurRepository
            .findById(utilisateurId)
            .orElseThrow(() ->
                    new IllegalArgumentException(
                            "Utilisateur introuvable."
                    )
            );

    if (utilisateur.getStatut() != StatutUtilisateur.EN_ATTENTE_VALIDATION) {
        throw new IllegalArgumentException(
                "Cette inscription a déjà été traitée."
        );
    }

    utilisateur.setStatut(StatutUtilisateur.INACTIF);

    UtilisateurEntity utilisateurEnregistre =
            utilisateurRepository.save(utilisateur);

    return new UtilisateurAdminResponse(
            utilisateurEnregistre.getId(),
            utilisateurEnregistre.getNom(),
            utilisateurEnregistre.getPrenom(),
            utilisateurEnregistre.getEmail(),
            utilisateurEnregistre.getTelephone(),
            utilisateurEnregistre.getStatut().name(),
            utilisateurEnregistre.getRoles()
                    .stream()
                    .map(Enum::name)
                    .collect(java.util.stream.Collectors.toSet())
    );
}
@Transactional
public UtilisateurAdminResponse modifierRoles(
        Long utilisateurId,
        ModifierRolesRequest request) {

    UtilisateurEntity utilisateur = utilisateurRepository
            .findById(utilisateurId)
            .orElseThrow(() ->
                    new IllegalArgumentException(
                            "Utilisateur introuvable."
                    )
            );

    utilisateur.setRoles(request.roles());

    UtilisateurEntity utilisateurModifie =
            utilisateurRepository.save(utilisateur);

    return new UtilisateurAdminResponse(
            utilisateurModifie.getId(),
            utilisateurModifie.getNom(),
            utilisateurModifie.getPrenom(),
            utilisateurModifie.getEmail(),
            utilisateurModifie.getTelephone(),
            utilisateurModifie.getStatut().name(),
            utilisateurModifie.getRoles()
                    .stream()
                    .map(Enum::name)
                    .collect(java.util.stream.Collectors.toSet())
    );
}
public Page<UtilisateurAdminResponse> listerUtilisateurs(Pageable pageable) {

    return utilisateurRepository.findAll(pageable)
            .map(utilisateur -> new UtilisateurAdminResponse(
                    utilisateur.getId(),
                    utilisateur.getNom(),
                    utilisateur.getPrenom(),
                    utilisateur.getEmail(),
                    utilisateur.getTelephone(),
                    utilisateur.getStatut().name(),
                    utilisateur.getRoles()
                            .stream()
                            .map(Enum::name)
                            .collect(Collectors.toSet())
            ));
}
public Page<UtilisateurAdminResponse> rechercherUtilisateurs(
        String recherche,
        Pageable pageable) {

    return utilisateurRepository.rechercher(recherche, pageable)
            .map(utilisateur -> new UtilisateurAdminResponse(
                    utilisateur.getId(),
                    utilisateur.getNom(),
                    utilisateur.getPrenom(),
                    utilisateur.getEmail(),
                    utilisateur.getTelephone(),
                    utilisateur.getStatut().name(),
                    utilisateur.getRoles()
                            .stream()
                            .map(Enum::name)
                            .collect(Collectors.toSet())
            ));
}
}
