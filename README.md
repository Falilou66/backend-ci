# UADB Backend Microservices

Plateforme backend microservices pour l'Université Amadou Diallo (UADB) — gestion des identités, données académiques, matching tutorat, ressources pédagogiques et notifications.

## Architecture

```
┌─────────────┐     ┌──────────────────┐     ┌────────────────────┐
│   Client    │────▶│   API Gateway    │────▶│  Service Discovery │
│  (Web/App)  │     │    (Port 8080)   │     │    Eureka (8761)   │
└─────────────┘     └────────┬─────────┘     └────────────────────┘
                             │
        ┌────────────────────┼────────────────────┐
        ▼                    ▼                    ▼
┌───────────────┐    ┌───────────────┐    ┌───────────────┐
│  Identity     │    │  Academic     │    │  Matching     │
│  Service      │    │  Service      │    │  Service      │
│  (8081)       │    │  (8082)       │    │  (8083)       │
└───────────────┘    └───────────────┘    └───────────────┘
        │                    │                    │
        ▼                    ▼                    ▼
┌───────────────┐    ┌───────────────┐    ┌───────────────┐
│  Resource     │    │ Notification  │    │   MinIO       │
│  Service      │    │  Service      │    │  (Storage)    │
│  (8084)       │    │  (8085)       │    │  (9000)       │
└───────────────┘    └───────────────┘    └───────────────┘
```

## Services

| Service | Port | Description | Base de données |
|---------|------|-------------|-----------------|
| **eureka-server** | 8761 | Service Discovery (Netflix Eureka) | — |
| **api-gateway** | 8080 | Passerelle API, routage, sécurité JWT | — |
| **identity-service** | 8081 | Authentification, utilisateurs, rôles, inscriptions | `identity_db` |
| **academic-service** | 8082 | UFR, Filières, Niveaux, Cours | `academic_db` |
| **matching-service** | 8083 | Tuteurs, séances, disponibilités, avis | `matching_db` |
| **resource-service** | 8084 | Ressources pédagogiques, fichiers (MinIO), signalements | `resource_db` |
| **notification-service** | 8085 | Notifications temps réel | `notification_db` |

## Technologies

- **Java 25** / **Spring Boot 4.1.2**
- **Spring Cloud Gateway** (WebFlux) + **Netflix Eureka**
- **Spring Security** + **OAuth2 Resource Server** (JWT HS256)
- **Spring Data JPA** + **MySQL 8**
- **SpringDoc OpenAPI 2.8** (Swagger UI)
- **MinIO** (stockage objets S3-compatible)
- **Maven** (monorepo multi-modules)
- **Docker Compose** (infra locale)

## Prérequis

- JDK 25+
- Maven 3.9+
- MySQL 8.0+
- MinIO (ou Docker)
- Docker Compose (optionnel pour infra complète)

## Démarrage rapide

### 1. Cloner et configurer l'environnement

```bash
git clone <repo-url>
cd backend-ci

# Copier et adapter les variables d'environnement
cp .env.example .env
# Éditer .env avec vos valeurs, notamment les identifiants de l'administrateur initial
```

`BOOTSTRAP_ADMIN_EMAIL` doit être une adresse email valide et
`BOOTSTRAP_ADMIN_PASSWORD` un mot de passe personnel d'au moins 8 caractères.
L'administrateur est créé au premier démarrage d'identity-service si ce compte
n'existe pas. Les redémarrages ne réinitialisent pas son mot de passe.

### 2. Démarrer l'infrastructure (MySQL + MinIO)

```bash
docker compose up -d mysql minio
```

MySQL crée les bases des cinq microservices (`identity_db`, `academic_db`, `matching_db`, `resource_db`, `notification_db`) et `institution_db`, puis configure l'utilisateur commun `DB_USERNAME` / `DB_PASSWORD`. Celui-ci a les droits sur les bases applicatives et la lecture seule sur `institution_db`. Hibernate crée ensuite les tables de chaque service via `spring.jpa.hibernate.ddl-auto=update`.

Les scripts `docker-entrypoint-initdb.d` ne sont lancés automatiquement qu'à la création d'un volume MySQL vide. Pour appliquer ou réappliquer les droits et bases sur un volume déjà existant, sans effacer les données :

```bash
docker compose up -d mysql
docker compose exec mysql /docker-entrypoint-initdb.d/01-app-databases.sh
```

