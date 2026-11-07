
package com.uadb.backend.identity.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "profils_administrateurs")
public class AdministrateurEntity {

    @Id
    private Long id;

    @OneToOne(optional = false)
    @MapsId
    @JoinColumn(name = "utilisateur_id")
    private UtilisateurEntity utilisateur;

    @Column(length = 100)
    private String fonction;

    public AdministrateurEntity() {
    }

    public Long getId() {
        return id;
    }

    public UtilisateurEntity getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(UtilisateurEntity utilisateur) {
        this.utilisateur = utilisateur;
    }

    public String getFonction() {
        return fonction;
    }

    public void setFonction(String fonction) {
        this.fonction = fonction;
    }
}
