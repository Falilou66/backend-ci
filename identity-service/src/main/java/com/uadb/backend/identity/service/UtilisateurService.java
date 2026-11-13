package com.uadb.backend.identity.service;

import com.uadb.backend.identity.dto.InscriptionRequest;
import com.uadb.backend.identity.dto.UtilisateurResponse;
import com.uadb.backend.identity.dto.ModifierProfilRequest;
import com.uadb.backend.identity.entity.EtudiantEntity;
import com.uadb.backend.identity.entity.EnseignantEntity;
import com.uadb.backend.identity.entity.UtilisateurEntity;
import com.uadb.backend.identity.enums.Role;
import com.uadb.backend.identity.enums.StatutUtilisateur;
import com.uadb.backend.identity.repository.EtudiantRepository;
import com.uadb.backend.identity.repository.EnseignantRepository;
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
    private final EnseignantRepository enseignantRepository;
    private final PasswordEncoder passwordEncoder;

    public UtilisateurService(
            UtilisateurRepository utilisateurRepository,
            EtudiantRepository etudiantRepository,
            EnseignantRepository enseignantRepository,
            PasswordEncoder passwordEncoder) {

        this.utilisateurRepository = utilisateurRepository;
        this.etudiantRepository = etudiantRepository;
        this.enseignantRepository = enseignantRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private static final String DOMAINE_EMAIL_VALID = "@uadb.edu.sn";

    @Transactional
    public UtilisateurResponse inscrire(InscriptionRequest request) {

        String email = request.email().trim().toLowerCase(Locale.ROOT);

        validerDomaineEmail(email);

        if (utilisateurRepository.existsByEmail(email)) {
            throw new IllegalArgumentException(
                    "Cette adresse email est déjà utilisée."
            );
        }

        if (request.role() == Role.ADMINISTRATEUR) {
            throw new IllegalArgumentException(
                    "Les comptes administrateur sont créés par un administrateur."
            );
        }

        if (request.role() == Role.ETUDIANT) {
            inscrireEtudiant(request);
        } else if (request.role() == Role.ENSEIGNANT) {
            inscrireEnseignant(request);
        } else {
            throw new IllegalArgumentException(
                    "Le type de compte doit être ETUDIANT ou ENSEIGNANT."
            );
        }

        UtilisateurEntity utilisateur = new UtilisateurEntity();
        utilisateur.setNom(request.nom().trim());
        utilisateur.setPrenom(request.prenom().trim());
        utilisateur.setEmail(email);
        utilisateur.setMotDePasse(
                passwordEncoder.encode(request.motDePasse())
        );
        utilisateur.setTelephone(request.telephone());
        utilisateur.setRoles(Set.of(request.role()));
        utilisateur.setStatut(StatutUtilisateur.ACTIF);

        UtilisateurEntity saved = utilisateurRepository.save(utilisateur);

        if (request.role() == Role.ETUDIANT) {
            EtudiantEntity etudiant = new EtudiantEntity();
            etudiant.setUtilisateur(saved);
            etudiant.setCodePermanent(request.codePermanent().trim());
            etudiant.setFiliereId(request.filiereId());
            etudiant.setNiveauId(request.niveauId());
            etudiantRepository.save(etudiant);
        } else {
            EnseignantEntity enseignant = new EnseignantEntity();
            enseignant.setUtilisateur(saved);
            enseignant.setMatriculeProfessionnel(
                    normaliserMatricule(request.matriculeProfessionnel()));
            enseignantRepository.save(enseignant);
        }

        return new UtilisateurResponse(
                saved.getId(),
                saved.getNom(),
                saved.getPrenom(),
                saved.getEmail(),
                saved.getTelephone(),
                saved.getRoles(),
                saved.getStatut()
        );
    }

    private void inscrireEtudiant(InscriptionRequest request) {
        String codePermanent = request.codePermanent();
        if (codePermanent == null || codePermanent.isBlank()) {
            throw new IllegalArgumentException(
                    "Le code permanent est obligatoire pour un étudiant."
            );
        }
        codePermanent = codePermanent.trim();
        if (etudiantRepository.existsByCodePermanent(codePermanent)) {
            throw new IllegalArgumentException(
                    "Ce code permanent est déjà utilisé."
            );
        }
    }

    private void inscrireEnseignant(InscriptionRequest request) {
        String matricule = normaliserMatricule(request.matriculeProfessionnel());
        if (matricule != null
                && enseignantRepository.findByMatriculeProfessionnel(matricule).isPresent()) {
            throw new IllegalArgumentException(
                    "Ce matricule professionnel est déjà utilisé."
            );
        }
    }

    private String normaliserMatricule(String matricule) {
        if (matricule == null || matricule.isBlank()) {
            return null;
        }
        return matricule.trim();
    }

    private void validerDomaineEmail(String email) {
        if (!email.toLowerCase(Locale.ROOT).endsWith(DOMAINE_EMAIL_VALID)) {
            throw new IllegalArgumentException(
                    "L'adresse email doit se terminer par " + DOMAINE_EMAIL_VALID
            );
        }
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