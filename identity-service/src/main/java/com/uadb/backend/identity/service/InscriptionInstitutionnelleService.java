package com.uadb.backend.identity.service;

import com.uadb.backend.identity.dto.InscriptionInstitutionnelleRequest;
import com.uadb.backend.identity.dto.InscriptionInstitutionnelleResponse;
import com.uadb.backend.identity.entity.EtudiantEntity;
import com.uadb.backend.identity.entity.EnseignantEntity;
import com.uadb.backend.identity.entity.UtilisateurEntity;
import com.uadb.backend.identity.enums.Role;
import com.uadb.backend.identity.enums.StatutUtilisateur;
import com.uadb.backend.identity.institution.InstitutionRepository;
import com.uadb.backend.identity.institution.ReferenceInstitutionnelle;
import com.uadb.backend.identity.repository.EtudiantRepository;
import com.uadb.backend.identity.repository.EnseignantRepository;
import com.uadb.backend.identity.repository.UtilisateurRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Locale;
import java.util.Set;

@Service
public class InscriptionInstitutionnelleService {

    private final InstitutionRepository institutionRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final EtudiantRepository etudiantRepository;
    private final EnseignantRepository enseignantRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecureRandom secureRandom = new SecureRandom();

    public InscriptionInstitutionnelleService(
            InstitutionRepository institutionRepository,
            UtilisateurRepository utilisateurRepository,
            EtudiantRepository etudiantRepository,
            EnseignantRepository enseignantRepository,
            PasswordEncoder passwordEncoder) {
        this.institutionRepository = institutionRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.etudiantRepository = etudiantRepository;
        this.enseignantRepository = enseignantRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public InscriptionInstitutionnelleResponse inscrire(
            InscriptionInstitutionnelleRequest request) {

        String matricule = request.matricule().trim();
        String email = request.email().trim().toLowerCase(Locale.ROOT);

        ReferenceInstitutionnelle reference = institutionRepository
                .rechercherEtudiant(matricule, email)
                .or(() -> institutionRepository.rechercherEnseignant(matricule, email))
                .orElseThrow(() -> new IllegalArgumentException(
                        "Matricule ou adresse email institutionnelle invalide."
                ));

        if (!reference.actif()) {
            throw new IllegalArgumentException(
                    "Cette référence institutionnelle est inactive."
            );
        }

        if (utilisateurRepository.existsByEmail(email)) {
            throw new IllegalArgumentException(
                    "Un compte existe déjà avec cette adresse email."
            );
        }

        if (reference.telephone() == null || reference.telephone().isBlank()) {
            throw new IllegalArgumentException(
                    "Aucun numéro de téléphone n'est associé à cette référence."
            );
        }

        Role role;
        if ("ETUDIANT".equals(reference.type())) {
            role = Role.ETUDIANT;

            if (etudiantRepository.existsByCodePermanent(matricule)) {
                throw new IllegalArgumentException(
                        "Un profil étudiant existe déjà pour ce matricule."
                );
            }
        } else if ("ENSEIGNANT".equals(reference.type())) {
            role = Role.ENSEIGNANT;

            if (enseignantRepository
                    .findByMatriculeProfessionnel(matricule).isPresent()) {
                throw new IllegalArgumentException(
                        "Un profil enseignant existe déjà pour ce matricule."
                );
            }
        } else {
            throw new IllegalArgumentException(
                    "Type de compte institutionnel non reconnu."
            );
        }

        String motDePasseTemporaire = genererMotDePasseTemporaire();

        UtilisateurEntity utilisateur = new UtilisateurEntity();
        utilisateur.setNom(reference.nom());
        utilisateur.setPrenom(reference.prenom());
        utilisateur.setEmail(email);
        utilisateur.setTelephone(reference.telephone());
        utilisateur.setMotDePasse(passwordEncoder.encode(motDePasseTemporaire));
        utilisateur.setRoles(Set.of(role));
        utilisateur.setStatut(StatutUtilisateur.ACTIF);
        utilisateur.setDoitChangerMotDePasse(true);

        UtilisateurEntity sauvegarde = utilisateurRepository.save(utilisateur);

        if (role == Role.ETUDIANT) {
            EtudiantEntity profil = new EtudiantEntity();
            profil.setUtilisateur(sauvegarde);
            profil.setCodePermanent(matricule);
            profil.setFiliereId(reference.filiereId());
            profil.setNiveauId(reference.niveauId());
            etudiantRepository.save(profil);
        } else {
            EnseignantEntity profil = new EnseignantEntity();
            profil.setUtilisateur(sauvegarde);
            profil.setMatriculeProfessionnel(matricule);
            enseignantRepository.save(profil);
        }

        return new InscriptionInstitutionnelleResponse(
                sauvegarde.getId(),
                sauvegarde.getNom(),
                sauvegarde.getPrenom(),
                sauvegarde.getEmail(),
                role.name(),
                motDePasseTemporaire,
                "Compte créé. Vous devez changer votre mot de passe à la première connexion."
        );
    }

    private String genererMotDePasseTemporaire() {
        byte[] bytes = new byte[18];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