Le service autonome `service-mise-en-relation` n'est pas inclus dans le Compose racine : il utilise son propre conteneur PostgreSQL sur le port `5433`. Consulte [`service-mise-en-relation/README.md`](./service-mise-en-relation/README.md) pour le démarrer séparément.

### 3. Construire le projet

```bash
mvn clean install -DskipTests
```

### 4. Lancer les services (ordre recommandé)

```bash
# Terminal 1 - Eureka Server (doit démarrer en premier)
mvn spring-boot:run -pl eureka-server

# Terminal 2-n - Services métier (peuvent démarrer en parallèle)
mvn spring-boot:run -pl identity-service
mvn spring-boot:run -pl academic-service
mvn spring-boot:run -pl matching-service
mvn spring-boot:run -pl resource-service
mvn spring-boot:run -pl notification-service

# Terminal dernier - API Gateway
mvn spring-boot:run -pl api-gateway
```

### 5. Vérifier

- **Eureka Dashboard** : http://localhost:8761
- **API Gateway** : http://localhost:8080
- **Swagger UI** (chaque service) : `http://localhost:<port>/swagger-ui.html`

## Configuration

### Variables d'environnement principales (.env)

```bash
# MySQL
DB_USERNAME=identity_app
DB_PASSWORD=identity
DB_URL=jdbc:mysql://localhost:3306/identity_db
ACADEMIC_DB_URL=jdbc:mysql://localhost:3306/academic_db
MATCHING_DB_URL=jdbc:mysql://localhost:3306/matching_db
RESOURCE_DB_URL=jdbc:mysql://localhost:3306/resource_db
NOTIFICATION_DB_URL=jdbc:mysql://localhost:3306/notification_db

# Eureka
EUREKA_URL=http://localhost:8761/eureka/

# JWT (clé base64, min 256 bits)
JWT_SECRET=<votre-clé-base64-de-256-bits-minimum>
JWT_ISSUER=uadb-platform

# Administrateur initial (Docker Compose)
BOOTSTRAP_ADMIN_EMAIL=admin@uadb.edu.sn
BOOTSTRAP_ADMIN_PASSWORD=<mot-de-passe-personnel-de-8-caracteres-minimum>
BOOTSTRAP_ADMIN_NOM=Administrateur
BOOTSTRAP_ADMIN_PRENOM=Initial
BOOTSTRAP_ADMIN_TELEPHONE=

# MinIO
MINIO_ENDPOINT=http://localhost:9000
MINIO_ROOT_USER=minio_uadb
MINIO_ROOT_PASSWORD=your-password
MINIO_BUCKET=uadb-ressources

# Inter-service
INTERNAL_API_KEY=fccc33f3d1acc6ba1248c1d12d87156cf37c635f80d68f8a13465ed51840f0b6
```

### Ports par défaut

| Service | Port HTTP | Port Actuator |
|---------|-----------|---------------|
| eureka-server | 8761 | 8761 |
| api-gateway | 8080 | 8080 |
| identity-service | 8081 | 8081 |
| academic-service | 8082 | 8082 |
| matching-service | 8083 | 8083 |
| resource-service | 8084 | 8084 |
| notification-service | 8085 | 8085 |

## Sécurité & Authentification

### JWT Token

- Algorithme : **HS256**
- Claim `roles` : liste des noms de rôles (`ETUDIANT`, `ENSEIGNANT`, `ADMINISTRATEUR`); les services les convertissent en autorités Spring `ROLE_*`.
- Claim `sub` : adresse e-mail de l'utilisateur
- Claim `userId` : identifiant numérique de l'utilisateur
- Expiration : 24h (configurable via `app.jwt.expiration`)

### Rôles

| Rôle | Description |
|------|-------------|
| `ETUDIANT` | Étudiant inscrit |
| `ENSEIGNANT` | Enseignant inscrit |
| `ADMINISTRATEUR` | Administrateur plateforme |

### Endpoints publics (sans token)

- `POST /api/v1/auth/login`
- `POST /api/v1/auth/inscription`

`/inscription` accepte les rôles `ETUDIANT` et `ENSEIGNANT`, vérifie que
l'adresse se termine par `@uadb.edu.sn`, active le compte immédiatement et
renvoie un JWT dans la réponse `201 Created`. Pour un étudiant, `codePermanent`
est obligatoire; pour un enseignant, `matriculeProfessionnel` est facultatif.
La création publique d'un compte administrateur est refusée.

