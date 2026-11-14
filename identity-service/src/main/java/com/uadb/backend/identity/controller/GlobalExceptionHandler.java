package com.uadb.backend.identity.controller;

import com.uadb.backend.identity.dto.ErreurResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErreurResponse> handleIllegalArgument(
            IllegalArgumentException exception) {

        ErreurResponse erreur = new ErreurResponse(
                Instant.now(),
                400,
                "Bad Request",
                exception.getMessage()
        );

        return ResponseEntity.badRequest().body(erreur);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErreurResponse> handleValidation(
            MethodArgumentNotValidException exception) {

        String message = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + " : " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));

        ErreurResponse erreur = new ErreurResponse(
                Instant.now(),
                400,
                "Validation Failed",
                message
        );

        return ResponseEntity.badRequest().body(erreur);
    }

    // Corps JSON absent ou mal formé : erreur du client (400), pas du serveur
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErreurResponse> handleMessageIllisible(
            HttpMessageNotReadableException exception) {

        ErreurResponse erreur = new ErreurResponse(
                Instant.now(),
                400,
                "Bad Request",
                "Le corps de la requête est absent ou n'est pas un JSON valide."
        );

        return ResponseEntity.badRequest().body(erreur);
    }

    // Rôle insuffisant (@PreAuthorize, hasRole...) : 403 et non 500
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErreurResponse> handleAccesRefuse(
            AccessDeniedException exception) {

        ErreurResponse erreur = new ErreurResponse(
                Instant.now(),
                403,
                "Forbidden",
                "Vous n'avez pas les droits nécessaires pour cette action."
        );

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(erreur);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErreurResponse> handleGeneral(
            Exception exception) {

        // Exceptions Spring MVC qui portent déjà leur code HTTP
        // (route inexistante 404, méthode non supportée 405, type de contenu 415...) : on le conserve
        if (exception instanceof ErrorResponse reponseSpring) {
            HttpStatusCode statut = reponseSpring.getStatusCode();
            HttpStatus statutConnu = HttpStatus.resolve(statut.value());

            ErreurResponse erreur = new ErreurResponse(
                    Instant.now(),
                    statut.value(),
                    statutConnu != null ? statutConnu.getReasonPhrase() : "Erreur",
                    statut.value() == 404
                            ? "La ressource demandée n'existe pas."
                            : exception.getMessage()
            );

            return ResponseEntity.status(statut).body(erreur);
        }

        logger.error("Erreur interne lors du traitement de la requête.", exception);

        ErreurResponse erreur = new ErreurResponse(
                Instant.now(),
                500,
                "Internal Server Error",
                "Une erreur interne est survenue."
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(erreur);
    }

    @ExceptionHandler(BadCredentialsException.class)
public ResponseEntity<ErreurResponse> handleBadCredentials(
        BadCredentialsException exception) {

    ErreurResponse erreur = new ErreurResponse(
            Instant.now(),
            401,
            "Unauthorized",
            "Email ou mot de passe incorrect."
    );

    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(erreur);
}

@ExceptionHandler(DisabledException.class)
public ResponseEntity<ErreurResponse> handleDisabled(
        DisabledException exception) {

    ErreurResponse erreur = new ErreurResponse(
            Instant.now(),
            403,
            "Forbidden",
            exception.getMessage()
    );

    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(erreur);
}
}