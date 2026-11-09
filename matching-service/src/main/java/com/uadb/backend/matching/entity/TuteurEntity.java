package com.uadb.backend.matching.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;

@Entity
@Table(name = "matching_tuteurs",
    uniqueConstraints = @UniqueConstraint(columnNames = "utilisateur_id"))
public class TuteurEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "utilisateur_id", nullable = false, unique = true)
    private Long utilisateurId;

    @Column(nullable = false, length = 2000)
    private String description;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "matching_tuteur_specialites",
        joinColumns = @JoinColumn(name = "tuteur_id"))
    @Column(name = "specialite", nullable = false)
    private List<String> specialites = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "matching_tuteur_cours",
        joinColumns = @JoinColumn(name = "tuteur_id"))
    @Column(name = "cours_id", nullable = false)
    private List<Long> coursIds = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutProfilTuteur statut = StatutProfilTuteur.ACTIF;

    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUtilisateurId() { return utilisateurId; }
    public void setUtilisateurId(Long utilisateurId) { this.utilisateurId = utilisateurId; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public List<String> getSpecialites() { return specialites; }
    public void setSpecialites(List<String> specialites) {
        this.specialites = specialites == null ? new ArrayList<>() : new ArrayList<>(specialites);
    }
    public List<Long> getCoursIds() { return coursIds; }
    public void setCoursIds(List<Long> coursIds) {
        this.coursIds = coursIds == null ? new ArrayList<>() : new ArrayList<>(coursIds);
    }
    public StatutProfilTuteur getStatut() { return statut; }
    public void setStatut(StatutProfilTuteur statut) { this.statut = statut; }
    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }
}
