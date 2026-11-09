package com.uadb.backend.notification.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
public class NotificationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long utilisateurId;

    @Column(nullable = false, length = 50)
    private String type;

    @Column(nullable = false, length = 150)
    private String titre;

    @Column(nullable = false, length = 1000)
    private String message;

    @Column(length = 500)
    private String lien;

    @Column(nullable = false)
    private boolean lu = false;

    @Column(nullable = false)
    private LocalDateTime dateCreation = LocalDateTime.now();

    public Long getId() { return id; }
    public Long getUtilisateurId() { return utilisateurId; }
    public String getType() { return type; }
    public String getTitre() { return titre; }
    public String getMessage() { return message; }
    public String getLien() { return lien; }
    public boolean isLu() { return lu; }
    public LocalDateTime getDateCreation() { return dateCreation; }

    public void setUtilisateurId(Long utilisateurId) { this.utilisateurId = utilisateurId; }
    public void setType(String type) { this.type = type; }
    public void setTitre(String titre) { this.titre = titre; }
    public void setMessage(String message) { this.message = message; }
    public void setLien(String lien) { this.lien = lien; }
    public void setLu(boolean lu) { this.lu = lu; }
}
