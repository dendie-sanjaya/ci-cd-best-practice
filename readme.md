

1. perlu install docker cli di jenkis

# 1. Masuk ke terminal Jenkins sebagai root
docker exec -u 0 -it jenkins bash

# 2. Update paket dan install Docker CLI
apt-get update && apt-get install -y docker.io

# 3. Berikan izin akses ke socket Docker agar user jenkins bisa memakainya
chmod 666 /var/run/docker.sock

# 4. Keluar dari container
exit


2
----------------------------
Langkah-langkah Membuat Credential:

    Buka Dashboard Jenkins Anda.

    Klik menu Manage Jenkins di sisi kiri.

    Cari dan klik bagian Credentials.

    Klik tanda panah di sebelah (global), lalu pilih Add Credentials.

    Isi formulir sebagai berikut:

        Kind: Pilih Username with password.

        Scope: Biarkan Global.

        Username: Masukkan username Docker Hub Anda (contoh: dendie).

        Password: Masukkan Docker Hub Access Token Anda (sangat disarankan memakai Token daripada password asli).

        ID: Masukkan dockerhub-auth (Harus sama persis dengan yang ada di script pipeline Anda).

        Description: Isi bebas (contoh: Login Docker Hub).

    Klik Create.


3. sonar integration
-------------------

Error No tool named SonarScanner found terjadi karena Jenkins tidak dapat menemukan konfigurasi binary untuk scanner tersebut. Meskipun plugin sudah diinstal, Anda harus mendaftarkan "nama" scanner tersebut di pengaturan global agar variabel tool 'SonarScanner' dalam script bisa berfungsi.

Berikut adalah langkah-langkah untuk memperbaikinya:
1. Daftarkan SonarScanner di Global Tool Configuration

    Buka dashboard Jenkins Anda.

    Pilih Manage Jenkins > Global Tool Configuration (pada versi terbaru bernama Tools).

    Scroll ke bawah sampai menemukan bagian SonarQube Scanner.

    Klik tombol Add SonarQube Scanner.

    Pada kolom Name, masukkan: SonarScanner (Harus sama persis dengan yang tertulis di script pipeline Anda).

    Centang kotak Install automatically.

    Klik Save.

2. Tambahkan Token SonarQube ke Jenkins

Agar Jenkins bisa mengirim laporan ke SonarQube, Anda butuh token:

    Buka SonarQube Anda (localhost:9000).

    Pergi ke My Account > Security > Generate Token.

    Di Jenkins, pilih Manage Jenkins > Credentials > Add Credentials.

    Pilih Kind: Secret text.

    Tempel token SonarQube tadi di kolom Secret.

    Beri ID: sonar-token.

3. Hubungkan Jenkins ke Server SonarQube

    Pilih Manage Jenkins > System (dulu bernama Configure System).

    Cari bagian SonarQube servers.

    Klik Add SonarQube.

    Name: Isi dengan SonarQube.

    Server URL: Isi dengan http://sonarqube:9000 (karena Anda menggunakan container bernama sonarqube).

    Server authentication token: Pilih kredensial sonar-token yang baru saja Anda buat.

    Klik Save.



3
------------
akun sonarcube admin admin
username : admin
passwrod : Admin12345689!



4 trivy
----------

. Penting: Memperbaiki Tampilan Laporan (CSS)

Seringkali setelah laporan HTML muncul, tampilannya rusak (hanya teks polos) karena fitur keamanan Jenkins (CSP). Jika ini terjadi, lakukan hal berikut:

    Pergi ke Manage Jenkins > Nodes.

    Klik Built-in Node (atau master).

    Klik Script Console di menu kiri.

    Masukkan kode ini dan klik Run:
    Groovy

System.setProperty("hudson.model.DirectoryBrowserSupport.CSP", "")