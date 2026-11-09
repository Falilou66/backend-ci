package com.uadb.backend.resource.entity;

import com.uadb.backend.resource.enums.StatutPublication;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ressources")
public class RessourceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String titre;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false, length = 30)
    private String type;

    @Column(nullable = false, length = 1000)
    private String url;

    @Column(nullable = false)
    private Long coursId;

    @Column(nullable = false)
    private Long auteurId;

    @Column(length = 255)
    private String nomFichier;

    @Column(length = 150)
    private String contentType;

    private Long taille;

    @Column(nullable = false)
    private Long telechargements = 0L;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut_publication", nullable = false, length = 20)
    private StatutPublication statutPublication = StatutPublication.EN_ATTENTE;

    @Column(length = 1000)
    private String motifRejet;

    private Long validateurId;

    private LocalDateTime dateValidation;

    private LocalDateTime dateCreation = LocalDateTime.now();

    @PrePersist
    public void initialiser() {
        if (dateCreation == null) {
            dateCreation = LocalDateTime.now();
        }
        if (statutPublication == null) {
            statutPublication = StatutPublication.EN_ATTENTE;
        }
        if (telechargements == null) {
            telechargements = 0L;
        }
    }

    public Long getId() { return id; }
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public Long getCoursId() { return coursId; }
    public void setCoursId(Long coursId) { this.coursId = coursId; }
    public Long getAuteurId() { return auteurId; }
    public void setAuteurId(Long auteurId) { this.auteurId = auteurId; }
    public String getNomFichier() { return nomFichier; }
    public void setNomFichier(String nomFichier) { this.nomFichier = nomFichier; }
    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }
    public Long getTaille() { return taille; }
    public void setTaille(Long taille) { this.taille = taille; }
    public Long getTelechargements() { return telechargements; }
    public void setTelechargements(Long telechargements) { this.telechargements = telechargements; }
    public StatutPublication getStatutPublication() { return statutPublication; }
    public void setStatutPublication(StatutPublication statutPublication) { this.statutPublication = statutPublication; }
    public String getMotifRejet() { return motifRejet; }
    public void setMotifRejet(String motifRejet) { this.motifRejet = motifRejet; }
    public Long getValidateurId() { return validateurId; }
    public void setValidateurId(Long validateurId) { this.validateurId = validateurId; }
    public LocalDateTime getDateValidation() { return dateValidation; }
    public void setDateValidation(LocalDateTime dateValidation) { this.dateValidation = dateValidation; }
    public LocalDateTime getDateCreation() { return dateCreation; }
}
