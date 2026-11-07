
package com.uadb.backend.identity.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "profils_etudiants")
public class EtudiantEntity {

    @Id
    private Long id;

    @OneToOne(optional = false)
    @MapsId
    @JoinColumn(name = "utilisateur_id")
    private UtilisateurEntity utilisateur;

    @Column(name = "code_permanent", unique = true, nullable = false, length = 50)
    private String codePermanent;

    // Identifiants gérés par academic-service
    private Long filiereId;

    private Long niveauId;

    public EtudiantEntity() {
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

    public String getCodePermanent() {
        return codePermanent;
    }

    public void setCodePermanent(String codePermanent) {
        this.codePermanent = codePermanent;
    }

    public Long getFiliereId() {
        return filiereId;
    }

    public void setFiliereId(Long filiereId) {
        this.filiereId = filiereId;
    }

    public Long getNiveauId() {
        return niveauId;
    }

    public void setNiveauId(Long niveauId) {
        this.niveauId = niveauId;
    }
}
