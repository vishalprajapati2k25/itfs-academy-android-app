# 🚀 ITFS Academy - Production Release Information

Official release documentation for **ITFS Academy**.

---

## 📥 Official Release Downloads (Hosted via GitHub Releases CDN)

In accordance with modern software release standards, compiled binaries (`.apk`, `.aab`) are hosted on **GitHub Releases** rather than tracked inside the Git repository tree, preventing repository bloat while guaranteeing high-speed downloads:

| File | Type | Size | Official Direct Download Link | Description |
| :--- | :--- | :---: | :--- | :--- |
| **`ITFSAcademy-v1.0.0-release.apk`** | Signed Release APK | 2.2 MB | [⬇️ **Download Release APK**](https://github.com/vishalprajapati2k25/itfs-academy-android-app/releases/download/v1.0.0/ITFSAcademy-v1.0.0-release.apk) | Standalone signed & R8-minified APK for production testing & sideloading. |
| **`ITFSAcademy-v1.0.0-debug.apk`** | Debug Signed APK | 14.8 MB | [⬇️ **Download Debug APK**](https://github.com/vishalprajapati2k25/itfs-academy-android-app/releases/download/v1.0.0/ITFSAcademy-v1.0.0-debug.apk) | Debug-signed APK with Logcat diagnostics enabled. |
| **`ITFSAcademy-v1.0.0-release.aab`** | Production AAB | 4.1 MB | [⬇️ **Download AAB**](https://github.com/vishalprajapati2k25/itfs-academy-android-app/releases/download/v1.0.0/ITFSAcademy-v1.0.0-release.aab) | Google Play Store publication bundle (Android 15 / API 35 compliant). |

> 🏷️ **GitHub Release Tag**: [`v1.0.0`](https://github.com/vishalprajapati2k25/itfs-academy-android-app/releases/tag/v1.0.0)

---

## 🔐 Cryptographic Integrity & Checksums

| File | SHA-256 Checksum |
| :--- | :--- |
| `ITFSAcademy-v1.0.0-release.apk` | `aa389dea53409a7469617ef3cb2d30f867bacc560482366e7a5df5472a233f33` |
| `ITFSAcademy-v1.0.0-debug.apk` | `f73837bac18692ad3ba3666a8d720090662663550bb15b04eb6f2d41e03fdddf` |
| `ITFSAcademy-v1.0.0-release.aab` | `08764609136fa354e067af01c0baafbc2516bbdbdf785cd8d94be661b7b4e445` |

---

## 📋 Release Metadata

* **Version Name**: `1.0.0`
* **Version Code**: `1`
* **Target SDK**: `API 35` (Android 15)
* **Privacy Policy URL**: [https://academy.itfreesource.com/apps/itfs-academy/privacy.html](https://academy.itfreesource.com/apps/itfs-academy/privacy.html)

---

## 📲 Installation Instructions

### Via ADB
```bash
adb install -r ITFSAcademy-v1.0.0-release.apk
```

### Direct Sideload on Android
1. Open the [APK Download Link](https://github.com/vishalprajapati2k25/itfs-academy-android-app/releases/download/v1.0.0/ITFSAcademy-v1.0.0-release.apk) on your Android device.
2. Allow "Install unknown apps" if prompted.
3. Tap **Install** and launch **ITFS Academy**.