L'administrateur initial est configuré via `BOOTSTRAP_ADMIN_EMAIL` et
`BOOTSTRAP_ADMIN_PASSWORD` dans `.env`. Un administrateur connecté peut créer
d'autres administrateurs avec `POST /api/v1/admin/administrateurs`.
Au démarrage, les comptes hérités au statut `EN_ATTENTE_VALIDATION` sont
activés automatiquement afin qu'ils ne restent pas bloqués par l'ancien
parcours d'approbation. La valeur de statut est conservée pour compatibilité
avec les données existantes.

### Endpoints protégés (exemples)

| Méthode | Endpoint | Rôles requis |
|---------|----------|--------------|
| POST | `/api/v1/admin/administrateurs` | ADMINISTRATEUR |
| GET | `/api/v1/ufrs/**` | ETUDIANT, ENSEIGNANT, ADMINISTRATEUR |
| POST | `/api/v1/ressources/**` | ENSEIGNANT, ADMINISTRATEUR |

## API Documentation (Swagger)

Chaque service expose sa documentation OpenAPI :

```
http://localhost:8080/swagger-ui.html   # API Gateway
http://localhost:8081/swagger-ui.html   # Identity Service
http://localhost:8082/swagger-ui.html   # Academic Service
http://localhost:8083/swagger-ui.html   # Matching Service
http://localhost:8084/swagger-ui.html   # Resource Service
http://localhost:8085/swagger-ui.html   # Notification Service
```

Spécifications JSON : `/api-docs` sur chaque service.

## Tests Postman

Importer [`scripts/postman/backend-ci.postman_collection.json`](./scripts/postman/backend-ci.postman_collection.json) dans Postman. Les variables de collection contiennent les URL locales des services; renseigner aussi `internalApiKey` avec la valeur configurée dans `INTERNAL_API_KEY` pour les appels interservices.

La requête **Inscription (ETUDIANT ou ENSEIGNANT, compte actif + JWT)** génère une adresse institutionnelle et enregistre directement le JWT dans `accessToken`. Pour créer un admin, renseigner `bootstrapAdminEmail` et `bootstrapAdminPassword` avec les valeurs configurées dans `.env`, exécuter **Connexion de l'administrateur initial**, puis renseigner `nouvelAdminEmail` et `nouvelAdminPassword` et exécuter **Créer un administrateur**.

Les requêtes marquées `ADMINISTRATEUR` ou `TUTEUR` doivent recevoir un JWT avec ce rôle; remplacer `accessToken` par un jeton approprié si nécessaire. Les routes indiquées « route directe » ne sont pas déclarées dans les routes de l'API Gateway et utilisent l'URL directe du service. Le dossier **Nettoyage** contient des suppressions définitives et doit être lancé seulement si souhaité.

Le projet autonome `service-mise-en-relation`, qui n'est pas un module du `pom.xml` racine, conserve sa propre collection : [`service-mise-en-relation/postman/mise-en-relation.postman_collection.json`](./service-mise-en-relation/postman/mise-en-relation.postman_collection.json).

## Communication Inter-Services

### Via API Gateway (recommandé)

```bash
# Exemple : créer un cours via le gateway
curl -X POST http://localhost:8080/api/v1/cours \
  -H "Authorization: Bearer <JWT_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{"nom":"Algorithmique","description":"...","volumeHoraire":30,"filiereId":1,"niveauId":2}'
```

### Direct (service-to-service)

Les services `matching-service` et `resource-service` appellent `academic-service` via `RestClient` :

```java
// matching-service
@Component
public class AcademicCoursClient {
    private final RestClient restClient;
    public AcademicCoursClient(RestClient.Builder builder, @Value("${academic.service.url}") String url) {
        this.restClient = builder.baseUrl(url).build();
    }
    public CoursInfo obtenirCours(Long coursId) { ... }
}
```

Configuration dans `application.yml` :
```yaml
academic:
  service:
    url: ${ACADEMIC_SERVICE_URL:http://localhost:8082}
```

## Base de données

### Schémas principaux

**identity_db**
- `utilisateurs` — comptes utilisateurs
- `utilisateur_roles` — rôles (many-to-many)
- `profils_enseignants` — profils enseignants (matricule, spécialité, grade)
- `etudiants_internes` — profils étudiants (matricule, filière, niveau)
- `candidatures_tuteur` — candidatures tutorat
- `inscriptions_institutionnelles` — inscriptions admin

**academic_db**
- `ufrs` — Unités de Formation et de Recherche
- `filieres` — Filières d'études
- `niveaux` — Niveaux (L1, L2, M1, M2...)
- `cours` — Cours (liés filière + niveau)

