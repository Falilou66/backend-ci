package com.uadb.backend.academic.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Table(name = "filieres")
public class FiliereEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, unique = true, length = 100)
    private String nom;

    @Size(max = 500)
    private String description;

    @ManyToOne
    @JoinColumn(name = "ufr_id")
    private UfrEntity ufr;

    private LocalDateTime dateCreation = LocalDateTime.now();

    public Long getId() { return id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public UfrEntity getUfr() { return ufr; }
    public void setUfr(UfrEntity ufr) { this.ufr = ufr; }
    public LocalDateTime getDateCreation() { return dateCreation; }
}
