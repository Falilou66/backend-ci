package com.uadb.backend.identity.dto;

import java.time.Instant;

public record ErreurResponse(
        Instant timestamp,
        int status,
        String erreur,
        String message
) {}