**matching_db**
- `tuteurs_internes` — Tuteurs disponibles
- `disponibilites` — Créneaux horaires
- `seances` — Séances de tutorat
- `avis` — Évaluations tuteurs

**resource_db**
- `ressources` — Ressources pédagogiques (métadonnées)
- `fichiers_stockes` — Fichiers dans MinIO
- `signalements` — Signalements de contenus

**notification_db**
- `notifications` — Notifications utilisateurs

## Déploiement Docker (Recommandé)

Le projet inclut une configuration `docker-compose.yml` complète pour lancer toute la stack (9 conteneurs) en une commande.

### Prérequis Docker

- Docker Engine 24+
- Docker Compose v2+ (plugin `docker compose`)

### 1. Configuration

```bash
cp .env.example .env
# Éditer .env si besoin (mots de passe, JWT_SECRET, etc.)
```

### 2. Construire les images

```bash
# Construire tous les services (premier build ~5-10 min)
docker compose build

# Ou builder un service spécifique
docker compose build identity-service
```

### 3. Lancer la stack complète

```bash
# Démarrage en arrière-plan (tous les services)
docker compose up -d

# Suivre les logs
docker compose logs -f

# Logs d'un service spécifique
docker compose logs -f identity-service
```

### 4. Vérifier les services

| Service | URL | Description |
|---------|-----|-------------|
| **Eureka Dashboard** | http://localhost:8761 | Service discovery |
| **API Gateway** | http://localhost:8080 | Point d'entrée unique |
| **Identity Service** | http://localhost:8081/swagger-ui.html | Auth & Users |
| **Academic Service** | http://localhost:8082/swagger-ui.html | UFR, Filières, Cours |
| **Matching Service** | http://localhost:8083/swagger-ui.html | Tutorat |
| **Resource Service** | http://localhost:8084/swagger-ui.html | Ressources & MinIO |
| **Notification Service** | http://localhost:8085/swagger-ui.html | Notifications |
| **MinIO Console** | http://localhost:9001 | Stockage objets (UI) |

### 5. Arrêter la stack

```bash
# Arrêt simple (garde les volumes)
docker compose down

# Arrêt + suppression volumes (reset DB/MinIO)
docker compose down -v

# Arrêt + suppression images locales
docker compose down --rmi local
```

### Démarrage sélectif (profils)

Pour ne pas tout lancer (ex: dev sur un seul service) :

```bash
# Infra seulement
docker compose up -d mysql minio

# Infra + Eureka + un service métier
docker compose up -d mysql minio eureka-server identity-service

# Lancer API Gateway après que les services soient healthy
docker compose up -d api-gateway
```

> **Ordre de dépendance** : `mysql`/`minio` → `eureka-server` → services métier → `api-gateway`
> Le `docker-compose.yml` gère cela via `depends_on` + `healthcheck`.

### Variables Docker (.env)

```bash
# MySQL
MYSQL_ROOT_PASSWORD=root
DB_USERNAME=identity_app
DB_PASSWORD=identity

# JWT (clé base64 privée, identique partout)
JWT_SECRET=<votre-clé-base64-de-256-bits-minimum>
JWT_ISSUER=uadb-platform

# Administrateur initial (créé au premier démarrage d'identity-service)
BOOTSTRAP_ADMIN_EMAIL=admin@uadb.edu.sn
BOOTSTRAP_ADMIN_PASSWORD=<mot-de-passe-personnel-de-8-caracteres-minimum>
BOOTSTRAP_ADMIN_NOM=Administrateur
BOOTSTRAP_ADMIN_PRENOM=Initial
BOOTSTRAP_ADMIN_TELEPHONE=

# MinIO
MINIO_ROOT_USER=minio_uadb
MINIO_ROOT_PASSWORD=minio123456
MINIO_BUCKET=uadb-ressources

# Inter-service
INTERNAL_API_KEY=fccc33f3d1acc6ba1248c1d12d87156cf37c635f80d68f8a13465ed51840f0b6
```

### Architecture Docker

```
┌─────────────────────────────────────────────────────────────┐
│                    uadb-network (bridge)                    │
├─────────────────────────────────────────────────────────────┤
│  mysql:3306  │  minio:9000  │  eureka:8761                  │
├──────────────┼──────────────┼───────────────────────────────┤
│                    Spring Services                          │
│  identity:8081  academic:8082  matching:8083                │
│  resource:8084  notification:8085                           │
├─────────────────────────────────────────────────────────────┤
│  api-gateway:8080 (lb://service-name via Eureka)            │
└─────────────────────────────────────────────────────────────┘
```

