package com.uadb.backend.identity.institution;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class InstitutionRepository {

    private final JdbcTemplate jdbcTemplate;

    public InstitutionRepository(JdbcTemplate institutionJdbcTemplate) {
        this.jdbcTemplate = institutionJdbcTemplate;
    }

    public Optional<ReferenceInstitutionnelle> rechercherEtudiant(
            String matricule, String email) {

        String sql = """
            SELECT matricule, nom, prenom, email, telephone,
                   filiere_id, niveau_id, NULL AS departement_id,
                   'ETUDIANT' AS type, actif
            FROM etudiants
            WHERE UPPER(matricule) = UPPER(?)
              AND LOWER(email) = LOWER(?)
            """;

        return rechercher(sql, matricule, email);
    }

    public Optional<ReferenceInstitutionnelle> rechercherEnseignant(
            String matricule, String email) {

        String sql = """
            SELECT matricule, nom, prenom, email, telephone,
                   NULL AS filiere_id, NULL AS niveau_id,
                   departement_id, 'ENSEIGNANT' AS type, actif
            FROM enseignants
            WHERE UPPER(matricule) = UPPER(?)
              AND LOWER(email) = LOWER(?)
            """;

        return rechercher(sql, matricule, email);
    }

    private Optional<ReferenceInstitutionnelle> rechercher(
            String sql, String matricule, String email) {

        List<ReferenceInstitutionnelle> resultats =
                jdbcTemplate.query(sql, (rs, rowNum) ->
                        new ReferenceInstitutionnelle(
                                rs.getString("matricule"),
                                rs.getString("nom"),
                                rs.getString("prenom"),
                                rs.getString("email"),
                                rs.getString("telephone"),
                                rs.getObject("filiere_id", Long.class),
                                rs.getObject("niveau_id", Long.class),
                                rs.getObject("departement_id", Long.class),
                                rs.getString("type"),
                                rs.getBoolean("actif")
                        ),
                        matricule.trim(),
                        email.trim()
                );

        return resultats.stream().findFirst();
    }
}
