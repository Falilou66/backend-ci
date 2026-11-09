CREATE DATABASE IF NOT EXISTS institution_db
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE institution_db;

-- =========================================
-- 1. DEPARTEMENTS
-- =========================================

CREATE TABLE IF NOT EXISTS departements (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(20) NOT NULL UNIQUE,
    nom VARCHAR(150) NOT NULL
);

INSERT INTO departements (code, nom) VALUES
('INFO', 'Informatique'),
('GEST', 'Gestion'),
('MATH', 'Mathématiques')
ON DUPLICATE KEY UPDATE nom = VALUES(nom);

-- =========================================
-- 2. FILIERES
-- =========================================

CREATE TABLE IF NOT EXISTS filieres (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(20) NOT NULL UNIQUE,
    nom VARCHAR(150) NOT NULL,
    departement_id BIGINT NOT NULL,
    FOREIGN KEY (departement_id) REFERENCES departements(id)
);

INSERT INTO filieres (code, nom, departement_id) VALUES
('MIAGE', 'Méthodes Informatiques Appliquées à la Gestion', 1),
('GL', 'Génie Logiciel', 1),
('SI', 'Systèmes d Information', 1)
ON DUPLICATE KEY UPDATE nom = VALUES(nom);

-- =========================================
-- 3. NIVEAUX
-- =========================================

CREATE TABLE IF NOT EXISTS niveaux (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(20) NOT NULL UNIQUE,
    libelle VARCHAR(100) NOT NULL
);

INSERT INTO niveaux (code, libelle) VALUES
('L1', 'Licence 1'),
('L2', 'Licence 2'),
('L3', 'Licence 3'),
('M1', 'Master 1'),
('M2', 'Master 2')
ON DUPLICATE KEY UPDATE libelle = VALUES(libelle);

-- =========================================
-- 4. ETUDIANTS
-- =========================================

CREATE TABLE IF NOT EXISTS etudiants (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    matricule VARCHAR(50) NOT NULL UNIQUE,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    telephone VARCHAR(20),
    filiere_id BIGINT NOT NULL,
    niveau_id BIGINT NOT NULL,
    actif BOOLEAN NOT NULL DEFAULT TRUE,
    FOREIGN KEY (filiere_id) REFERENCES filieres(id),
    FOREIGN KEY (niveau_id) REFERENCES niveaux(id)
);

INSERT INTO etudiants
(matricule, nom, prenom, email, telephone, filiere_id, niveau_id)
VALUES
('ETU2026001','Diop','Mamadou','mamadou.diop@etudiant.uadb.test','770000001',1,5),
('ETU2026002','Fall','Aminata','aminata.fall@etudiant.uadb.test','770000002',1,4),
('ETU2026003','Ndiaye','Ibrahima','ibrahima.ndiaye@etudiant.uadb.test','770000003',2,5),
('ETU2026004','Sarr','Fatou','fatou.sarr@etudiant.uadb.test','770000004',3,4),
('ETU2026005','Ba','Moussa','moussa.ba@etudiant.uadb.test','770000005',1,3),
('ETU2026006','Gueye','Awa','awa.gueye@etudiant.uadb.test','770000006',2,3),
('ETU2026007','Sow','Abdoulaye','abdoulaye.sow@etudiant.uadb.test','770000007',3,2),
('ETU2026008','Diallo','Mariama','mariama.diallo@etudiant.uadb.test','770000008',1,2),
('ETU2026009','Faye','Cheikh','cheikh.faye@etudiant.uadb.test','770000009',2,1),
('ETU2026010','Sy','Khady','khady.sy@etudiant.uadb.test','770000010',3,1),
('ETU2026011','Diouf','Ousmane','ousmane.diouf@etudiant.uadb.test','770000011',1,5),
('ETU2026012','Cisse','Aissatou','aissatou.cisse@etudiant.uadb.test','770000012',2,4),
('ETU2026013','Kane','Amadou','amadou.kane@etudiant.uadb.test','770000013',3,3),
('ETU2026014','Gomis','Mame','mame.gomis@etudiant.uadb.test','770000014',1,2),
('ETU2026015','Thiam','Babacar','babacar.thiam@etudiant.uadb.test','770000015',2,1),
('ETU2026016','Seck','Ndeye','ndeye.seck@etudiant.uadb.test','770000016',3,5),
('ETU2026017','Lô','Pape','pape.lo@etudiant.uadb.test','770000017',1,4),
('ETU2026018','Dieng','Sokhna','sokhna.dieng@etudiant.uadb.test','770000018',2,3),
('ETU2026019','Mbaye','Alioune','alioune.mbaye@etudiant.uadb.test','770000019',3,2),
('ETU2026020','Sane','Coumba','coumba.sane@etudiant.uadb.test','770000020',1,1)
ON DUPLICATE KEY UPDATE
nom = VALUES(nom),
prenom = VALUES(prenom),
telephone = VALUES(telephone),
filiere_id = VALUES(filiere_id),
niveau_id = VALUES(niveau_id);

-- =========================================
-- 5. ENSEIGNANTS
-- =========================================

CREATE TABLE IF NOT EXISTS enseignants (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    matricule VARCHAR(50) NOT NULL UNIQUE,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    telephone VARCHAR(20),
    departement_id BIGINT NOT NULL,
    actif BOOLEAN NOT NULL DEFAULT TRUE,
    FOREIGN KEY (departement_id) REFERENCES departements(id)
);

INSERT INTO enseignants
(matricule, nom, prenom, email, telephone, departement_id)
VALUES
('ENS2026001','Diop','Abdou','abdou.diop@enseignant.uadb.test','760000001',1),
('ENS2026002','Fall','Moussa','moussa.fall@enseignant.uadb.test','760000002',1),
('ENS2026003','Ndiaye','Fatou','fatou.ndiaye@enseignant.uadb.test','760000003',2),
('ENS2026004','Sarr','Ibrahima','ibrahima.sarr@enseignant.uadb.test','760000004',3),
('ENS2026005','Gueye','Aminata','aminata.gueye@enseignant.uadb.test','760000005',1)
ON DUPLICATE KEY UPDATE
nom = VALUES(nom),
prenom = VALUES(prenom),
telephone = VALUES(telephone),
departement_id = VALUES(departement_id);