### Healthchecks

Tous les services ont des healthchecks configurés :

```bash
# Vérifier l'état de santé
docker compose ps

# Exemple sortie attendue :
# NAME                STATUS                    PORTS
# uadb-mysql          healthy                   3306/tcp
# uadb-minio          healthy                   9000-9001/tcp
# uadb-eureka         healthy                   8761/tcp
# uadb-identity       healthy                   8081/tcp
# ...
```

### Dépannage Docker

| Problème | Solution |
|----------|----------|
| `healthy` ne passe jamais | `docker compose logs <service>` → vérifier `/actuator/health` accessible |
| Port déjà utilisé | Changer le port `published` dans `docker-compose.yml` ou arrêter le processus local |
| DB non créée | Vérifier `createDatabaseIfNotExist=true` dans les JDBC URLs |
| MinIO bucket manquant | Le `resource-service` le crée au démarrage si `MINIO_BUCKET` configuré |
| Eureka vide | Attendre 30-60s après démarrage services (heartbeat 10s) |
| Build échoue (Maven) | `docker compose build --no-cache <service>` |
| OOM / lenteur | Augmenter `docker` memory limit (Docker Desktop → Settings → Resources) |

### Production (notes)

Pour la prod, adapter :

- `docker-compose.prod.yml` avec :
  - Images pré-buildées (registry)
  - Secrets via Docker Secrets / HashiCorp Vault
  - Reverse proxy (Traefik/NGINX) devant Gateway
  - MySQL/MinIO externalisés (RDS, S3)
  - Réplicas + load balancing
  - Logging centralisé (ELK/Loki)
  - Monitoring (Prometheus/Grafana + Micrometer)

## Tests

```bash
# Tests unitaires + intégration
mvn test

# Tests d'un module spécifique
mvn test -pl identity-service

# Rapport de couverture (JaCoCo)
mvn verify jacoco:report
```

## Structure du projet

```
backend-ci/
├── pom.xml                          # Parent POM (dependency management)
├── .env.example                     # Variables d'environnement exemple
├── .env                             # Variables locales (gitignored)
├── docker-compose.yml               # Infrastructure locale
├── eureka-server/                   # Service Discovery
│   ├── pom.xml
│   └── src/main/...
├── api-gateway/                     # Passerelle API (Spring Cloud Gateway)
│   ├── pom.xml
│   └── src/main/...
├── identity-service/                # Auth & Utilisateurs
│   ├── pom.xml
│   └── src/main/
│       ├── java/.../entity/         # JPA Entities
│       ├── java/.../repository/     # Spring Data Repositories
│       ├── java/.../service/        # Business Logic
│       ├── java/.../controller/     # REST Controllers
│       ├── java/.../config/         # Security, OpenAPI
│       └── resources/
│           └── application.properties
├── academic-service/                # Données académiques
├── matching-service/                # Matching tutorat
├── resource-service/                # Ressources & MinIO
└── notification-service/            # Notifications
```

## Bonnes pratiques

1. **Toujours démarrer Eureka en premier** — les services s'enregistrent au démarrage
2. **Utiliser le Gateway** pour tous les appels externes — gère JWT, routage, discovery
3. **Configurer via .env** — jamais de secrets en dur dans le code
4. **Swagger-first** — documenter les endpoints dans les controllers (`@Operation`, `@Parameter`)
5. **Tests d'intégration** avec `@SpringBootTest` + `Testcontainers` pour MySQL

## Dépannage

| Problème | Solution |
|----------|----------|
| Service ne s'enregistre pas sur Eureka | Vérifier `EUREKA_URL`, logs au démarrage, pare-feu |
| 401 Unauthorized sur Gateway | Vérifier `JWT_SECRET` identique partout, token valide, claim `roles` présent |
| Erreur de connexion MySQL | Vérifier que MySQL est up, credentials dans `.env`, bases créées |
| MinIO connection refused | Vérifier `MINIO_ENDPOINT`, bucket existe, credentials |
| Swagger UI inaccessible | Vérifier `springdoc.swagger-ui.path`, port correct, service démarré |

## Licence

Projet privé UADB — usage interne uniquement.

---

**Équipe Backend UADB** — `backend@uadb.edu`