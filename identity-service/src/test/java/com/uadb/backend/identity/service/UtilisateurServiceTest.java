package com.uadb.backend.identity.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.uadb.backend.identity.dto.InscriptionRequest;
import com.uadb.backend.identity.entity.UtilisateurEntity;
import com.uadb.backend.identity.enums.Role;
import com.uadb.backend.identity.enums.StatutUtilisateur;
import com.uadb.backend.identity.repository.EnseignantRepository;
import com.uadb.backend.identity.repository.EtudiantRepository;
import com.uadb.backend.identity.repository.UtilisateurRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UtilisateurServiceTest {

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @Mock
    private EtudiantRepository etudiantRepository;

    @Mock
    private EnseignantRepository enseignantRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UtilisateurService utilisateurService;

    private InscriptionRequest request;

    @BeforeEach
    void setUp() {
        request = new InscriptionRequest(
                "Jean",
                "Dupont",
                " Jean.Dupont@UADB.EDU.SN ",
                "MotDePasse123!",
                "0123456789",
                "CODE123",
                2L,
                3L,
                Role.ETUDIANT,
                null
        );
    }

    @Test
    void inscrireEtudiant_CreeUnCompteActifAvecProfil() {
        when(utilisateurRepository.existsByEmail("jean.dupont@uadb.edu.sn"))
                .thenReturn(false);
        when(etudiantRepository.existsByCodePermanent("CODE123"))
                .thenReturn(false);
        when(passwordEncoder.encode("MotDePasse123!"))
                .thenReturn("encodedPassword");
        when(utilisateurRepository.save(any(UtilisateurEntity.class)))
                .thenAnswer(invocation -> {
                    return invocation.getArgument(0);
                });

        var response = utilisateurService.inscrire(request);

        assertEquals("jean.dupont@uadb.edu.sn", response.email());
        assertEquals(Role.ETUDIANT, response.roles().iterator().next());
        assertEquals(StatutUtilisateur.ACTIF, response.statut());
        verify(etudiantRepository).save(any());
    }

    @Test
    void inscrireEnseignant_CreeUnCompteAvecProfil() {
        InscriptionRequest teacherRequest = new InscriptionRequest(
                "Diop",
                "Awa",
                "awa.diop@uadb.edu.sn",
                "MotDePasse123!",
                null,
                null,
                null,
                null,
                Role.ENSEIGNANT,
                " ENS-42 "
        );
        when(utilisateurRepository.existsByEmail(teacherRequest.email()))
                .thenReturn(false);
        when(enseignantRepository.findByMatriculeProfessionnel("ENS-42"))
                .thenReturn(Optional.empty());
        when(passwordEncoder.encode(teacherRequest.motDePasse()))
                .thenReturn("encodedPassword");
        when(utilisateurRepository.save(any(UtilisateurEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var response = utilisateurService.inscrire(teacherRequest);

        assertNotNull(response);
        assertEquals(Role.ENSEIGNANT, response.roles().iterator().next());
        verify(enseignantRepository).save(any());
    }

    @Test
    void inscrire_RefuseUnEmailHorsDomaineInstitutionnel() {
        InscriptionRequest invalidRequest = new InscriptionRequest(
                "Jean", "Dupont", "jean@example.com", "MotDePasse123!",
                null, "CODE123", null, null, Role.ETUDIANT, null
        );

        var exception = assertThrows(
                IllegalArgumentException.class,
                () -> utilisateurService.inscrire(invalidRequest)
        );

        assertEquals(
                "L'adresse email doit se terminer par @uadb.edu.sn",
                exception.getMessage()
        );
    }

    @Test
    void inscrire_RefuseLaCreationPubliqueDAdministrateur() {
        InscriptionRequest adminRequest = new InscriptionRequest(
                "Admin", "Test", "admin@uadb.edu.sn", "MotDePasse123!",
                null, null, null, null, Role.ADMINISTRATEUR, null
        );
        when(utilisateurRepository.existsByEmail(adminRequest.email()))
                .thenReturn(false);

        assertThrows(
                IllegalArgumentException.class,
                () -> utilisateurService.inscrire(adminRequest)
        );
    }

    @Test
    void inscrire_RefuseUnCodePermanentManquantPourEtudiant() {
        InscriptionRequest invalidRequest = new InscriptionRequest(
                "Jean", "Dupont", "jean@uadb.edu.sn", "MotDePasse123!",
                null, null, null, null, Role.ETUDIANT, null
        );
        when(utilisateurRepository.existsByEmail(invalidRequest.email()))
                .thenReturn(false);

        assertThrows(
                IllegalArgumentException.class,
                () -> utilisateurService.inscrire(invalidRequest)
        );
    }
}
