package com.uadb.backend.identity.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import com.uadb.backend.identity.dto.ErreurResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;

/**
 * Vérifie que les erreurs du client ne sont plus renvoyées comme des erreurs serveur (500).
 */
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void accesRefuseRenvoie403() {
        ResponseEntity<ErreurResponse> reponse =
                handler.handleAccesRefuse(new AuthorizationDeniedException("Access Denied"));

        assertEquals(403, reponse.getStatusCode().value());
        assertEquals(403, reponse.getBody().status());
    }

    @Test
    void jsonInvalideRenvoie400() {
        ResponseEntity<ErreurResponse> reponse = handler.handleMessageIllisible(
                new HttpMessageNotReadableException("JSON invalide", mock(HttpInputMessage.class)));

        assertEquals(400, reponse.getStatusCode().value());
    }

    @Test
    void exceptionSpringConserveSonCodeHttp() {
        ResponseEntity<ErreurResponse> reponse =
                handler.handleGeneral(new HttpRequestMethodNotSupportedException("GET"));

        assertEquals(405, reponse.getStatusCode().value());
        assertEquals(405, reponse.getBody().status());
    }

    @Test
    void erreurInattendueRenvoieToujours500() {
        ResponseEntity<ErreurResponse> reponse =
                handler.handleGeneral(new IllegalStateException("panne"));

        assertEquals(500, reponse.getStatusCode().value());
    }
}
