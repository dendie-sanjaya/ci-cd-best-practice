
# DevOps CI/CD Best Practice

This guide provides comprehensive documentation for implementing **DevOps and CI/CD (Continuous Integration / Continuous Deployment)** using various open source tools. It is designed as a technical reference and best practice for engineering teams to build automation pipelines that are stable, secure, and scalable.


## History and Background of DevOps

DevOps became popular around 2007–2009 as a response to classic problems in software development, especially the separation (silos) between Development and Operations teams. Developers focused on releasing features quickly, while Operations prioritized system stability. These different goals often caused conflicts, release delays, and increased risk of failure in production.

DevOps emerged as a cultural, practical, and tooling approach that unifies both roles. With DevOps, the build, test, release, and deployment processes are automated, making them faster, more consistent, and repeatable. This practice is supported by the growth of cloud technology, containers, and automation tools like Jenkins and Docker. Today, DevOps is an industry standard for modern application development.

## Table of Contents

- [DevOps CI/CD Best Practice](#devops-cicd-best-practice)
  - [History and Background of DevOps](#history-and-background-of-devops)
  - [Table of Contents](#table-of-contents)
  - [2. What is DevOps?](#2-what-is-devops)
  - [3. When is DevOps Needed?](#3-when-is-devops-needed)
  - [4. DevOps Architecture](#4-devops-architecture)
  - [5. Installing DevOps Tools](#5-installing-devops-tools)
    - [5.1 Running Docker Compose](#51-running-docker-compose)
  - [6. Jenkins Configuration](#6-jenkins-configuration)
  - [7. Gitea Configuration (Source Code Repository)](#7-gitea-configuration-source-code-repository)
    - [7.1 Repository Management](#71-repository-management)
    - [7.2 Webhook and Jenkins Integration](#72-webhook-and-jenkins-integration)
    - [7.3 Integrating Gitea with Jenkins](#73-integrating-gitea-with-jenkins)
    - [7.4 Installing Gitea Plugin in Jenkins](#74-installing-gitea-plugin-in-jenkins)
    - [7.5 Gitea Credential Configuration](#75-gitea-credential-configuration)
    - [7.6 Webhook IP Whitelist](#76-webhook-ip-whitelist)
  - [8. Docker Configuration](#8-docker-configuration)
    - [8.1 Installing Docker Plugin in Jenkins](#81-installing-docker-plugin-in-jenkins)
    - [8.2 Accessing Docker from Jenkins](#82-accessing-docker-from-jenkins)
  - [9. SonarQube Configuration](#9-sonarqube-configuration)
    - [9.1 Integrating SonarQube with Jenkins](#91-integrating-sonarqube-with-jenkins)
    - [9.2 SonarQube Global Variable Configuration](#92-sonarqube-global-variable-configuration)
    - [9.3 Generating SonarQube Token](#93-generating-sonarqube-token)
  - [10. Trivy Configuration](#10-trivy-configuration)
    - [10.1 Installing Trivy Plugin](#101-installing-trivy-plugin)
    - [10.2 Trivy Configuration](#102-trivy-configuration)
    - [10.3 Trivy Report View](#103-trivy-report-view)
  - [11. Docker Hub Configuration (Artifact Registry)](#11-docker-hub-configuration-artifact-registry)
  - [12. Auto Deploy to Target Server](#12-auto-deploy-to-target-server)
  - [13. CI/CD Flow Summary](#13-cicd-flow-summary)
  - [15. Jenkins CI/CD Pipeline](#15-jenkins-cicd-pipeline)
    - [15.1 Creating a New Pipeline](#151-creating-a-new-pipeline)
    - [15.2 Trigger Configuration](#152-trigger-configuration)
    - [15.3 Jenkinsfile](#153-jenkinsfile)
    - [15.4 Pipeline Overview](#154-pipeline-overview)
    - [15.5 Git Version Verification](#155-git-version-verification)
    - [15.6 SonarQube Scan Results](#156-sonarqube-scan-results)
    - [15.7 SonarQube Quality Gate](#157-sonarqube-quality-gate)
    - [15.8 Trivy Scan Results](#158-trivy-scan-results)
    - [15.9 Push Image to Docker Hub](#159-push-image-to-docker-hub)
    - [15.10 Deploy Results to Server](#1510-deploy-results-to-server)


## 2. What is DevOps?

DevOps is a collaborative approach that integrates software development and system operations into one automated workflow. DevOps is not just about tools, but also about changing work culture, communication patterns, and shared responsibility. With DevOps, every code change goes through build, testing, and deployment in a consistent way.

DevOps practices help teams detect errors early, reduce human mistakes, and speed up time-to-market. It also encourages continuous monitoring and feedback, so application quality keeps improving over time.


## 3. When is DevOps Needed?

DevOps is needed when applications become more complex and require faster releases without sacrificing stability. Organizations with frequent deployments benefit greatly from CI/CD pipeline automation. DevOps is also suitable for teams that want to improve cross-functional collaboration.

With DevOps, manual processes that are prone to errors can be minimized. This is very important for large-scale systems with many dependencies and deployment environments. DevOps also supports continuous improvement through fast and measurable feedback.


## 4. DevOps Architecture

The DevOps architecture in this implementation is designed to support end-to-end CI/CD workflows. Each component has a specific role in ensuring application quality, security, and consistency. Integration between tools is automated through Jenkins pipelines.

![DevOps Architecture](./design/arsitekur.png)

- **Gitea** as the source code repository
- **Jenkins** as the CI/CD pipeline orchestrator
- **SonarQube** for code quality and security analysis
- **Trivy** for container vulnerability scanning
- **Docker** as the container engine
- **Docker Hub** as the artifact registry
- **Portainer** for container management

This architecture is modular and can be expanded as needed by the organization.

Auto CI/CD process result using Jenkins:

![Deploy Result](./ss/pipeline-17.jpg)


## 5. Installing DevOps Tools

All DevOps tools are run using Docker Compose to simplify setup and ensure environment consistency. This approach makes sure each service runs in isolation and can be reproduced in other environments.

### 5.1 Running Docker Compose

```bash
docker-compose up -d
```

This command will start all services automatically in the background.

![Docker Compose](./ss/docker-compose.jpg)
![Docker Compose 2](./ss/docker-compose-2.jpg)


## 6. Jenkins Configuration

Jenkins acts as the center of CI/CD automation, orchestrating the entire pipeline process. From fetching source code, building the application, analyzing quality, to deployment—all are handled by Jenkins.

Access Jenkins at:

```
http://[ip-server]:8080
```

Initial Jenkins setup includes creating the admin user, installing plugins, and integrating with other tools like Gitea, Docker, and SonarQube.

![Jenkins Setup](./ss/jenknis-1.jpg)

The initial Jenkins password can be obtained from inside the Jenkins container.

![Initial Password](./ss/jenknis-2.jpg)

![Setup Jenkins](./ss/jenknis-3.jpg)

![Jenkins Ready](./ss/jenknis-4.jpg)


## 7. Gitea Configuration (Source Code Repository)

Gitea is a lightweight and easy-to-manage source code repository. Every code change in the repository triggers the CI/CD pipeline.

### 7.1 Repository Management

The repository serves as the single source of truth for the application. Good version control practices make collaboration and code change auditing easier.

![Create Repo](./ss/gitea-2.jpg)
![Push Code](./ss/gitea-3.jpg)

### 7.2 Webhook and Jenkins Integration

Webhooks allow Jenkins to receive notifications whenever code changes. This mechanism enables the CI/CD process to run automatically without manual intervention.

![Webhook 1](./ss/gitea-4-webhook-jenkis.jpg)
![Webhook 2](./ss/gitea-4-webhook-jenkis-2.jpg)

### 7.3 Integrating Gitea with Jenkins

![Gitea Server](./ss/gitea-4-webhook-jenkis-server.jpg)
![Gitea Server 2](./ss/gitea-4-webhook-jenkis-server-2.jpg)

### 7.4 Installing Gitea Plugin in Jenkins

![Gitea Plugin](./ss/jenknis-5-gitea.jpg)
![Gitea Plugin 2](./ss/jenknis-5-gitea-2.jpg)

### 7.5 Gitea Credential Configuration

Add Gitea credentials so Jenkins can access the repository.

![Credential Gitea](./ss/jenknis-7-credential-gitea.jpg)
![Credential Gitea 2](./ss/jenknis-7-credential-gitea-2.jpg)

### 7.6 Webhook IP Whitelist

Add Jenkins IP to the whitelist in Gitea configuration:

```ini
/data/gitea/conf/app.ini

[webhook]
ALLOWED_HOST_LIST = jenkins, 172.18.0.0/16
```

![Whitelist](./ss/jenknis-11-pipeline-gitea-tringger.jpg)


## 8. Docker Configuration

Docker is used to package the application and its dependencies into containers. This approach ensures consistency between development, testing, and production environments.

Integrating Docker with Jenkins allows the image build and push process to be automated and standardized.

### 8.1 Installing Docker Plugin in Jenkins

![Docker Plugin](./ss/jenknis-6-docker.jpg)
![Docker Plugin 2](./ss/jenknis-6-docker-2.jpg)

### 8.2 Accessing Docker from Jenkins

Make sure the Docker CLI can be accessed from the Jenkins container.

![Docker CLI](./ss/jenknis-11-pipeline-docker-cli.jpg)


## 9. SonarQube Configuration

SonarQube is used for code quality and security analysis. This tool helps detect bugs, code smells, and potential vulnerabilities early in the process.

Integrating SonarQube with Jenkins ensures every build is evaluated based on the defined quality gate.

### 9.1 Integrating SonarQube with Jenkins

![SonarQube Integration](./ss/jenknis-11-pipeline-sonarcube.jpg)
![SonarQube Integration 2](./ss/jenknis-11-pipeline-sonarcube-2.jpg)

### 9.2 SonarQube Global Variable Configuration

![Global Variable](./ss/jenknis-11-pipeline-sonarcube-global-variable.jpg)
![Global Variable 2](./ss/jenknis-11-pipeline-sonarcube-global-variable-2.jpg)

### 9.3 Generating SonarQube Token

Tokens are used for Jenkins authentication to SonarQube.

![Token 1](./ss/jenknis-11-pipeline-sonarcube-token.jpg)
![Token 2](./ss/jenknis-11-pipeline-sonarcube-token-2.jpg)
![Token 3](./ss/jenknis-11-pipeline-sonarcube-token-3.jpg)
![Token 4](./ss/jenknis-11-pipeline-sonarcube-token-4.jpg)

Add the token to Jenkins as a credential.

![Token Jenkins](./ss/jenknis-11-pipeline-sonarcube-token-5.jpg)
![Token Jenkins 2](./ss/jenknis-11-pipeline-sonarcube-token-6.jpg)
![Token Jenkins 3](./ss/jenknis-11-pipeline-sonarcube-token-7.jpg)
![Token Jenkins 4](./ss/jenknis-11-pipeline-sonarcube-token-8.jpg)


## 10. Trivy Configuration

Trivy is used to scan container images for security. This tool detects vulnerabilities based on the latest CVE database.

With Trivy, the risk of deploying unsafe images can be minimized before the application runs in the target environment.

### 10.1 Installing Trivy Plugin

![Trivy Plugin](./ss/jenknis-12-pipeline-overview-trivy-plugin.jpg)
![Trivy Plugin 2](./ss/jenknis-12-pipeline-overview-trivy-plugin-2.jpg)

### 10.2 Trivy Configuration

![Trivy Setting](./ss/jenknis-12-pipeline-overview-trivy-plugin-3jpg)

### 10.3 Trivy Report View

![Trivy Report](./ss/jenknis-12-pipeline-overview-trivy-plugin-4.jpg)


## 11. Docker Hub Configuration (Artifact Registry)

Docker Hub is used as a storage place for built images. The registry allows images to be reused by other environments consistently.

Integrating Docker Hub with Jenkins is done using a token for safer authentication.

![SSH Install](./ss/jenknis-13-autodeploy-docker-1.jpg)
![SSH Install 2](./ss/jenknis-13-autodeploy-docker-2.jpg)

```bash
apt-get update && apt-get install -y sshpass
```

![Docker Version](./ss/jenknis-2.jpg)


## 12. Auto Deploy to Target Server

Auto deploy allows applications to be deployed to the target server automatically after the CI/CD pipeline succeeds. This process uses SSH and Docker on the target server.

With auto deploy, release time is shortened and the risk of human error is reduced.


## 13. CI/CD Flow Summary

This section provides a concise yet complete overview of the CI/CD workflow implemented in this document. The CI/CD flow is designed to ensure every code change passes through quality validation, security checks, and automated deployment.

The flow starts when a developer pushes code to the Gitea repository. This change triggers a webhook that sends an event to Jenkins. Jenkins then runs the CI/CD pipeline as defined in the repository's Jenkinsfile.

The first stage of the pipeline is checking out the source code and building the application. After a successful build, Jenkins runs code quality analysis using SonarQube to ensure standards are met. If the quality gate fails, the pipeline stops.

Next, the resulting container image is scanned using Trivy to detect potential vulnerabilities. This step helps prevent unsafe images from entering the next environment.

If all validation stages succeed, the image is pushed to Docker Hub as an artifact registry. The final stage is auto deploy to the target server using Docker and SSH. With this flow, the entire CI/CD process is automatic, consistent, and auditable.


## 15. Jenkins CI/CD Pipeline

The Jenkins pipeline represents the end-to-end CI/CD workflow. It includes stages for code checkout, build, testing, scanning, artifact push, and deployment.

Each pipeline stage is designed to be repeatable, traceable, and easy to improve in the future. The CI/CD pipeline is the main foundation for mature and professional DevOps implementation.

This section explains how to create a CI/CD pipeline from start to finish.

### 15.1 Creating a New Pipeline

![Pipeline 1](./ss/pipeline-1.jpg)
![Pipeline 2](./ss/pipeline-2.jpg)
![Pipeline 3](./ss/pipeline-3.jpg)

### 15.2 Trigger Configuration

![Trigger 1](./ss/pipeline-3.jpg)
![Trigger 2](./ss/pipeline-4.jpg)

### 15.3 Jenkinsfile

![Jenkinsfile](./Jenkinsfile.groovy)
![Pipeline Script](./ss/pipeline-6.jpg)

### 15.4 Pipeline Overview

![Overview 1](./ss/pipeline-9.jpg)
![Overview 2](./ss/pipeline-10.jpg)

### 15.5 Git Version Verification

![Git Version 1](./ss/pipeline-7.jpg)
![Git Version 2](./ss/pipeline-8.jpg)
![Git Version 3](./ss/pipeline-8-1.jpg)

### 15.6 SonarQube Scan Results

![Sonar Scan](./ss/pipeline-11.jpg)

### 15.7 SonarQube Quality Gate

![Quality Gate](./ss/pipeline-12.jpg)
![Quality Detail](./ss/pipeline-13.jpg)

### 15.8 Trivy Scan Results

![Trivy Scan 1](./ss/pipeline-14.jpg)
![Trivy Scan 2](./ss/pipeline-15.jpg)

### 15.9 Push Image to Docker Hub

![Docker Hub Push](./ss/pipeline-16.jpg)

### 15.10 Deploy Results to Server

![Deploy Result](./ss/pipeline-17.jpg)
![Deploy Container](./ss/pipeline-18.jpg)

The application can be accessed through the available endpoint.

![Application Access](./ss/pipeline-19.jpg)
