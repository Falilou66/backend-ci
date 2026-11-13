package com.uadb.backend.identity.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.uadb.backend.identity.dto.LoginRequest;
import com.uadb.backend.identity.dto.LoginResponse;
import com.uadb.backend.identity.entity.UtilisateurEntity;
import com.uadb.backend.identity.enums.Role;
import com.uadb.backend.identity.enums.StatutUtilisateur;
import com.uadb.backend.identity.repository.UtilisateurRepository;
import com.uadb.backend.identity.security.JwtService;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthentificationServiceTest {

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthentificationService authentificationService;

    @Test
    void connecter_EmailNormalise_RetourneUnJwtAvecRole() {
        LoginRequest request = new LoginRequest(" Test@UADB.EDU.SN ", "password123");
        UtilisateurEntity utilisateur = utilisateur("test@uadb.edu.sn");
        when(utilisateurRepository.findByEmail("test@uadb.edu.sn"))
                .thenReturn(Optional.of(utilisateur));
        when(passwordEncoder.matches("password123", "encodedPassword"))
                .thenReturn(true);
        when(jwtService.genererToken(utilisateur)).thenReturn("jwt-token");

        LoginResponse response = authentificationService.connecter(request);

        assertNotNull(response.accessToken());
        assertEquals("Bearer", response.tokenType());
        assertEquals("test@uadb.edu.sn", response.email());
        assertEquals(Set.of("ETUDIANT"), response.roles());
        verify(utilisateurRepository).findByEmail("test@uadb.edu.sn");
    }

    @Test
    void connecter_MotDePasseIncorrect_RefuseLaConnexion() {
        LoginRequest request = new LoginRequest("test@uadb.edu.sn", "wrong");
        UtilisateurEntity utilisateur = utilisateur("test@uadb.edu.sn");
        when(utilisateurRepository.findByEmail(request.email()))
                .thenReturn(Optional.of(utilisateur));
        when(passwordEncoder.matches("wrong", "encodedPassword"))
                .thenReturn(false);

        assertThrows(
                BadCredentialsException.class,
                () -> authentificationService.connecter(request)
        );
    }

    @Test
    void connecter_CompteInactif_RefuseLaConnexion() {
        LoginRequest request = new LoginRequest("test@uadb.edu.sn", "password123");
        UtilisateurEntity utilisateur = utilisateur("test@uadb.edu.sn");
        utilisateur.setStatut(StatutUtilisateur.INACTIF);
        when(utilisateurRepository.findByEmail(request.email()))
                .thenReturn(Optional.of(utilisateur));
        when(passwordEncoder.matches("password123", "encodedPassword"))
                .thenReturn(true);

        assertThrows(
                DisabledException.class,
                () -> authentificationService.connecter(request)
        );
    }

    private UtilisateurEntity utilisateur(String email) {
        UtilisateurEntity utilisateur = new UtilisateurEntity();
        utilisateur.setEmail(email);
        utilisateur.setMotDePasse("encodedPassword");
        utilisateur.setStatut(StatutUtilisateur.ACTIF);
        utilisateur.setRoles(Set.of(Role.ETUDIANT));
        return utilisateur;
    }
}
