package com.uadb.backend.identity.controller;

import com.uadb.backend.identity.dto.ErreurResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;

import java.time.Instant;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

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

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErreurResponse> handleGeneral(
            Exception exception) {

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