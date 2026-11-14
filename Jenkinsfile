// Pipeline CI du backend Jangalma (7 microservices Spring Boot)
// Checkout -> Build Maven -> Tests -> Scan OWASP -> 7 images Docker -> Scan Trivy -> Push Docker Hub
pipeline {
    agent any

    environment {
        // Préfixe des images sur Docker Hub : seckou04/jangalma-<service>
        IMAGE_PREFIX = 'seckou04/jangalma-'
        IMAGE_TAG    = "${env.BUILD_NUMBER}"
        // Les 7 modules Maven, construits avec le MÊME Dockerfile commun
        SERVICES     = 'eureka-server api-gateway identity-service academic-service matching-service resource-service notification-service'
        // Dépôt Maven local dans le volume "maven-repo" : dépendances et base NVD téléchargées une seule fois
        MVN          = 'mvn -B -Dmaven.repo.local=/root/.m2/repository'
    }

    options {
        timestamps()
        buildDiscarder(logRotator(numToKeepStr: '10'))
        // Plus long que le frontend : 7 images + base NVD au premier lancement
        timeout(time: 120, unit: 'MINUTES')
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
                // Repart sans les rapports du build précédent (sinon ils fausseraient la comparaison)
                sh 'rm -f target/dependency-check-report.* trivy-*.json trivy-backend.txt rapport-securite.csv constats-*.csv resume-securite.txt reference-securite.csv'
            }
        }

        stage('Build') {
            // Conteneur Maven + Java 25 emprunté le temps de l'étape, avec le cache Maven persistant
            agent {
                docker {
                    image 'maven:3.10-eclipse-temurin-25-alpine'
                    args '-v maven-repo:/root/.m2'
                    reuseNode true
                }
            }
            steps {
                sh '$MVN clean package -DskipTests'
            }
        }

        stage('Tests') {
            agent {
                docker {
                    image 'maven:3.10-eclipse-temurin-25-alpine'
                    args '-v maven-repo:/root/.m2'
                    reuseNode true
                }
            }
            steps {
                // Lance les tests unitaires de tous les modules (s'activera dès que les tests seront écrits)
                sh '$MVN test'
            }
            post {
                always {
                    // Résultats des tests affichés dans Jenkins (onglet "Test Result")
                    junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
                }
            }
        }

        stage('Scan du code (OWASP Dependency-Check)') {
            agent {
                docker {
                    image 'maven:3.10-eclipse-temurin-25-alpine'
                    args '-v maven-repo:/root/.m2'
                    reuseNode true
                }
            }
            steps {
                withCredentials([string(credentialsId: 'nvd-api-key', variable: 'NVD_API_KEY')]) {
                    // Rapport unique pour les 7 modules ; échec si une faille a un score CVSS >= 9 (critique)
                    // OSS Index est désactivé : il exige désormais un compte Sonatype
                    sh '''
                        $MVN org.owasp:dependency-check-maven:13.0.0:aggregate \
                          -DnvdApiKey="$NVD_API_KEY" \
                          -DfailBuildOnCVSS=9 \
                          -Dformats=HTML,JSON \
                          -DossindexAnalyzerEnabled=false
                    '''
                }
            }
        }

        stage('Images Docker') {
            steps {
                // Une image par service, avec le Dockerfile commun : deux tags (numéro de build + latest)
                sh '''
                    for s in $SERVICES; do
                      docker build --build-arg SERVICE=$s \
                        -t ${IMAGE_PREFIX}$s:$IMAGE_TAG \
                        -t ${IMAGE_PREFIX}$s:latest .
                    done
                '''
            }
        }

        stage('Scan des images (Trivy)') {
            // Répartition des scans : OWASP (étape précédente) analyse les bibliothèques Java,
            // Trivy analyse ici le système de l'image (paquets Alpine) : --pkg-types os.
            // Sans cela, Trivy ré-analyse chaque gros app.jar et dépasse son délai (build #5).
            environment {
                TRIVY_OPTS = '--no-progress --scanners vuln --pkg-types os --timeout 15m'
            }
            steps {
                // Rapport complet (HIGH + CRITICAL) des 7 images, conservé pour le rapport de sécurité
                sh '''
                    rm -f trivy-backend.txt
                    for s in $SERVICES; do
                      echo "===== $s" >> trivy-backend.txt
                      docker run --rm \
                        -v /var/run/docker.sock:/var/run/docker.sock \
                        -v trivy-cache:/root/.cache/ \
                        aquasec/trivy:latest image \
                        $TRIVY_OPTS --severity HIGH,CRITICAL --exit-code 0 \
                        ${IMAGE_PREFIX}$s:$IMAGE_TAG >> trivy-backend.txt
                    done
                '''
                // Même analyse en JSON, TOUTES sévérités : une entrée par faille avec son CVE.
                // Sert au rapport comparable d'un build à l'autre (ci/rapport-securite.sh).
                sh '''
                    rm -f trivy-*.json
                    for s in $SERVICES; do
                      docker run --rm \
                        -v /var/run/docker.sock:/var/run/docker.sock \
                        -v trivy-cache:/root/.cache/ \
                        aquasec/trivy:latest image \
                        $TRIVY_OPTS --skip-db-update --format json --exit-code 0 \
                        ${IMAGE_PREFIX}$s:$IMAGE_TAG > trivy-$s.json
                    done
                '''
                // Échec si une image contient une faille CRITIQUE corrigeable
                sh '''
                    for s in $SERVICES; do
                      docker run --rm \
                        -v /var/run/docker.sock:/var/run/docker.sock \
                        -v trivy-cache:/root/.cache/ \
                        aquasec/trivy:latest image \
                        $TRIVY_OPTS --skip-db-update --ignore-unfixed \
                        --severity CRITICAL --exit-code 1 \
                        ${IMAGE_PREFIX}$s:$IMAGE_TAG
                    done
                '''
            }
        }

        stage('Push Docker Hub') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'dockerhub-credentials',
                    usernameVariable: 'DOCKER_USER',
                    passwordVariable: 'DOCKER_PASS'
                )]) {
                    sh 'echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin'
                    sh '''
                        for s in $SERVICES; do
                          docker push ${IMAGE_PREFIX}$s:$IMAGE_TAG
                          docker push ${IMAGE_PREFIX}$s:latest
                        done
                    '''
                }
            }
        }
    }

    post {
        always {
            // Rapport de sécurité comparable : mêmes noms de fichiers et mêmes colonnes à chaque build,
            // un constat = un identifiant stable (CVE), comparé au dernier build réussi.
            // Lancé même si un scan a bloqué le build : c'est là qu'on en a le plus besoin.
            sh 'bash ci/rapport-securite.sh backend-ci || echo "Rapport de sécurité non généré"'
            script {
                if (fileExists('resume-securite.txt')) {
                    currentBuild.description = readFile('resume-securite.txt').trim()
                }
            }
            // Rapports de sécurité conservés avec chaque build (onglet "Artefacts")
            archiveArtifacts artifacts: 'target/dependency-check-report.*, trivy-backend.txt, trivy-*.json, rapport-securite.csv, constats-nouveaux.csv, constats-corriges.csv, resume-securite.txt', allowEmptyArchive: true
            sh 'docker logout || true'
        }
        success {
            // Seul un build complet et réussi devient la référence de comparaison
            sh 'bash ci/rapport-securite.sh backend-ci --enregistrer'
            echo "✅ 7 images publiées : ${IMAGE_PREFIX}<service>:${IMAGE_TAG}"
        }
        failure {
            echo '❌ Le pipeline backend a échoué : voir les logs de l\'étape en rouge'
        }
    }
}
