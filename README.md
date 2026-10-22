# Custom TV Android (CleanYourTV with Prayer Times & Adzan)

Repository ini merupakan versi kustomisasi dari [CleanYourTV](https://github.com/Gathin23/cleanyourtv.git) yang telah disesuaikan untuk kebutuhan smart TV keluarga di Indonesia, bebas iklan/bloatware, dilengkapi fitur **Jadwal Sholat Kemenag RI**, **Kumandang Adzan Otomatis**, dan **Layar Utama Bersih (Clean Wallpaper Mode)**.

---

## Fitur Tambahan pada Fork Ini

1. **Jadwal Sholat Akurat (100% Offline)**
   - Menggunakan kalkulator astronomi offline berstandar **Kementerian Agama RI** (Subuh 20°, Isya 18°, bayangan Ashar rasio 1 + buffer 2 menit).
   - Berjalan mandiri tanpa koneksi internet atau dependensi API luar.
   - Panel *glassmorphism* elegan menampilkan 5 waktu sholat fardhu (**Subuh, Dzuhur, Ashar, Maghrib, Isya**), hitung mundur (*countdown*) waktu sholat berikutnya, serta highlight chip sholat yang aktif.

2. **Kumandang Adzan Otomatis & Nada Adzan Kustom**
   - Saat waktu sholat fardhu tiba, suara adzan akan otomatis berkumandang dan TV menampilkan layar pengingat dengan kaligrafi Arab.
   - **Dua Nada Adzan Berbeda:**
     - `adzan_subuh.mp3`: Khusus dikumandangkan untuk sholat Subuh (memuat lafadz *Ash-shalatu khairum minan-naum*).
     - `adzan.mp3`: Dikumandangkan untuk 4 waktu sholat lainnya (Dzuhur, Ashar, Maghrib, Isya).
   - **Kontrol Remote Mudah:** Suara adzan dan layar pengingat dapat langsung dimatikan seketika hanya dengan menekan sembarang tombol pada remote TV.
   - **Pratinjau / Tes Manual:** Tekan panah Bawah ke panel jadwal sholat lalu tekan OK pada remote untuk mengetes suara adzan.

3. **Layar Utama Bersih (`hideApps: true`)**
   - Seluruh baris icon aplikasi disembunyikan dari layar beranda agar foto wallpaper tampil 100% utuh tanpa terhalang.
   - Semua aplikasi terpasang (YouTube, Netflix, Vidio, Prime Video, Disney+, dll.) tetap dapat dibuka kapan saja melalui **ikon Grid (3x3 titik)** di pojok kanan atas.

4. **Tema Font & Kapsul Cokelat Hangat (*Warm Earthy Brown*)**
   - Dirancang agar kontras tinggi dan mudah terbaca di atas wallpaper dengan bagian atas berwarna putih atau terang.
   - Jam, ucapan waktu, dan tanggal menggunakan warna cokelat tua elegan. Tombol di bilah atas tampil sebagai kapsul bergaris cokelat lembut dan berubah solid saat disorot remote.

---

## Step-by-Step Deploy Menggunakan Antigravity IDE di Laptop

### Tahap 1: Siapkan TV (Aktifkan Debugging Wi-Fi)

1. Di remote TV, buka **Settings (Pengaturan)** $\rightarrow$ **Device Preferences / Sistem** $\rightarrow$ **About (Tentang)**.
2. Cari menu **Android TV OS Build** (atau *Build Number*), lalu klik tombol **OK di remote sebanyak 7 kali berturut-turut** sampai muncul notifikasi: *"You are now a developer!"*.
3. Kembali ke menu Settings $\rightarrow$ masuk ke menu baru **Developer Options (Opsi Pengembang)**.
4. Aktifkan **USB Debugging** (dan jika ada opsi **Network Debugging / Wireless Debugging**, aktifkan juga).
5. Cari tahu **IP Address TV**:
   * Buka Settings $\rightarrow$ Network & Internet $\rightarrow$ klik Wi-Fi yang terhubung $\rightarrow$ catat IP-nya (misal: `192.168.1.50`).
   * *Catatan: Pastikan laptop dan TV terhubung ke jaringan Wi-Fi yang sama.*

---

### Tahap 2: Buka Repo di Antigravity IDE

1. Buka Git Bash / Terminal di laptopmu, lalu clone repo:
   ```bash
   git clone https://github.com/ipangpangeran/custom-tv-android.git
   cd custom-tv-android
   ```
2. Buka folder `custom-tv-android` tersebut di **Antigravity IDE**.

---

### Tahap 3: Prompt untuk Antigravity IDE

Buka chat prompt di Antigravity IDE, lalu salin dan tempel prompt berikut (sesuaikan IP TV, kota, dan kebutuhan wallpapermu):

```markdown
Tolong bantu saya deploy custom-tv-android ke Android/Google TV di rumah saya via Wi-Fi.

### Detail Perangkat:
- Target IP TV: [GANTI DENGAN IP TV KAMU, contoh: 192.168.1.15]
- Port default: 5555
- OS TV: Android TV / Google TV
- Lokasi Jadwal Sholat: [GANTI DENGAN KOTA KAMU, contoh: Bogor / Jakarta / Bandung]

### Tugas yang Harus Dijalankan Bertahap:
1. **Koneksi ADB via Wi-Fi:**
   - Cek apakah TV merespons di port 5555 (`tools/tv.py` atau `adb connect [IP_TV]:5555`).
   - Beritahu saya jika layar TV menampilkan popup "Allow USB Debugging?" agar saya bisa centang "Always allow" dan klik OK di remote TV.

2. **Backup & Discovery:**
   - Lakukan backup state aplikasi bawaan TV terlebih dahulu menggunakan `tools/recovery.py`.
   - Deteksi brand/model TV dan identifikasi bloatware/ad-services bawaan yang aman untuk di-disable.

3. **Install Launcher, Nada Adzan & Debloat:**
   - Pastikan nada adzan (`adzan.mp3` dan `adzan_subuh.mp3`) sudah terpasang di launcher.
   - Build dan jalankan instalasi launcher (`launcher/build-and-install.sh` atau upload via `tools/b64push.py`).
   - Jalankan `tools/disable.sh` untuk menonaktifkan bloatware yang teridentifikasi.
   - Set custom launcher sebagai default Home.

4. **Konfigurasi Wallpaper & Jadwal Sholat:**
   - Terapkan wallpaper kustom jika disediakan (misal dari folder tema lokal).
   - Pastikan panel jadwal sholat muncul di layar dengan perhitungan waktu yang sesuai untuk kota yang ditentukan.
   - Sembunyikan deretan icon aplikasi di beranda (`hideApps: true`) agar wallpaper terlihat bersih, dan pastikan tombol Grid di bilah atas aktif untuk mengakses aplikasi.

5. **Verifikasi:**
   - Lakukan uji coba kumandang adzan (tekan tombol OK pada panel sholat) dan pastikan audio terdengar jernih serta bisa dimatikan via remote.
   - Ambil screenshot verifikasi dari TV untuk memastikan tampilan beranda sudah rapi.
   - Sediakan juga perintah/script rollback (restore) jika sewaktu-waktu ingin mengembalikan ke tampilan awal pabrik.

Pandu saya langkah demi langkah dan jalankan command-nya secara bertahap.
```

---

## Kustomisasi Tambahan

### Mengganti Nada Adzan
Letakkan file audio MP3 baru di folder `nada_adzan/`:
- `nada_adzan/adzan.mp3`: Untuk jadwal Dzuhur, Ashar, Maghrib, dan Isya.
- `nada_adzan/adzan_subuh.mp3`: Khusus untuk jadwal Subuh.

Lalu salin ke `launcher/app/src/main/res/raw/` dan build ulang launcher dengan `./gradlew assembleRelease`.

### Mengganti Lokasi Kota Sholat
Ubah nama kota dan koordinat pada `launcher/app/src/main/java/com/gathin/tvhome/MainActivity.java` di method `updatePrayerTimes()`:
```java
double lat = -6.5950;   // Latitude kota Anda
double lng = 106.8166;  // Longitude kota Anda
double tz  = 7.0;       // Zona Waktu (WIB: 7.0, WITA: 8.0, WIT: 9.0)
String city = "Bogor";
```

### Mengganti Wallpaper
Letakkan gambar wallpaper (resolusi 1920x1080 disarankan) di folder tema baru, misalnya `themes/mytheme/wallpaper.jpg`, lalu terapkan menggunakan script tema:
```bash
TV_HOST=192.168.1.15 ./tools/theme.sh themes/mytheme
```

---

## Cara Rollback ke Tampilan Pabrik

Jika suatu saat ingin mengembalikan TV ke launcher dan pengaturan bawaan pabrik:

```bash
# Jalankan script restore 1-klik:
TV_HOST=192.168.1.15 ./restore-to-factory.sh
```

Atau secara manual menggunakan tool bawaan:
```bash
TV_HOST=192.168.1.15 ./tools/restore.sh debloat-safe.txt com.google.android.tvlauncher
```

---

## Kredit & Lisensi

Proyek ini berbasis pada [CleanYourTV](https://github.com/Gathin23/cleanyourtv.git) oleh [Gathin23](https://github.com/Gathin23).
Lisensi mengikuti lisensi asli open-source CleanYourTV.
