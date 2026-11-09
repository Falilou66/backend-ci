package com.uadb.backend.resource.storage;

public record FichierStocke(
        String objectKey,
        String nomOriginal,
        String contentType,
        long taille
) {}
