# DevOps CI/CD Best Practice

Panduan ini merupakan dokumentasi komprehensif mengenai penerapan **DevOps dan CI/CD (Continuous Integration / Continuous Deployment)** menggunakan berbagai tools open source. Dokumen ini disusun sebagai referensi teknis sekaligus best practice untuk tim engineering dalam membangun pipeline otomasi yang stabil, aman, dan dapat diskalakan.




## Sejarah DevOps dan Latar Belakang Kemunculannya

Konsep DevOps mulai dikenal sekitar tahun 2007–2009 sebagai respons atas permasalahan klasik dalam pengembangan perangkat lunak, yaitu adanya sekat (silo) antara tim Development dan tim Operations. Pada masa itu, tim development berfokus pada kecepatan rilis fitur, sementara tim operations menitikberatkan stabilitas sistem. Perbedaan tujuan ini sering memicu konflik, keterlambatan rilis, dan meningkatnya risiko kegagalan di lingkungan produksi.

DevOps muncul sebagai pendekatan budaya, praktik, dan tooling yang menyatukan kedua peran tersebut. Dengan DevOps, proses build, test, release, dan deployment diotomatisasi sehingga lebih cepat, konsisten, dan dapat diulang. Praktik ini didukung oleh berkembangnya teknologi cloud, container, serta automation tools seperti Jenkins dan Docker. Saat ini, DevOps telah menjadi standar industri dalam pengembangan aplikasi modern.



## Daftar Isi

