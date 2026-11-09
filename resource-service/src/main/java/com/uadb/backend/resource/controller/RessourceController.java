package com.uadb.backend.resource.controller;

import com.uadb.backend.resource.dto.RessourceRequest;
import com.uadb.backend.resource.entity.RessourceEntity;
import com.uadb.backend.resource.service.RessourceService;
import com.uadb.backend.resource.storage.FichierTelecharge;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/v1/ressources")
public class RessourceController {

    private final RessourceService service;

    public RessourceController(RessourceService service) {
        this.service = service;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RessourceEntity> creer(
            @Valid @RequestBody RessourceRequest request) {
        return ResponseEntity.status(201).body(service.creer(request));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<RessourceEntity> publier(
            @RequestParam String titre,
            @RequestParam(required = false) String description,
            @RequestParam String type,
            @RequestParam Long coursId,
            @RequestParam Long auteurId,
            @RequestParam("fichier") MultipartFile fichier) {
        return ResponseEntity.status(201).body(
                service.publier(titre, description, type, coursId, auteurId, fichier));
    }

    @GetMapping
    public Page<RessourceEntity> rechercher(
            @RequestParam(required = false) Long coursId,
            @RequestParam(required = false) Long auteurId,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String titre,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return service.rechercher(coursId, auteurId, type, titre, page, size);
    }

    @GetMapping("/{id}")
    public RessourceEntity obtenir(@PathVariable Long id) {
        return service.obtenirPubliee(id);
    }

    @GetMapping("/cours/{coursId}")
    public List<RessourceEntity> parCours(@PathVariable Long coursId) {
        return service.trouverParCours(coursId);
    }

    @GetMapping("/{id}/fichier")
    public ResponseEntity<byte[]> telecharger(@PathVariable Long id) {
        FichierTelecharge fichier = service.telecharger(id);
        ContentDisposition disposition = ContentDisposition.attachment()
                .filename(fichier.nomOriginal(), StandardCharsets.UTF_8)
                .build();

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(fichier.contentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
                .body(fichier.contenu());
    }

    @GetMapping("/admin/en-attente")
    public List<RessourceEntity> listerEnAttente() {
        return service.listerEnAttente();
    }

    @PutMapping("/admin/{id}/valider")
    public RessourceEntity valider(
            @PathVariable Long id,
            @RequestParam Long validateurId) {
        return service.valider(id, validateurId);
    }

    @PutMapping("/admin/{id}/rejeter")
    public RessourceEntity rejeter(
            @PathVariable Long id,
            @RequestParam Long validateurId,
            @RequestParam String motif) {
        return service.rejeter(id, validateurId, motif);
    }

    @PutMapping("/{id}")
    public RessourceEntity modifier(@PathVariable Long id,
                                    @Valid @RequestBody RessourceRequest request) {
        return service.modifier(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        service.supprimer(id);
        return ResponseEntity.noContent().build();
    }
}
