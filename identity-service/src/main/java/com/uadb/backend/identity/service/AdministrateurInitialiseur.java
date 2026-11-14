package com.uadb.backend.identity.service;

import com.uadb.backend.identity.entity.AdministrateurEntity;
import com.uadb.backend.identity.entity.UtilisateurEntity;
import com.uadb.backend.identity.enums.Role;
import com.uadb.backend.identity.enums.StatutUtilisateur;
import com.uadb.backend.identity.repository.AdministrateurRepository;
import com.uadb.backend.identity.repository.UtilisateurRepository;
import java.util.Locale;
import java.util.Set;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AdministrateurInitialiseur implements ApplicationRunner {

    private final UtilisateurRepository utilisateurRepository;
    private final AdministrateurRepository administrateurRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.bootstrap-admin.email:}")
    private String email;

    @Value("${app.bootstrap-admin.password:}")
    private String password;

    @Value("${app.bootstrap-admin.nom:Administrateur}")
    private String nom;

    @Value("${app.bootstrap-admin.prenom:Initial}")
    private String prenom;

    @Value("${app.bootstrap-admin.telephone:}")
    private String telephone;

    public AdministrateurInitialiseur(
            UtilisateurRepository utilisateurRepository,
            AdministrateurRepository administrateurRepository,
            PasswordEncoder passwordEncoder) {
        this.utilisateurRepository = utilisateurRepository;
        this.administrateurRepository = administrateurRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        activerComptesEnAttente();

        if ((email == null || email.isBlank())
                && (password == null || password.isBlank())) {
            return;
        }
        if (email == null || email.isBlank()
                || password == null || password.length() < 8) {
            throw new IllegalStateException(
                    "L'email admin initial et un mot de passe d'au moins 8 caractères sont requis."
            );
        }

        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);
        UtilisateurEntity utilisateur = utilisateurRepository
                .findByEmail(normalizedEmail)
                .orElseGet(() -> creerUtilisateur(normalizedEmail));

        if (!utilisateur.getRoles().contains(Role.ADMINISTRATEUR)) {
            throw new IllegalStateException(
                    "L'adresse configurée pour l'administrateur initial est déjà associée à un compte non administrateur."
            );
        }

        if (!administrateurRepository.existsById(utilisateur.getId())) {
            AdministrateurEntity profil = new AdministrateurEntity();
            profil.setUtilisateur(utilisateur);
            administrateurRepository.save(profil);
        }
    }

    private void activerComptesEnAttente() {
        Set<UtilisateurEntity> comptesEnAttente = Set.copyOf(
                utilisateurRepository.findAllByStatut(
                        StatutUtilisateur.EN_ATTENTE_VALIDATION
                )
        );
        if (comptesEnAttente.isEmpty()) {
            return;
        }

        comptesEnAttente.forEach(
                utilisateur -> utilisateur.setStatut(StatutUtilisateur.ACTIF)
        );
        utilisateurRepository.saveAll(comptesEnAttente);
    }

    private UtilisateurEntity creerUtilisateur(String normalizedEmail) {
        UtilisateurEntity utilisateur = new UtilisateurEntity();
        utilisateur.setNom(nom.trim());
        utilisateur.setPrenom(prenom.trim());
        utilisateur.setEmail(normalizedEmail);
        utilisateur.setTelephone(telephone.isBlank() ? null : telephone.trim());
        utilisateur.setMotDePasse(passwordEncoder.encode(password));
        utilisateur.setRoles(Set.of(Role.ADMINISTRATEUR));
        utilisateur.setStatut(StatutUtilisateur.ACTIF);
        return utilisateurRepository.save(utilisateur);
    }
}
