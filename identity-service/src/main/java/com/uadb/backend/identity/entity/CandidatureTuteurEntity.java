package com.uadb.backend.identity.entity;

import com.uadb.backend.identity.enums.StatutCandidatureTuteur;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "candidatures_tuteurs")
public class CandidatureTuteurEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long utilisateurId;

    @Column(nullable = false, length = 2000)
    private String description;

    @Column(nullable = false, length = 2000)
    private String motivation;

    @ElementCollection
    @CollectionTable(
        name = "candidature_tuteur_specialites",
        joinColumns = @JoinColumn(name = "candidature_id")
    )
    @Column(name = "specialite", nullable = false, length = 150)
    private List<String> specialites = new ArrayList<>();

    @ElementCollection
    @CollectionTable(
        name = "candidature_tuteur_cours",
        joinColumns = @JoinColumn(name = "candidature_id")
    )
    @Column(name = "cours_id", nullable = false)
    private List<Long> coursIds = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutCandidatureTuteur statut = StatutCandidatureTuteur.EN_ATTENTE;

    @Column(length = 1000)
    private String motifRefus;

    private LocalDateTime dateCreation;
    private LocalDateTime dateDecision;

    @PrePersist
    void creerDate() {
        dateCreation = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public Long getUtilisateurId() { return utilisateurId; }
    public void setUtilisateurId(Long id) { this.utilisateurId = id; }
    public String getDescription() { return description; }
    public void setDescription(String value) { this.description = value; }
    public String getMotivation() { return motivation; }
    public void setMotivation(String value) { this.motivation = value; }
    public List<String> getSpecialites() { return specialites; }
    public void setSpecialites(List<String> value) { this.specialites = value; }
    public List<Long> getCoursIds() { return coursIds; }
    public void setCoursIds(List<Long> value) {
        this.coursIds = value == null ? new ArrayList<>() : new ArrayList<>(value);
    }
    public StatutCandidatureTuteur getStatut() { return statut; }
    public void setStatut(StatutCandidatureTuteur value) { this.statut = value; }
    public String getMotifRefus() { return motifRefus; }
    public void setMotifRefus(String value) { this.motifRefus = value; }
    public LocalDateTime getDateCreation() { return dateCreation; }
    public LocalDateTime getDateDecision() { return dateDecision; }
    public void setDateDecision(LocalDateTime value) { this.dateDecision = value; }
}