- [DevOps CI/CD Best Practice](#devops-cicd-best-practice)
  - [Sejarah DevOps dan Latar Belakang Kemunculannya](#sejarah-devops-dan-latar-belakang-kemunculannya)
  - [Daftar Isi](#daftar-isi)
  - [2. Apa itu DevOps?](#2-apa-itu-devops)
  - [3. Kapan DevOps Diperlukan?](#3-kapan-devops-diperlukan)
  - [4. Arsitektur DevOps](#4-arsitektur-devops)
  - [5. Instalasi Tools DevOps](#5-instalasi-tools-devops)
    - [5.1 Menjalankan Docker Compose](#51-menjalankan-docker-compose)
  - [6. Konfigurasi Jenkins](#6-konfigurasi-jenkins)
  - [7. Konfigurasi Gitea (Source Code Repository)](#7-konfigurasi-gitea-source-code-repository)
    - [7.1 Repository Management](#71-repository-management)
    - [7.2 Webhook dan Integrasi Jenkins](#72-webhook-dan-integrasi-jenkins)
    - [7.3 Integrasi Gitea ke Jenkins](#73-integrasi-gitea-ke-jenkins)
    - [7.4 Instalasi Plugin Gitea di Jenkins](#74-instalasi-plugin-gitea-di-jenkins)
    - [7.5 Konfigurasi Credential Gitea](#75-konfigurasi-credential-gitea)
    - [7.6 Whitelist IP Webhook](#76-whitelist-ip-webhook)
  - [8. Konfigurasi Docker](#8-konfigurasi-docker)
    - [8.1 Instalasi Docker Plugin di Jenkins](#81-instalasi-docker-plugin-di-jenkins)
    - [8.2 Akses Docker dari Jenkins](#82-akses-docker-dari-jenkins)
  - [9. Konfigurasi SonarQube](#9-konfigurasi-sonarqube)
    - [9.1 Integrasi SonarQube ke Jenkins](#91-integrasi-sonarqube-ke-jenkins)
    - [9.2 Konfigurasi Global Variable SonarQube](#92-konfigurasi-global-variable-sonarqube)
    - [9.3 Generate Token SonarQube](#93-generate-token-sonarqube)
  - [10. Konfigurasi Trivy](#10-konfigurasi-trivy)
    - [10.1 Instalasi Plugin Trivy](#101-instalasi-plugin-trivy)
    - [10.2 Konfigurasi Trivy](#102-konfigurasi-trivy)
    - [10.3 Tampilan Report Trivy](#103-tampilan-report-trivy)
  - [11. Konfigurasi Docker Hub (Artifact Registry)](#11-konfigurasi-docker-hub-artifact-registry)
  - [12. Konfigurasi Auto Deploy ke Server Target](#12-konfigurasi-auto-deploy-ke-server-target)
  - [13. Flow CI/CD Summary](#13-flow-cicd-summary)
  - [15. Pipeline Jenkins CI/CD](#15-pipeline-jenkins-cicd)
    - [15.1 Membuat Pipeline Baru](#151-membuat-pipeline-baru)
    - [15.2 Konfigurasi Trigger](#152-konfigurasi-trigger)
    - [15.3 Jenkinsfile](#153-jenkinsfile)
    - [15.4 Overview Pipeline](#154-overview-pipeline)
    - [15.5 Verifikasi Git Version](#155-verifikasi-git-version)
    - [15.6 Hasil Scan SonarQube](#156-hasil-scan-sonarqube)
    - [15.7 Quality Gate SonarQube](#157-quality-gate-sonarqube)
    - [15.8 Hasil Scan Trivy](#158-hasil-scan-trivy)
    - [15.9 Push Image ke Docker Hub](#159-push-image-ke-docker-hub)
    - [15.10 Hasil Deploy ke Server](#1510-hasil-deploy-ke-server)





## 2. Apa itu DevOps?

DevOps adalah pendekatan kolaboratif yang mengintegrasikan proses pengembangan perangkat lunak dan operasional sistem dalam satu alur kerja yang terotomasi. DevOps tidak hanya berbicara tentang tools, tetapi juga perubahan budaya kerja, pola komunikasi, dan tanggung jawab bersama. Dengan DevOps, setiap perubahan kode dapat melalui proses build, testing, dan deployment secara konsisten.

Praktik DevOps membantu tim mendeteksi error lebih awal, mengurangi human error, serta mempercepat time-to-market. Selain itu, DevOps mendorong monitoring dan feedback berkelanjutan sehingga kualitas aplikasi terus meningkat dari waktu ke waktu.



## 3. Kapan DevOps Diperlukan?

DevOps diperlukan ketika aplikasi berkembang semakin kompleks dan membutuhkan rilis yang lebih cepat tanpa mengorbankan stabilitas. Organisasi dengan frekuensi deployment tinggi akan sangat terbantu dengan otomasi pipeline CI/CD. Selain itu, DevOps cocok diterapkan pada tim yang ingin meningkatkan kolaborasi lintas fungsi.

Dengan DevOps, proses manual yang rawan kesalahan dapat diminimalkan. Hal ini sangat penting pada sistem berskala besar yang memiliki banyak dependensi dan lingkungan deployment. DevOps juga mendukung praktik continuous improvement melalui feedback yang cepat dan terukur.



## 4. Arsitektur DevOps

Arsitektur DevOps pada implementasi ini dirancang untuk mendukung alur CI/CD end-to-end. Setiap komponen memiliki peran spesifik dalam memastikan kualitas, keamanan, dan konsistensi aplikasi. Integrasi antar tools dilakukan secara otomatis melalui pipeline Jenkins.

![Arsitektur DevOps](./design/arsitekur.png)

- **Gitea** sebagai repository source code
- **Jenkins** sebagai orchestrator pipeline CI/CD
- **SonarQube** untuk analisis kualitas dan keamanan kode
- **Trivy** untuk pemindaian kerentanan container
- **Docker** sebagai container engine
- **Docker Hub** sebagai artifact registry
- **Portainer** untuk manajemen container

Arsitektur ini bersifat modular dan dapat dikembangkan sesuai kebutuhan organisasi.

 Hasi Proses Auto CI/CD menggunakan jenkins

![Deploy Result](./ss/pipeline-17.jpg)

## 5. Instalasi Tools DevOps

Seluruh tools DevOps dijalankan menggunakan Docker Compose untuk memudahkan proses setup dan konsistensi environment. Pendekatan ini memastikan setiap service berjalan terisolasi dan dapat direproduksi di lingkungan lain.

### 5.1 Menjalankan Docker Compose

```bash
docker-compose up -d
```

Perintah ini akan menjalankan seluruh service secara otomatis di background.

![Docker Compose](./ss/docker-compose.jpg)
![Docker Compose 2](./ss/docker-compose-2.jpg)




## 6. Konfigurasi Jenkins

Jenkins berperan sebagai pusat otomasi CI/CD yang mengorkestrasi seluruh proses pipeline. Mulai dari pengambilan source code, build aplikasi, analisis kualitas, hingga deployment dilakukan melalui Jenkins.

Akses Jenkins melalui:

```
http://[ip-server]:8080

```

Konfigurasi awal Jenkins meliputi setup user admin, instalasi plugin, serta integrasi dengan tools lain seperti Gitea, Docker, dan SonarQube.

![Jenkins Setup](./ss/jenknis-1.jpg)

Password awal Jenkins dapat diperoleh dari dalam container Jenkins.

![Initial Password](./ss/jenknis-2.jpg)

![Setup Jenkins](./ss/jenknis-3.jpg)

![Jenkins Ready](./ss/jenknis-4.jpg)



## 7. Konfigurasi Gitea (Source Code Repository)

Gitea digunakan sebagai repository source code yang ringan dan mudah dikelola. Setiap perubahan kode pada repository akan menjadi pemicu pipeline CI/CD.

### 7.1 Repository Management

Repository berfungsi sebagai single source of truth bagi aplikasi. Praktik version control yang baik akan memudahkan kolaborasi dan audit perubahan kode.

![Create Repo](./ss/gitea-2.jpg)
![Push Code](./ss/gitea-3.jpg)

### 7.2 Webhook dan Integrasi Jenkins

Webhook memungkinkan Jenkins menerima notifikasi setiap ada perubahan kode. Dengan mekanisme ini, proses CI/CD dapat berjalan secara otomatis tanpa intervensi manual.

![Webhook 1](./ss/gitea-4-webhook-jenkis.jpg)
![Webhook 2](./ss/gitea-4-webhook-jenkis-2.jpg)


### 7.3 Integrasi Gitea ke Jenkins

![Gitea Server](./ss/gitea-4-webhook-jenkis-server.jpg)
![Gitea Server 2](./ss/gitea-4-webhook-jenkis-server-2.jpg)



### 7.4 Instalasi Plugin Gitea di Jenkins

![Gitea Plugin](./ss/jenknis-5-gitea.jpg)
![Gitea Plugin 2](./ss/jenknis-5-gitea-2.jpg)

### 7.5 Konfigurasi Credential Gitea

Tambahkan credential Gitea agar Jenkins dapat mengakses repository.

![Credential Gitea](./ss/jenknis-7-credential-gitea.jpg)
![Credential Gitea 2](./ss/jenknis-7-credential-gitea-2.jpg)

### 7.6 Whitelist IP Webhook

Tambahkan whitelist IP Jenkins pada konfigurasi Gitea:

```ini
/data/gitea/conf/app.ini

[webhook]
ALLOWED_HOST_LIST = jenkins, 172.18.0.0/16
```

![Whitelist](./ss/jenknis-11-pipeline-gitea-tringger.jpg)




## 8. Konfigurasi Docker

Docker digunakan untuk membungkus aplikasi beserta dependensinya ke dalam container. Pendekatan ini menjamin konsistensi antara environment development, testing, dan production.

Integrasi Docker dengan Jenkins memungkinkan proses build image dan push ke registry dilakukan secara otomatis dan terstandarisasi.

### 8.1 Instalasi Docker Plugin di Jenkins

![Docker Plugin](./ss/jenknis-6-docker.jpg)
![Docker Plugin 2](./ss/jenknis-6-docker-2.jpg)

### 8.2 Akses Docker dari Jenkins

Pastikan Docker CLI dapat diakses dari container Jenkins.

![Docker CLI](./ss/jenknis-11-pipeline-docker-cli.jpg)



## 9. Konfigurasi SonarQube

SonarQube berfungsi sebagai alat analisis kualitas kode dan keamanan. Tool ini membantu mendeteksi bug, code smell, dan potensi vulnerability sejak tahap awal.

Integrasi SonarQube ke Jenkins memastikan setiap build dievaluasi berdasarkan quality gate yang telah ditentukan.


### 9.1 Integrasi SonarQube ke Jenkins

![SonarQube Integration](./ss/jenknis-11-pipeline-sonarcube.jpg)
![SonarQube Integration 2](./ss/jenknis-11-pipeline-sonarcube-2.jpg)

### 9.2 Konfigurasi Global Variable SonarQube

![Global Variable](./ss/jenknis-11-pipeline-sonarcube-global-variable.jpg)
![Global Variable 2](./ss/jenknis-11-pipeline-sonarcube-global-variable-2.jpg)

### 9.3 Generate Token SonarQube

Token digunakan untuk autentikasi Jenkins ke SonarQube.

![Token 1](./ss/jenknis-11-pipeline-sonarcube-token.jpg)
![Token 2](./ss/jenknis-11-pipeline-sonarcube-token-2.jpg)
![Token 3](./ss/jenknis-11-pipeline-sonarcube-token-3.jpg)
![Token 4](./ss/jenknis-11-pipeline-sonarcube-token-4.jpg)

Masukkan token ke Jenkins sebagai credential.

![Token Jenkins](./ss/jenknis-11-pipeline-sonarcube-token-5.jpg)
![Token Jenkins 2](./ss/jenknis-11-pipeline-sonarcube-token-6.jpg)
![Token Jenkins 3](./ss/jenknis-11-pipeline-sonarcube-token-7.jpg)
![Token Jenkins 4](./ss/jenknis-11-pipeline-sonarcube-token-8.jpg)



## 10. Konfigurasi Trivy

Trivy digunakan untuk memindai image container dari sisi keamanan. Tool ini mendeteksi vulnerability berdasarkan database CVE terbaru.

Dengan Trivy, risiko deployment image yang tidak aman dapat diminimalkan sebelum aplikasi dijalankan di environment target.

### 10.1 Instalasi Plugin Trivy

![Trivy Plugin](./ss/jenknis-12-pipeline-overview-trivy-plugin.jpg)
![Trivy Plugin 2](./ss/jenknis-12-pipeline-overview-trivy-plugin-2.jpg)

### 10.2 Konfigurasi Trivy

![Trivy Setting](./ss/jenknis-12-pipeline-overview-trivy-plugin-3jpg)

### 10.3 Tampilan Report Trivy

![Trivy Report](./ss/jenknis-12-pipeline-overview-trivy-plugin-4.jpg)




## 11. Konfigurasi Docker Hub (Artifact Registry)

Docker Hub berfungsi sebagai tempat penyimpanan image hasil build. Registry memungkinkan image digunakan kembali oleh environment lain secara konsisten.

Integrasi Docker Hub ke Jenkins dilakukan menggunakan token agar proses autentikasi lebih aman.

![SSH Install](./ss/jenknis-13-autodeploy-docker-1.jpg)
![SSH Install 2](./ss/jenknis-13-autodeploy-docker-2.jpg)

```bash
apt-get update && apt-get install -y sshpass
```

![Docker Version](./ss/jenknis-2.jpg)



## 12. Konfigurasi Auto Deploy ke Server Target

Auto deploy memungkinkan aplikasi dideploy ke server target secara otomatis setelah pipeline CI/CD berhasil. Proses ini memanfaatkan SSH dan Docker pada server tujuan.

Dengan auto deploy, waktu rilis aplikasi dapat dipersingkat dan risiko human error dapat dikurangi.




## 13. Flow CI/CD Summary

Section ini memberikan gambaran ringkas namun menyeluruh mengenai alur kerja CI/CD yang diimplementasikan pada dokumen ini. Flow CI/CD dirancang untuk memastikan setiap perubahan kode melewati tahapan validasi kualitas, keamanan, dan deployment secara otomatis.

Alur dimulai ketika developer melakukan push code ke repository Gitea. Perubahan tersebut akan memicu webhook yang mengirimkan event ke Jenkins. Jenkins kemudian menjalankan pipeline CI/CD sesuai dengan Jenkinsfile yang telah didefinisikan di repository.

Tahap pertama pipeline adalah proses checkout source code dan build aplikasi. Setelah build berhasil, Jenkins menjalankan analisis kualitas kode menggunakan SonarQube untuk memastikan standar kualitas terpenuhi. Jika quality gate tidak lolos, pipeline akan dihentikan.

Selanjutnya, image container yang dihasilkan akan dipindai menggunakan Trivy untuk mendeteksi potensi vulnerability. Proses ini bertujuan untuk mencegah image yang tidak aman masuk ke environment berikutnya.

Apabila seluruh tahap validasi berhasil, image akan di-push ke Docker Hub sebagai artifact registry. Tahap terakhir adalah proses auto deploy ke server target menggunakan Docker dan SSH. Dengan flow ini, seluruh proses CI/CD berjalan otomatis, konsisten, dan dapat diaudit.



## 15. Pipeline Jenkins CI/CD

Pipeline Jenkins merupakan representasi alur CI/CD secara end-to-end. Pipeline ini mencakup tahapan checkout code, build, testing, scanning, push artifact, hingga deployment.

Setiap tahap pipeline dirancang agar bersifat repeatable, traceable, dan mudah dikembangkan di masa depan. Pipeline CI/CD menjadi fondasi utama dalam penerapan DevOps yang matang dan profesional.


Tahapan ini menjelaskan pembuatan pipeline CI/CD secara end-to-end.

### 15.1 Membuat Pipeline Baru

![Pipeline 1](./ss/pipeline-1.jpg)
![Pipeline 2](./ss/pipeline-2.jpg)
![Pipeline 3](./ss/pipeline-3.jpg)

### 15.2 Konfigurasi Trigger

![Trigger 1](./ss/pipeline-3.jpg)
![Trigger 2](./ss/pipeline-4.jpg)

### 15.3 Jenkinsfile

![Jenkinsfile](./Jenkinsfile.groovy)
![Pipeline Script](./ss/pipeline-6.jpg)

### 15.4 Overview Pipeline

![Overview 1](./ss/pipeline-9.jpg)
![Overview 2](./ss/pipeline-10.jpg)

### 15.5 Verifikasi Git Version

![Git Version 1](./ss/pipeline-7.jpg)
![Git Version 2](./ss/pipeline-8.jpg)
![Git Version 3](./ss/pipeline-8-1.jpg)

### 15.6 Hasil Scan SonarQube

![Sonar Scan](./ss/pipeline-11.jpg)

### 15.7 Quality Gate SonarQube

![Quality Gate](./ss/pipeline-12.jpg)
![Quality Detail](./ss/pipeline-13.jpg)

### 15.8 Hasil Scan Trivy

![Trivy Scan 1](./ss/pipeline-14.jpg)
![Trivy Scan 2](./ss/pipeline-15.jpg)

### 15.9 Push Image ke Docker Hub

![Docker Hub Push](./ss/pipeline-16.jpg)

### 15.10 Hasil Deploy ke Server

![Deploy Result](./ss/pipeline-17.jpg)
![Deploy Container](./ss/pipeline-18.jpg)

Aplikasi berhasil diakses melalui endpoint yang tersedia.

![Application Access](./ss/pipeline-19.jpg)
