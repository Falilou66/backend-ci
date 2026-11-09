package com.uadb.backend.resource.service;

import com.uadb.backend.resource.client.AcademicClient;
import com.uadb.backend.resource.client.IdentityClient;
import com.uadb.backend.resource.client.NotificationClient;
import com.uadb.backend.resource.dto.RessourceRequest;
import com.uadb.backend.resource.entity.RessourceEntity;
import com.uadb.backend.resource.enums.StatutPublication;
import com.uadb.backend.resource.repository.RessourceRepository;
import com.uadb.backend.resource.storage.FichierStocke;
import com.uadb.backend.resource.storage.FichierTelecharge;
import com.uadb.backend.resource.storage.MinioStorageService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RessourceService {

    private final RessourceRepository repository;
    private final MinioStorageService stockage;
    private final AcademicClient academicClient;
    private final IdentityClient identityClient;
    private final NotificationClient notificationClient;

    public RessourceService(
            RessourceRepository repository,
            MinioStorageService stockage,
            AcademicClient academicClient,
            IdentityClient identityClient,
            NotificationClient notificationClient) {
        this.repository = repository;
        this.stockage = stockage;
        this.academicClient = academicClient;
        this.identityClient = identityClient;
        this.notificationClient = notificationClient;
    }

    @Transactional
    public RessourceEntity creer(RessourceRequest request) {
        RessourceEntity r = new RessourceEntity();
        remplir(r, request);
        r.setStatutPublication(StatutPublication.EN_ATTENTE);
        return repository.save(r);
    }

    @Transactional
    public RessourceEntity publier(String titre, String description, String type,
                                   Long coursId, Long auteurId, MultipartFile fichier) {
        FichierStocke stocke = stockage.stocker(fichier);

        try {
            RessourceEntity r = new RessourceEntity();
            r.setTitre(titre);
            r.setDescription(description);
            r.setType(type);
            r.setCoursId(coursId);
            r.setAuteurId(auteurId);
            r.setUrl(stocke.objectKey());
            r.setNomFichier(stocke.nomOriginal());
            r.setContentType(stocke.contentType());
            r.setTaille(stocke.taille());
            r.setStatutPublication(StatutPublication.EN_ATTENTE);
            return repository.save(r);
        } catch (RuntimeException e) {
            stockage.supprimer(stocke.objectKey());
            throw e;
        }
    }

    @Transactional
    public RessourceEntity valider(Long id, Long validateurId) {
        RessourceEntity r = obtenir(id);

        if (r.getStatutPublication() != StatutPublication.EN_ATTENTE) {
            throw new IllegalStateException("Cette ressource n'est plus en attente de validation");
        }

        r.setStatutPublication(StatutPublication.PUBLIEE);
        r.setValidateurId(validateurId);
        r.setDateValidation(LocalDateTime.now());
        r.setMotifRejet(null);

        RessourceEntity sauvegardee = repository.save(r);

        notifierEtudiants(sauvegardee);

        notificationClient.notifier(
                r.getAuteurId(),
                "RESSOURCE_VALIDEE",
                "Ressource validée",
                "Votre ressource « " + r.getTitre() + " » a été validée et publiée.",
                "/ressources/" + r.getId()
        );

        return sauvegardee;
    }

    @Transactional
    public RessourceEntity rejeter(Long id, Long validateurId, String motif) {
        if (motif == null || motif.isBlank()) {
            throw new IllegalArgumentException("Le motif du rejet est obligatoire");
        }

        RessourceEntity r = obtenir(id);

        if (r.getStatutPublication() != StatutPublication.EN_ATTENTE) {
            throw new IllegalStateException("Cette ressource n'est plus en attente de validation");
        }

        r.setStatutPublication(StatutPublication.REJETEE);
        r.setValidateurId(validateurId);
        r.setDateValidation(LocalDateTime.now());
        r.setMotifRejet(motif.trim());

        RessourceEntity sauvegardee = repository.save(r);

        notificationClient.notifier(
                r.getAuteurId(),
                "RESSOURCE_REJETEE",
                "Ressource rejetée",
                "Votre ressource « " + r.getTitre() + " » a été rejetée. Motif : " + motif.trim(),
                "/mes-ressources"
        );

        return sauvegardee;
    }

    public List<RessourceEntity> listerEnAttente() {
        return repository.findByStatutPublicationOrderByDateCreationDesc(
                StatutPublication.EN_ATTENTE);
    }

    private void notifierEtudiants(RessourceEntity ressource) {
        if (ressource.getCoursId() == null) {
            return;
        }

        academicClient.obtenirCours(ressource.getCoursId()).ifPresent(cours -> {
            if (cours.filiereId() == null || cours.niveauId() == null) {
                return;
            }

            List<Long> etudiants = identityClient.trouverEtudiants(
                    cours.filiereId(), cours.niveauId());

            String nomCours = cours.nom() == null ? "votre cours" : cours.nom();
            String message = "Une nouvelle ressource est disponible pour le cours "
                    + nomCours + " : " + ressource.getTitre();

            for (Long etudiantId : etudiants) {
                notificationClient.notifier(
                        etudiantId,
                        "NOUVELLE_RESSOURCE",
                        "Nouvelle ressource disponible",
                        message,
                        "/ressources/" + ressource.getId()
                );
            }
        });
    }

    public Page<RessourceEntity> rechercher(Long coursId, Long auteurId, String type,
                                             String titre, int page, int size) {
        if (page < 0) {
            throw new IllegalArgumentException("Le numéro de page doit être positif");
        }
        if (size < 1 || size > 100) {
            throw new IllegalArgumentException("La taille doit être comprise entre 1 et 100");
        }

        String typeFiltre = (type == null || type.isBlank()) ? null : type.trim();
        String titreFiltre = (titre == null || titre.isBlank()) ? null : titre.trim();

        Pageable pageable = PageRequest.of(page, size);
        return repository.rechercher(
                coursId, auteurId, typeFiltre, titreFiltre,
                StatutPublication.PUBLIEE, pageable);
    }

    public List<RessourceEntity> lister() {
        return repository.findByStatutPublicationOrderByDateCreationDesc(
                StatutPublication.PUBLIEE);
    }

    public RessourceEntity obtenir(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ressource introuvable"));
    }

    public RessourceEntity obtenirPubliee(Long id) {
        RessourceEntity r = obtenir(id);
        if (r.getStatutPublication() != StatutPublication.PUBLIEE) {
            throw new IllegalArgumentException("Ressource introuvable");
        }
        return r;
    }

    public List<RessourceEntity> trouverParCours(Long coursId) {
        return repository.findByCoursIdAndStatutPublicationOrderByDateCreationDesc(
                coursId, StatutPublication.PUBLIEE);
    }

    @Transactional
    public FichierTelecharge telecharger(Long id) {
        RessourceEntity r = obtenirPubliee(id);

        if (r.getNomFichier() == null || r.getNomFichier().isBlank()) {
            throw new IllegalArgumentException("Cette ressource ne possède pas de fichier stocké");
        }

        FichierTelecharge fichier = stockage.telecharger(
                r.getUrl(), r.getNomFichier(), r.getContentType());

        long compteur = r.getTelechargements() == null ? 0L : r.getTelechargements();
        r.setTelechargements(compteur + 1);
        repository.save(r);

        return fichier;
    }

    @Transactional
    public RessourceEntity modifier(Long id, RessourceRequest request) {
        RessourceEntity r = obtenir(id);
        if (r.getStatutPublication() != StatutPublication.EN_ATTENTE) {
            throw new IllegalStateException("Seules les ressources en attente peuvent être modifiées");
        }
        remplir(r, request);
        return repository.save(r);
    }

    @Transactional
    public void supprimer(Long id) {
        RessourceEntity r = obtenir(id);

        if (r.getNomFichier() != null && !r.getNomFichier().isBlank()) {
            stockage.supprimer(r.getUrl());
        }
        repository.delete(r);
    }

    private void remplir(RessourceEntity r, RessourceRequest request) {
        r.setTitre(request.titre());
        r.setDescription(request.description());
        r.setType(request.type());
        r.setUrl(request.url());
        r.setCoursId(request.coursId());
        r.setAuteurId(request.auteurId());
    }
}
