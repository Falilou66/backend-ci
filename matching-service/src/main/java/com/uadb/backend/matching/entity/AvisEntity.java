package com.uadb.backend.matching.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(
    name = "matching_avis",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_avis_seance",
        columnNames = "seance_id"
    ),
    indexes = @Index(name = "idx_avis_tuteur", columnList = "tuteur_id")
)
public class AvisEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "seance_id", nullable = false)
    private Long seanceId;

    @Column(name = "tuteur_id", nullable = false)
    private Long tuteurId;

    @Column(name = "auteur_id", nullable = false)
    private Long auteurId;

    @Column(nullable = false)
    private Integer note;

    @Column(length = 2000)
    private String commentaire;

    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation;

    @PrePersist
    void prePersist() {
        if (dateCreation == null) dateCreation = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public Long getSeanceId() { return seanceId; }
    public void setSeanceId(Long value) { this.seanceId = value; }
    public Long getTuteurId() { return tuteurId; }
    public void setTuteurId(Long value) { this.tuteurId = value; }
    public Long getAuteurId() { return auteurId; }
    public void setAuteurId(Long value) { this.auteurId = value; }
    public Integer getNote() { return note; }
    public void setNote(Integer value) { this.note = value; }
    public String getCommentaire() { return commentaire; }
    public void setCommentaire(String value) { this.commentaire = value; }
    public LocalDateTime getDateCreation() { return dateCreation; }
}
