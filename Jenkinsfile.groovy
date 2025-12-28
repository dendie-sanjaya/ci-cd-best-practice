pipeline {
    agent any

    environment {
        // Identitas Docker Hub
        DOCKER_HUB_USER = "dendie"
        IMAGE_NAME = "my-apps"
        
        // Format Tag: YYYYMMDD.BUILD_NUMBER
        DATE_TAG = sh(script: "date +'%Y%m%d'", returnStdout: true).trim()
        IMAGE_TAG = "${DATE_TAG}.${env.BUILD_ID}"
        DOCKER_HUB_REPO = "${DOCKER_HUB_USER}/${IMAGE_NAME}"
        
        // Konfigurasi SonarScanner Tool
        SCANNER_HOME = tool 'SonarScanner'
        
        // Konfigurasi Server Target
        TARGET_SERVER = "192.168.100.35"
        TARGET_USER = "dendie"
        TARGET_PASS = "12345678"
    }

    stages {
        stage('1. Checkout Source Code') {
            steps {
                git branch: 'main', 
                    credentialsId: 'gitea-auth', 
                    url: 'http://gitea:3000/dendie/my-apps.git'
            }
        }

        stage('2. SonarQube Analysis') {
            steps {
                script {
                    withSonarQubeEnv('SonarQube.') {
                        sh """
                            ${SCANNER_HOME}/bin/sonar-scanner \
                            -Dsonar.projectKey=${IMAGE_NAME} \
                            -Dsonar.sources=. \
                            -Dsonar.host.url=http://sonarqube:9000
                        """
                    }
                }
            }
        }

        stage('3. Build Docker Image') {
            steps {
                echo "Building Docker Image: ${DOCKER_HUB_REPO}:${IMAGE_TAG}"
                sh "docker build -t ${DOCKER_HUB_REPO}:${IMAGE_TAG} ."
                sh "docker tag ${DOCKER_HUB_REPO}:${IMAGE_TAG} ${DOCKER_HUB_REPO}:latest"
            }
        }

        stage('4. Security Scan (Trivy)') {
            steps {
                echo "Scanning Image and Generating HTML Report..."
                script {
                    sh "docker run --rm -v /var/run/docker.sock:/var/run/docker.sock aquasec/trivy:latest image ${DOCKER_HUB_REPO}:${IMAGE_TAG}"
                    
                    sh """
                        docker run --rm \
                        -v /var/run/docker.sock:/var/run/docker.sock \
                        -v ${WORKSPACE}:/root/ \
                        aquasec/trivy:latest image \
                        --format template --template "@contrib/html.tpl" \
                        --output /root/trivy-report.html \
                        ${DOCKER_HUB_REPO}:${IMAGE_TAG}
                    """
                    
                    publishHTML([
                        allowMissing: false,
                        alwaysLinkToLastBuild: true,
                        keepAll: true,
                        reportDir: '.',
                        reportFiles: 'trivy-report.html',
                        reportName: 'Trivy Security Report'
                    ])
                }
            }
        }

        stage('5. Push to Docker Hub') {
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: 'dockerhub-auth', passwordVariable: 'DOCKER_PASS', usernameVariable: 'DOCKER_USER')]) {
                        sh "echo \$DOCKER_PASS | docker login -u \$DOCKER_USER --password-stdin"
                        sh "docker push ${DOCKER_HUB_REPO}:${IMAGE_TAG}"
                        sh "docker push ${DOCKER_HUB_REPO}:latest"
                    }
                }
            }
        }

         stage('6. Auto Deploy to Server') {
            steps {
                echo "Deploying Version ${IMAGE_TAG} to ${TARGET_SERVER}..."
                script {
                    sh """
                        sshpass -p '${TARGET_PASS}' ssh -o StrictHostKeyChecking=no ${TARGET_USER}@${TARGET_SERVER} "
                            # Berhentikan dan hapus container lama jika ada
                            docker stop ${IMAGE_NAME} ; docker rm ${IMAGE_NAME}
                            
                            # Pull image spesifik yang baru saja di-push ke Docker Hub
                            docker pull ${DOCKER_HUB_REPO}:${IMAGE_TAG}
                            
                            # Jalankan container dengan tag versi terbaru
                            docker run -d --name ${IMAGE_NAME} -p 80:80 ${DOCKER_HUB_REPO}:${IMAGE_TAG}
                            
                            # Opsional: Hapus image yang tidak terpakai (dangling) untuk hemat disk
                            docker image prune -f
                        "
                    """
                }
            }
        }

    }

    post {
        always {
            echo "Cleaning up local images to save disk space..."
            sh "docker rmi ${DOCKER_HUB_REPO}:${IMAGE_TAG} ${DOCKER_HUB_REPO}:latest || true"
        }
        success {
            echo "Pipeline Berhasil! Laporan SonarQube & Trivy diperbarui & Aplikasi telah di-deploy ke ${TARGET_SERVER}."
        }
        failure {
            echo "Pipeline Gagal. Silakan cek log pada stage yang berwarna merah."
        }
    }
}