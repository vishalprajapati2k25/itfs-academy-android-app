# ITFS Academy — Native Android Application

[![Android CI](https://github.com/vishalprajapati2k25/itfs-academy-android-app/actions/workflows/android-ci.yml/badge.svg)](https://github.com/vishalprajapati2k25/itfs-academy-android-app/actions)
[![Platform](https://img.shields.io/badge/Platform-Android%208.0%2B%20(API%2024%2B)-3DDC84?logo=android&logoColor=white)](https://developer.android.com)
[![Target SDK](https://img.shields.io/badge/Target%20SDK-35%20(Android%2015)-brightgreen?logo=android&logoColor=white)](https://developer.android.com/about/versions/15)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.2.10-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/Jetpack%20Compose-Modern%20UI-4285F4?logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Security Armor](https://img.shields.io/badge/DRM%20Armor-FLAG__SECURE%20%2B%20Anti--Tamper-success?logo=fastapi&logoColor=white)](https://developer.android.com)
[![Edge API](https://img.shields.io/badge/Edge%20API-Cloudflare%20Pages%20v1-F38020?logo=cloudflare&logoColor=white)](https://academy.itfreesource.com/api/v1)
[![Privacy Policy](https://img.shields.io/badge/Privacy%20Policy-Live%20Hosted-blue?logo=googleplay&logoColor=white)](https://academy.itfreesource.com/apps/itfs-academy/privacy.html)

A production-grade, highly engaging native Android application engineered for **ITFreeSource Academy**. Built with **100% Jetpack Compose**, this app brings **Duolingo-style gamified learning** to professional engineering subjects: from Agentic AI and Application Security to Modern Playwright Testing, Python Internals, and Distributed Systems.

---

## 🎮 Gamified Duolingo-Style Play Experience

Students master complex software concepts through addictive micro-drills:

* **🔥 Daily Streaks**: Build daily learning momentum with streak freezes, flame animations, and reminder shields.
* **💎 Brain Gems & Hearts Economy**: Start with 5 Hearts. Errors deduct 1 Heart; refill with Brain Gems or practice drills.
* **🗺️ Winding Quest Path**: Journey down a sinusoidal visual learning trail with interactive 3D tactile nodes:
  - ⭐ **Core Quest**: Foundational concept drill cards.
  - ⚡ **Speed Blitz**: Rapid-fire timer attack.
  - 🎁 **Mystery Chest**: Surprise Brain Gem and XP bounty.
  - 👑 **Boss Exam**: Unit capstone mastery test to earn golden crowns.
* **⚡ Interactive Tactile Mechanics**:
  - *Instant Feedback*: Bouncy haptic-style visual sheet (Green celebration or Red diagnostic explanation).
  - *Code Scramble*: Arrange randomized syntax chips in valid execution order.
  - *Binary Concept Blitz*: True/False rapid evaluations.
* **🏆 Academy Leagues**: Compete in Diamond and Obsidian leagues with other engineers.
* **📚 Course Subscription Hub**: Browse 11+ Academy curricula and subscribe with 1 tap.

---

## 🛡️ Content Armor & Anti-Recompilation Security

Engineered to protect proprietary question banks and intellectual property:

| Security Layer | Mechanism | Protection Result |
|---|---|---|
| **Anti-Screen Scraping** | `WindowManager.LayoutParams.FLAG_SECURE` | Hardware screenshots are blocked, screen recordings render blank black frames, and task-switcher previews are blacked out. |
| **Zero Hardcoded Questions** | Decoupled REST Micro-APIs | The APK binary contains **zero plaintext questions or answers**. Quiz nodes are fetched dynamically over Edge HTTPS APIs. |
| **SHA-256 DRM Digestion** | Cryptographic Hashed Answers | The client and Edge API evaluate answers using SHA-256 digests (`correctAnswerHash`). Memory dumps and MITM proxies reveal no plaintext answers. |
| **Anti-Recompilation & Tamper** | `AntiTamperEngine` | Runtime package signature verification, active debugger detection (`Debug.isDebuggerConnected()`), and Frida/Xposed hooking checks. |
| **Encrypted Local Vault** | `EncryptedDataStore` | Device-bound AES-CBC encrypted persistence caches offline progress with hardware salt. |
| **Aggressive Code Armor** | ProGuard / R8 | Strips line numbers, source file names, collapses package hierarchies, and removes all debug telemetry. |

---

## 🌐 Connected Ecosystem & Architecture

```
┌────────────────────────────────────────────────────────────────────────┐
│                   ITFreeSource Academy Ecosystem                       │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │
        ┌───────────────────────────┴───────────────────────────┐
        ▼                                                       ▼
┌───────────────────────────────────┐   ┌───────────────────────────────────────────────┐
│   vishalprajapati2k25/            │   │   vishalprajapati2k25/                        │
│   itfs-academy-android-app        │   │   academy-itfreesource                        │
│   (This Repository)               │   │   (Web Portal & Edge Serverless)              │
│   • 100% Jetpack Compose Native   │   │   • Cloudflare Pages Functions (/api/v1/)     │
│   • Duolingo Gamification Engine  │   │   • Markdown Curriculum Bank (/content/)      │
│   • FLAG_SECURE Anti-Scraping     │   │   • Live Privacy Policy Hosting               │
│   • Decoupled Micro-API Client    │   │   • Astro 5 + Tailwind Responsive UI          │
└─────────────────┬─────────────────┘   └───────────────────────┬───────────────────────┘
                  │                                             │
                  │             HTTPS / REST + JSON             │
                  └──────────────────► ◄────────────────────────┘
                                     │
                     ┌───────────────┴───────────────┐
                     ▼                               ▼
     ┌───────────────────────────────┐   ┌───────────────────────────────┐
     │      Live Production API      │   │      Live Privacy Policy      │
     │  academy.itfreesource.com     │   │  academy.itfreesource.com     │
     │  /api/v1/courses              │   │  /apps/itfs-academy/          │
     │  /api/v1/curriculum           │   │  privacy.html                 │
     └───────────────────────────────┘   └───────────────────────────────┘
```

---

## 📥 Direct Downloads & Releases

This repository strictly enforces the **Zero-Binary Git Standard**. Binaries are hosted via GitHub Releases CDN:

| Artifact | Version | Format | Target / Use Case | Download Link |
| :--- | :---: | :---: | :--- | :--- |
| **Debug APK** | `v1.0.0` (Code 1) | `.apk` (15 MB) | Direct Android sideloading & testing | [⬇️ Download Debug APK](https://github.com/vishalprajapati2k25/itfs-academy-android-app/releases/download/v1.0.0/ITFSAcademy-v1.0.0-debug.apk) |
| **Play Store AAB** | `v1.0.0` (Code 1) | `.aab` | Google Play Console upload | [⬇️ Download Play Store AAB](https://github.com/vishalprajapati2k25/itfs-academy-android-app/releases/download/v1.0.0/ITFSAcademy-v1.0.0-release.aab) |

See [release/README.md](./release/README.md) for cryptographic SHA-256 fingerprints and verification guides.

---

## 🛠️ Development & Build Setup

### Prerequisites
* JDK 17 (Eclipse Adoptium Temurin 17 recommended)
* Android SDK (API 35 platform, Build-Tools 35.0.0)

### Building Locally

```bash
# Grant execution permissions
chmod +x gradlew

# Run Android Lint
JAVA_HOME=~/.jdk-17 ./gradlew lintDebug

# Build Debug APK
JAVA_HOME=~/.jdk-17 ./gradlew assembleDebug

# Build Release APK (requires release.jks in local.properties)
JAVA_HOME=~/.jdk-17 ./gradlew assembleRelease
```

---

## 📄 License

Distributed under the MIT License. See [LICENSE](./LICENSE) for details.
