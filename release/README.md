# ITFS Academy — Release Distribution & Checksums

This directory documents the official production binaries hosted via **GitHub Releases CDN**, complying with our organization's **Zero-Binary Git Standard**.

---

## 📦 Zero-Binary Architecture Notice

In compliance with Google Play Console policies and Git performance best practices:
- **Compiled `.apk` and `.aab` binaries are NOT committed into this Git repository.**
- All binaries are cryptographically signed and hosted directly on **GitHub Releases CDN**.

---

## ⬇️ Official Downloads

| Artifact | Version | File Format | Download Link | Purpose |
|:---|:---:|:---:|:---|:---|
| **Debug APK** | `v1.0.0` (Code 1) | `.apk` | [⬇️ Download Debug APK](https://github.com/vishalprajapati2k25/itfs-academy-android-app/releases/download/v1.0.0/ITFSAcademy-v1.0.0-debug.apk) | Direct testing & developer emulation |
| **Play Store AAB** | `v1.0.0` (Code 1) | `.aab` | [⬇️ Download Play Store AAB](https://github.com/vishalprajapati2k25/itfs-academy-android-app/releases/download/v1.0.0/ITFSAcademy-v1.0.0-release.aab) | Google Play Console production/closed testing |

---

## 🔐 Cryptographic Integrity & Anti-Tamper

Before installing, you may verify SHA-256 integrity:

```bash
sha256sum ITFSAcademy-v1.0.0-debug.apk
```

---

## 📲 Sideloading via ADB

To install directly to a connected Android phone or emulator:

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```
