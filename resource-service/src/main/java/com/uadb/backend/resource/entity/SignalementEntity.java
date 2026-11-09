package com.uadb.backend.resource.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "signalements")
public class SignalementEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ressource_id", nullable = false)
    private RessourceEntity ressource;

    @Column(nullable = false)
    private Long utilisateurId;

    @Column(nullable = false, length = 100)
    private String motif;

    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutSignalement statut = StatutSignalement.EN_ATTENTE;

    private LocalDateTime dateCreation = LocalDateTime.now();

    private LocalDateTime dateTraitement;

    public Long getId() { return id; }
    public RessourceEntity getRessource() { return ressource; }
    public Long getUtilisateurId() { return utilisateurId; }
    public String getMotif() { return motif; }
    public String getDescription() { return description; }
    public StatutSignalement getStatut() { return statut; }
    public LocalDateTime getDateCreation() { return dateCreation; }
    public LocalDateTime getDateTraitement() { return dateTraitement; }

    public void setRessource(RessourceEntity ressource) { this.ressource = ressource; }
    public void setUtilisateurId(Long utilisateurId) { this.utilisateurId = utilisateurId; }
    public void setMotif(String motif) { this.motif = motif; }
    public void setDescription(String description) { this.description = description; }
    public void setStatut(StatutSignalement statut) { this.statut = statut; }
    public void setDateTraitement(LocalDateTime dateTraitement) { this.dateTraitement = dateTraitement; }
}
