package com.uadb.backend.resource.storage;

public record FichierTelecharge(
        byte[] contenu,
        String nomOriginal,
        String contentType
) {}
