package com.uadb.backend.identity.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.uadb.backend.identity.dto.CreerAdministrateurRequest;
import com.uadb.backend.identity.entity.AdministrateurEntity;
import com.uadb.backend.identity.entity.UtilisateurEntity;
import com.uadb.backend.identity.enums.Role;
import com.uadb.backend.identity.repository.AdministrateurRepository;
import com.uadb.backend.identity.repository.UtilisateurRepository;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AdministrationServiceTest {

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @Mock
    private AdministrateurRepository administrateurRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AdministrationService administrationService;

    @Test
    void creerAdministrateur_NormaliseEmailEtCreeLeProfil() {
        CreerAdministrateurRequest request = new CreerAdministrateurRequest(
                " Admin ",
                " Principal ",
                "Admin.Externe@example.com",
                "StrongPass123",
                null
        );
        when(utilisateurRepository.existsByEmail("admin.externe@example.com"))
                .thenReturn(false);
        when(passwordEncoder.encode(request.motDePasse()))
                .thenReturn("encodedPassword");
        when(utilisateurRepository.save(any(UtilisateurEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(administrateurRepository.save(any(AdministrateurEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var response = administrationService.creerAdministrateur(request);

        assertEquals("admin.externe@example.com", response.email());
        assertEquals(Set.of("ADMINISTRATEUR"), response.roles());
        assertEquals("ACTIF", response.statut());
        verify(administrateurRepository).save(any(AdministrateurEntity.class));
    }

    @Test
    void creerAdministrateur_RefuseUnEmailDejaUtilise() {
        CreerAdministrateurRequest request = new CreerAdministrateurRequest(
                "Admin",
                "Existant",
                "admin@uadb.edu.sn",
                "StrongPass123",
                null
        );
        when(utilisateurRepository.existsByEmail(request.email()))
                .thenReturn(true);

        assertThrows(
                IllegalArgumentException.class,
                () -> administrationService.creerAdministrateur(request)
        );
    }
}
