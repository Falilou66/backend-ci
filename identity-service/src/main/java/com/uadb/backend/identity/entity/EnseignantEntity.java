
package com.uadb.backend.identity.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "profils_enseignants")
public class EnseignantEntity {

    @Id
    private Long id;

    @OneToOne(optional = false)
    @MapsId
    @JoinColumn(name = "utilisateur_id")
    private UtilisateurEntity utilisateur;

    @Column(unique = true, length = 50)
    private String matriculeProfessionnel;

    @Column(length = 150)
    private String specialite;

    @Column(length = 100)
    private String grade;

    public EnseignantEntity() {
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

    public String getMatriculeProfessionnel() {
        return matriculeProfessionnel;
    }

    public void setMatriculeProfessionnel(String matriculeProfessionnel) {
        this.matriculeProfessionnel = matriculeProfessionnel;
    }

    public String getSpecialite() {
        return specialite;
    }

    public void setSpecialite(String specialite) {
        this.specialite = specialite;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }
}
