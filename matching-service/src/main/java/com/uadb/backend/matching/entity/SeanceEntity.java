package com.uadb.backend.matching.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import jakarta.persistence.*;

@Entity
@Table(name = "matching_seances", indexes = {
    @Index(name = "idx_seance_tuteur_date", columnList = "tuteur_id,date_seance"),
    @Index(name = "idx_seance_etudiant", columnList = "etudiant_id")
})
public class SeanceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "etudiant_id", nullable = false)
    private Long etudiantId;

    @Column(name = "tuteur_id", nullable = false)
    private Long tuteurId;

    @Column(name = "cours_id", nullable = false)
    private Long coursId;

    @Column(name = "date_seance", nullable = false)
    private LocalDate dateSeance;

    @Column(name = "heure_debut", nullable = false)
    private LocalTime heureDebut;

    @Column(name = "heure_fin", nullable = false)
    private LocalTime heureFin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ModeSeance mode;

    @Column(length = 500)
    private String lieu;

    @Column(length = 2000)
    private String motif;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutSeance statut = StatutSeance.EN_ATTENTE;

    @Column(name = "motif_decision", length = 1000)
    private String motifDecision;

    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation;

    @Column(name = "date_decision")
    private LocalDateTime dateDecision;

    @PrePersist
    void prePersist() {
        if (dateCreation == null) dateCreation = LocalDateTime.now();
        if (statut == null) statut = StatutSeance.EN_ATTENTE;
    }

    public Long getId() { return id; }
    public Long getEtudiantId() { return etudiantId; }
    public void setEtudiantId(Long value) { this.etudiantId = value; }
    public Long getTuteurId() { return tuteurId; }
    public void setTuteurId(Long value) { this.tuteurId = value; }
    public Long getCoursId() { return coursId; }
    public void setCoursId(Long value) { this.coursId = value; }
    public LocalDate getDateSeance() { return dateSeance; }
    public void setDateSeance(LocalDate value) { this.dateSeance = value; }
    public LocalTime getHeureDebut() { return heureDebut; }
    public void setHeureDebut(LocalTime value) { this.heureDebut = value; }
    public LocalTime getHeureFin() { return heureFin; }
    public void setHeureFin(LocalTime value) { this.heureFin = value; }
    public ModeSeance getMode() { return mode; }
    public void setMode(ModeSeance value) { this.mode = value; }
    public String getLieu() { return lieu; }
    public void setLieu(String value) { this.lieu = value; }
    public String getMotif() { return motif; }
    public void setMotif(String value) { this.motif = value; }
    public StatutSeance getStatut() { return statut; }
    public void setStatut(StatutSeance value) { this.statut = value; }
    public String getMotifDecision() { return motifDecision; }
    public void setMotifDecision(String value) { this.motifDecision = value; }
    public LocalDateTime getDateCreation() { return dateCreation; }
    public LocalDateTime getDateDecision() { return dateDecision; }
    public void setDateDecision(LocalDateTime value) { this.dateDecision = value; }
}
