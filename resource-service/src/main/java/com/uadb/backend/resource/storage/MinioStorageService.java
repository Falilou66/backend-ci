package com.uadb.backend.resource.storage;

import io.minio.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

@Service
public class MinioStorageService {

    private final MinioClient minioClient;
    private final String bucket;
    private final long maxFileSize;

    public MinioStorageService(
            MinioClient minioClient,
            @Value("${minio.bucket}") String bucket,
            @Value("${minio.max-file-size:20971520}") long maxFileSize) {
        this.minioClient = minioClient;
        this.bucket = bucket;
        this.maxFileSize = maxFileSize;
    }

    public FichierStocke stocker(MultipartFile fichier) {
        try {
            if (fichier == null || fichier.isEmpty()) {
                throw new IllegalArgumentException("Le fichier est vide");
            }

            if (fichier.getSize() > maxFileSize) {
                throw new IllegalArgumentException(
                        "La taille maximale autorisée est de 20 Mo");
            }

            String nomOriginal = fichier.getOriginalFilename();
            if (nomOriginal == null || nomOriginal.isBlank()) {
                nomOriginal = "fichier";
            }

            nomOriginal = nomOriginal.replaceAll("[\\\\/]", "_");

            String extension = "";
            int point = nomOriginal.lastIndexOf('.');
            if (point >= 0) {
                extension = nomOriginal.substring(point).toLowerCase();
            }

            String objectKey = "ressources/" + UUID.randomUUID() + extension;

            String contentType = fichier.getContentType();
            if (contentType == null || contentType.isBlank()) {
                contentType = "application/octet-stream";
            }

            boolean bucketExiste = minioClient.bucketExists(
                    BucketExistsArgs.builder().bucket(bucket).build());

            if (!bucketExiste) {
                minioClient.makeBucket(
                        MakeBucketArgs.builder().bucket(bucket).build());
            }

            try (InputStream input = fichier.getInputStream()) {
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(bucket)
                                .object(objectKey)
                                .stream(input, fichier.getSize(), -1L)
                                .contentType(contentType)
                                .build());
            }

            return new FichierStocke(
                    objectKey,
                    nomOriginal,
                    contentType,
                    fichier.getSize());

        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Impossible de stocker le fichier dans MinIO", e);
        }
    }

    public FichierTelecharge telecharger(String objectKey, String nomOriginal,
                                         String contentType) {
        try (InputStream input = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucket)
                        .object(objectKey)
                        .build())) {

            return new FichierTelecharge(
                    input.readAllBytes(),
                    nomOriginal,
                    contentType);

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Impossible de récupérer le fichier depuis MinIO", e);
        }
    }

    public void supprimer(String objectKey) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectKey)
                            .build());
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Impossible de supprimer le fichier de MinIO", e);
        }
    }
}
