package com.uadb.backend.identity.service;

import com.uadb.backend.identity.dto.CreerAdministrateurRequest;
import com.uadb.backend.identity.dto.ModifierRolesRequest;
import com.uadb.backend.identity.dto.UtilisateurAdminResponse;
import com.uadb.backend.identity.entity.AdministrateurEntity;
import com.uadb.backend.identity.entity.UtilisateurEntity;
import com.uadb.backend.identity.enums.Role;
import com.uadb.backend.identity.enums.StatutUtilisateur;
import com.uadb.backend.identity.repository.AdministrateurRepository;
import com.uadb.backend.identity.repository.UtilisateurRepository;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdministrationService {

    private final UtilisateurRepository utilisateurRepository;
    private final AdministrateurRepository administrateurRepository;
    private final PasswordEncoder passwordEncoder;

    public AdministrationService(
            UtilisateurRepository utilisateurRepository,
            AdministrateurRepository administrateurRepository,
            PasswordEncoder passwordEncoder) {
        this.utilisateurRepository = utilisateurRepository;
        this.administrateurRepository = administrateurRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UtilisateurAdminResponse creerAdministrateur(
            CreerAdministrateurRequest request) {
        String email = request.email().trim().toLowerCase(Locale.ROOT);
        if (utilisateurRepository.existsByEmail(email)) {
            throw new IllegalArgumentException(
                    "Cette adresse email est déjà utilisée."
            );
        }

        UtilisateurEntity utilisateur = new UtilisateurEntity();
        utilisateur.setNom(request.nom().trim());
        utilisateur.setPrenom(request.prenom().trim());
        utilisateur.setEmail(email);
        utilisateur.setTelephone(request.telephone());
        utilisateur.setMotDePasse(passwordEncoder.encode(request.motDePasse()));
        utilisateur.setRoles(Set.of(Role.ADMINISTRATEUR));
        utilisateur.setStatut(StatutUtilisateur.ACTIF);
        UtilisateurEntity saved = utilisateurRepository.save(utilisateur);
        creerProfilAdministrateur(saved);
        return versUtilisateurAdmin(saved);
    }

    @Transactional
    public UtilisateurAdminResponse modifierRoles(
            Long utilisateurId,
            ModifierRolesRequest request) {
        UtilisateurEntity utilisateur = utilisateurRepository
                .findById(utilisateurId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Utilisateur introuvable."
                ));

        utilisateur.setRoles(request.roles());
        UtilisateurEntity updated = utilisateurRepository.save(utilisateur);
        if (request.roles().contains(Role.ADMINISTRATEUR)
                && !administrateurRepository.existsById(updated.getId())) {
            creerProfilAdministrateur(updated);
        }
        return versUtilisateurAdmin(updated);
    }

    @Transactional(readOnly = true)
    public Page<UtilisateurAdminResponse> listerUtilisateurs(Pageable pageable) {
        return utilisateurRepository.findAll(pageable).map(this::versUtilisateurAdmin);
    }

    @Transactional(readOnly = true)
    public Page<UtilisateurAdminResponse> rechercherUtilisateurs(
            String recherche,
            Pageable pageable) {
        return utilisateurRepository.rechercher(recherche, pageable)
                .map(this::versUtilisateurAdmin);
    }

    private void creerProfilAdministrateur(UtilisateurEntity utilisateur) {
        AdministrateurEntity profil = new AdministrateurEntity();
        profil.setUtilisateur(utilisateur);
        administrateurRepository.save(profil);
    }

    private UtilisateurAdminResponse versUtilisateurAdmin(
            UtilisateurEntity utilisateur) {
        return new UtilisateurAdminResponse(
                utilisateur.getId(),
                utilisateur.getNom(),
                utilisateur.getPrenom(),
                utilisateur.getEmail(),
                utilisateur.getTelephone(),
                utilisateur.getStatut().name(),
                utilisateur.getRoles().stream()
                        .map(Enum::name)
                        .collect(Collectors.toSet())
        );
    }
}
