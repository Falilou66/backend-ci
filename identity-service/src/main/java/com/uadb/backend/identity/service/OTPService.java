package com.uadb.backend.identity.service;

import org.springframework.stereotype.Service;

@Service
public class OTPService {

    private static final long EXPIRATION_MINUTES = 15;

    public String genererCodeOTP() {
        return String.format("%06d", System.currentTimeMillis() % 1000000);
    }

    public boolean validerCodeOTP(String codeSaisi) {
        if (codeSaisi == null || codeSaisi.isEmpty()) {
            return false;
        }
        // TODO: In version 2, vérifier en base avec expiration
        // Pour l'instant, acception simple
        return codeSaisi.length() == 6;
    }

    public long getExpirationMinutes() {
        return EXPIRATION_MINUTES;
    }
}