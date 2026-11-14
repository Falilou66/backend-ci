package com.uadb.backend.identity.service;

import com.uadb.backend.identity.dto.LoginRequest;
import com.uadb.backend.identity.dto.LoginResponse;
import com.uadb.backend.identity.entity.UtilisateurEntity;
import com.uadb.backend.identity.enums.StatutUtilisateur;
import com.uadb.backend.identity.repository.UtilisateurRepository;
import com.uadb.backend.identity.security.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthentificationService {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Value("${app.jwt.expiration}")
    private long expiration;

    public AuthentificationService(
            UtilisateurRepository utilisateurRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional(readOnly = true)
    public LoginResponse connecter(LoginRequest request) {

        String email = request.email().trim().toLowerCase(Locale.ROOT);

        UtilisateurEntity utilisateur = utilisateurRepository
                .findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException(
                        "Email ou mot de passe incorrect."
                ));

        if (!passwordEncoder.matches(
                request.motDePasse(), utilisateur.getMotDePasse())) {
            throw new BadCredentialsException(
                    "Email ou mot de passe incorrect."
            );
        }

        if (utilisateur.getStatut() != StatutUtilisateur.ACTIF) {
            throw new DisabledException("Ce compte n'est pas actif.");
        }

        Set<String> roles = utilisateur.getRoles()
                .stream()
                .map(Enum::name)
                .collect(Collectors.toSet());

        return new LoginResponse(
                jwtService.genererToken(utilisateur),
                "Bearer",
                expiration / 1000,
                utilisateur.getId(),
                utilisateur.getNom(),
                utilisateur.getPrenom(),
                utilisateur.getEmail(),
                roles
        );
    }

}
