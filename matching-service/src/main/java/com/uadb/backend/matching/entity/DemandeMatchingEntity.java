package com.uadb.backend.matching.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "demandes_matching")
public class DemandeMatchingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long etudiantId;

    @Column(nullable = false)
    private Long tuteurId;

    @Column(nullable = false)
    private Long coursId;

    @Column(length = 500)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutDemande statut = StatutDemande.EN_ATTENTE;

    @Column(nullable = false)
    private LocalDateTime dateCreation = LocalDateTime.now();

    public Long getId() { return id; }
    public Long getEtudiantId() { return etudiantId; }
    public void setEtudiantId(Long etudiantId) { this.etudiantId = etudiantId; }
    public Long getTuteurId() { return tuteurId; }
    public void setTuteurId(Long tuteurId) { this.tuteurId = tuteurId; }
    public Long getCoursId() { return coursId; }
    public void setCoursId(Long coursId) { this.coursId = coursId; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public StatutDemande getStatut() { return statut; }
    public void setStatut(StatutDemande statut) { this.statut = statut; }
    public LocalDateTime getDateCreation() { return dateCreation; }
}
