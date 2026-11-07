package com.uadb.backend.identity.service;

import com.uadb.backend.identity.dto.InscriptionRequest;
import com.uadb.backend.identity.dto.UtilisateurResponse;
import com.uadb.backend.identity.dto.ModifierProfilRequest;
import com.uadb.backend.identity.entity.EtudiantEntity;
import com.uadb.backend.identity.entity.UtilisateurEntity;
import com.uadb.backend.identity.enums.Role;
import com.uadb.backend.identity.repository.EtudiantRepository;
import com.uadb.backend.identity.repository.UtilisateurRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uadb.backend.identity.dto.ProfilUtilisateurResponse;

import java.util.Set;
import java.util.Locale;

@Service
public class UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final EtudiantRepository etudiantRepository;
    private final PasswordEncoder passwordEncoder;

    public UtilisateurService(
            UtilisateurRepository utilisateurRepository,
            EtudiantRepository etudiantRepository,
            PasswordEncoder passwordEncoder) {

        this.utilisateurRepository = utilisateurRepository;
        this.etudiantRepository = etudiantRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UtilisateurResponse inscrireEtudiant(InscriptionRequest request) {

        String email = request.email().trim().toLowerCase(Locale.ROOT);
        String codePermanent = request.codePermanent().trim();

        // Vérifier si l'adresse email existe déjà
        if (utilisateurRepository.existsByEmail(email)) {
            throw new IllegalArgumentException(
                    "Cette adresse email est déjà utilisée."
            );
        }

        // Vérifier si le code permanent existe déjà
        if (etudiantRepository.existsByCodePermanent(codePermanent)) {
            throw new IllegalArgumentException(
                    "Ce code permanent est déjà utilisé."
            );
        }

        // Créer le compte utilisateur
        UtilisateurEntity utilisateur = new UtilisateurEntity();

        utilisateur.setNom(request.nom().trim());
        utilisateur.setPrenom(request.prenom().trim());
        utilisateur.setEmail(email);
        utilisateur.setMotDePasse(
                passwordEncoder.encode(request.motDePasse())
        );
        utilisateur.setTelephone(request.telephone());
        utilisateur.setRoles(Set.of(Role.ETUDIANT));

        // Enregistrer l'utilisateur
        UtilisateurEntity utilisateurEnregistre =
                utilisateurRepository.save(utilisateur);

        // Créer le profil étudiant associé
        EtudiantEntity etudiant = new EtudiantEntity();

        etudiant.setUtilisateur(utilisateurEnregistre);
        etudiant.setCodePermanent(codePermanent);
        etudiant.setFiliereId(request.filiereId());
        etudiant.setNiveauId(request.niveauId());

        etudiantRepository.save(etudiant);

        // Retourner les informations publiques
        return new UtilisateurResponse(
                utilisateurEnregistre.getId(),
                utilisateurEnregistre.getNom(),
                utilisateurEnregistre.getPrenom(),
                utilisateurEnregistre.getEmail(),
                utilisateurEnregistre.getTelephone(),
                utilisateurEnregistre.getRoles(),
                utilisateurEnregistre.getStatut()
        );
    }

    @Transactional(readOnly = true)
public ProfilUtilisateurResponse consulterProfil(String email) {

    UtilisateurEntity utilisateur = utilisateurRepository
            .findByEmail(email)
            .orElseThrow(() ->
                    new IllegalArgumentException(
                            "Utilisateur introuvable."
                    )
            );

    return new ProfilUtilisateurResponse(
            utilisateur.getId(),
            utilisateur.getNom(),
            utilisateur.getPrenom(),
            utilisateur.getEmail(),
            utilisateur.getTelephone(),
            utilisateur.getStatut().name(),
            utilisateur.getRoles()
                    .stream()
                    .map(Enum::name)
                    .collect(java.util.stream.Collectors.toSet())
    );
}

@Transactional
public ProfilUtilisateurResponse modifierProfil(
        String email,
        ModifierProfilRequest request) {

    UtilisateurEntity utilisateur = utilisateurRepository
            .findByEmail(email)
            .orElseThrow(() ->
                    new IllegalArgumentException(
                            "Utilisateur introuvable."
                    )
            );

    utilisateur.setNom(request.nom().trim());
    utilisateur.setPrenom(request.prenom().trim());
    utilisateur.setTelephone(request.telephone());

    UtilisateurEntity utilisateurModifie =
            utilisateurRepository.save(utilisateur);

    return new ProfilUtilisateurResponse(
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
}