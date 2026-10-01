# ITFS Academy — Native Android Application

[![Android CI](https://github.com/vishalprajapati2k25/itfs-academy-android-app/actions/workflows/android-ci.yml/badge.svg)](https://github.com/vishalprajapati2k25/itfs-academy-android-app/actions)
[![Platform](https://img.shields.io/badge/Platform-Android%208.0%2B%20(API%2024%2B)-3DDC84?logo=android&logoColor=white)](https://developer.android.com)
[![Target SDK](https://img.shields.io/badge/Target%20SDK-35%20(Android%2015)-brightgreen?logo=android&logoColor=white)](https://developer.android.com/about/versions/15)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.2.10-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/Jetpack%20Compose-Obsidian%20Theme-4285F4?logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Security Armor](https://img.shields.io/badge/DRM%20Armor-FLAG__SECURE%20%2B%20Anti--Tamper-success?logo=fastapi&logoColor=white)](https://developer.android.com)
[![Edge API](https://img.shields.io/badge/Edge%20API-Cloudflare%20Pages%20v1-F38020?logo=cloudflare&logoColor=white)](https://academy.itfreesource.com/api/v1)
[![Privacy Policy](https://img.shields.io/badge/Privacy%20Policy-Live%20Hosted-blue?logo=googleplay&logoColor=white)](https://academy.itfreesource.com/apps/itfs-academy/privacy.html)

A production-grade, high-performance native Android application engineered for **ITFreeSource Academy**. Built with **100% Jetpack Compose** in a modern **Obsidian Dark developer design system**, the app delivers an elite engineering interview preparation and concept masterclass experience for senior and staff software engineers.

---

## 🏛️ The 3-Pillar Architectural Learning Engine

Engineered from the ground up to prepare developers for senior/staff interviews at top tech companies (Google, Meta, Amazon, Uber, Netflix, DeepMind):

```
┌────────────────────────────────────────────────────────────────────────┐
│                     TOPIC MASTERY ENGINE (3 PILLARS)                   │
├───────────────────┬──────────────────────────┬─────────────────────────┤
│    PILLAR 1       │         PILLAR 2         │        PILLAR 3         │
│  STEP-BY-STEP     │       INTERACTIVE        │    FAANG LONG ANSWER    │
│  CORE CONCEPTS    │       DRM MCQS           │   INTERVIEW SIMULATOR   │
├───────────────────┼──────────────────────────┼─────────────────────────┤
│ • Mental models   │ • Fast verification     │ • Real FAANG prompts    │
│ • Execution flow  │ • Distractor rationale   │ • Key talking points    │
│ • Low-level code  │ • SHA-256 DRM hash checks│ • Revealable answers    │
│ • Key takeaways   │ • Immediate review loop  │ • Follow-up questions   │
└───────────────────┴──────────────────────────┴─────────────────────────┘
```

### 1. 📖 Step-by-Step Core Concept Breakdown
- Deconstructs complex engineering internals (CPython VM, PyMalloc, AST compilation, Agentic Tool Use, Distributed Transactions) into bite-sized sequential cards.
- Embedded high-contrast dark code blocks with screen-protected DRM watermarks.
- Checkpoint persistence so learners never lose progress.

### 2. 🎯 Concept Verification MCQs
- Instant comprehension checks following each concept phase.
- Distractor rationale explaining *why* wrong choices fail in production.
- Cryptographically validated using SHA-256 digests (`correctAnswerHash`), preventing plaintext extraction from APK memory dumps.

### 3. 💼 FAANG Long Answer Interview Simulator
- Full-length L5/L6 interview questions modeled after Google, Meta, and Amazon engineering rounds.
- **Key Talking Points**: Bulleted mental anchors to mention to your interviewer.
- **Revealable Model Answer**: Comprehensive architectural breakdown with trade-off analysis and algorithmic complexities.
- **Production Code Solution**: Idiomatic, syntax-highlighted solutions.
- **Interviewer Follow-ups**: Realistic edge cases and scalability questions asked during onsite rounds.

---

## 🎨 Developer-Centric Obsidian Dark UI

- **Obsidian Dark Palette**: `#0B0F17` background with `#111827` card elevations, Indigo `#6366F1` accents, and Emerald `#10B981` indicators.
- **Interview Readiness Telemetry**: Real-time readiness score (%) calculated from concepts completed, MCQs solved, and long-answer questions reviewed.
- **Company & Role Badging**: Instant filtering by target employer (`Google`, `Meta`, `Amazon`, `Uber`, `Netflix`) and level (`L5 Senior`, `L6 Staff`).
- **Interactive Security Shield**: Real-time dialog displaying active DRM protections, tamper check status, and live Edge API connectivity latency.

---

## 🛡️ Content Armor & Anti-Recompilation Security

Engineered to protect proprietary question banks and intellectual property:

| Security Layer | Mechanism | Protection Result |
|---|---|---|
| **Anti-Screen Scraping** | `WindowManager.LayoutParams.FLAG_SECURE` | Hardware screenshots are blocked, screen recordings render blank black frames, and task-switcher previews are blacked out. |
| **Zero Hardcoded Questions** | Decoupled REST Micro-APIs | The APK binary contains **zero plaintext questions or answers**. Curriculum nodes are fetched dynamically over Cloudflare Edge HTTPS APIs. |
| **SHA-256 DRM Digestion** | Cryptographic Hashed Answers | The client and Edge API evaluate answers using SHA-256 digests (`correctAnswerHash`). Memory dumps and MITM proxies reveal no plaintext answers. |
| **Anti-Recompilation & Tamper** | `AntiTamperEngine` | Runtime package signature verification, active debugger detection (`Debug.isDebuggerConnected()`), and Frida/Xposed hooking checks. |
| **Encrypted Local Vault** | `EncryptedDataStore` | Device-bound AES-CBC encrypted persistence caches offline progress with hardware salt. |
| **Aggressive Code Armor** | ProGuard / R8 | Strips line numbers, source file names, collapses package hierarchies, and minifies release APK to just ~2.2 MB. |

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
│   • 3-Pillar Learning Engine      │   │   • Markdown Curriculum Bank (/content/)      │
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

| Artifact | Version | Format | Size | Target / Use Case | Download Link |
| :--- | :---: | :---: | :---: | :--- | :--- |
| **Release APK** | `v1.0.0` (Code 1) | `.apk` | 2.2 MB | Direct Android sideloading & testing | [⬇️ Download Release APK](https://github.com/vishalprajapati2k25/itfs-academy-android-app/releases/download/v1.0.0/ITFSAcademy-v1.0.0-release.apk) |
| **Play Store AAB** | `v1.0.0` (Code 1) | `.aab` | 4.1 MB | Google Play Console upload | [⬇️ Download Play Store AAB](https://github.com/vishalprajapati2k25/itfs-academy-android-app/releases/download/v1.0.0/ITFSAcademy-v1.0.0-release.aab) |

See [release/README.md](./release/README.md) for cryptographic SHA-256 fingerprints and verification guides.

---

## 🛠️ Development & Build Setup

### Prerequisites
* JDK 17 (`JAVA_HOME=/path/to/jdk-17`)
* Android SDK (API 35 platform, Build-Tools 35.0.0)

### Building Locally

```bash
# Grant execution permissions
chmod +x gradlew

# Build Debug APK
JAVA_HOME=~/.jdk-17 ./gradlew assembleDebug

# Build Production Release APK (Minified with R8)
JAVA_HOME=~/.jdk-17 ./gradlew assembleRelease

# Build Google Play App Bundle (AAB)
JAVA_HOME=~/.jdk-17 ./gradlew bundleRelease
```

---

## 📄 License

Distributed under the MIT License. See [LICENSE](./LICENSE) for details.
