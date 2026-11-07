package com.uadb.backend.academic.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "cours")
public class CoursEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nom;

    @Column(length = 500)
    private String description;

    private Integer volumeHoraire;

    @ManyToOne(optional = false)
    @JoinColumn(name = "filiere_id", nullable = false)
    private FiliereEntity filiere;

    @ManyToOne(optional = false)
    @JoinColumn(name = "niveau_id", nullable = false)
    private NiveauEntity niveau;

    public Long getId() { return id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Integer getVolumeHoraire() { return volumeHoraire; }
    public void setVolumeHoraire(Integer volumeHoraire) { this.volumeHoraire = volumeHoraire; }
    public FiliereEntity getFiliere() { return filiere; }
    public void setFiliere(FiliereEntity filiere) { this.filiere = filiere; }
    public NiveauEntity getNiveau() { return niveau; }
    public void setNiveau(NiveauEntity niveau) { this.niveau = niveau; }
}
