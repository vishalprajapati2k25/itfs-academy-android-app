# AGENTS.md — Operational Protocol for ITFS Academy Android App

This document governs agentic and human pair-programming operations within the **ITFreeSource Academy Android Native Client** repository.

---

## 1. Repository Identity & Inter-Repo Topology

| Component | Target / Value |
|---|---|
| **Android App Repository** | `https://github.com/vishalprajapati2k25/itfs-academy-android-app` |
| **Academy Web & Cloudflare Backend** | `https://github.com/vishalprajapati2k25/academy-itfreesource` |
| **Live API Gateway** | `https://academy.itfreesource.com/api/v1` |
| **Live Privacy Policy** | `https://academy.itfreesource.com/apps/itfs-academy/privacy.html` |
| **Primary Maintainer** | Vishal Prajapati (`vishalprajapati2k25`) |

---

## 2. Core Operational Rules (Non-Negotiable)

1. **Zero-Binary Git Tree Standard (Strict Rule)**:
   - NEVER commit `.apk`, `.aab`, `.idsig`, `.jks`, or `.keystore` binaries into the Git tree.
   - All binaries are distributed via GitHub Releases CDN and documented in `release/README.md`.
2. **Screen-Scraping Protection (DRM & Content Armor)**:
   - `FLAG_SECURE` must remain active on `MainActivity` and quiz screens (`SecurityManager.applyScreenshotProtection`).
   - Hardware screenshots, screencasting, and recent-app previews are strictly blocked to protect question banks.
3. **Anti-Recompilation & Tamper Shield**:
   - `AntiTamperEngine` verifies APK signature hashes and guards against active debuggers, Frida, and Xposed hooks.
   - Questions are fetched dynamically over authenticated REST APIs and never stored as plaintext strings in the APK.
4. **Decoupled Section-Based Micro-APIs**:
   - Every course, section, and lesson has distinct identifiers. Changing a question on the backend must never break other sections.
   - Local offline fallback and device-bound encrypted caching (`EncryptedDataStore`) ensure uninterrupted learning.
5. **Dark & Light Mode Parity**:
   - All Jetpack Compose screens must maintain WCAG AAA contrast in both Light (`#FFFFFF`, `#F7F7F7`) and Dark (`#131F24`, `#1B272C`) modes.

---

## 3. Directory Layout

```
itfs-academy-android-app/
├── .github/workflows/
│   └── android-ci.yml                 # GitHub Actions CI for build and linting
├── app/
│   ├── build.gradle                   # Target SDK 35, Jetpack Compose, R8 optimization
│   ├── proguard-rules.pro             # Anti-decompilation & symbol scrambling rules
│   └── src/main/
│       ├── AndroidManifest.xml        # Hardened manifest (allowBackup=false, FLAG_SECURE)
│       └── java/com/itfreesource/academy/
│           ├── MainActivity.kt        # Compose entrypoint with FLAG_SECURE & bottom nav
│           ├── security/
│           │   ├── SecurityManager.kt     # Screen-capture & task-switcher protection
│           │   ├── AntiTamperEngine.kt    # Signature check, root/Frida/debugger detector
│           │   └── EncryptedDataStore.kt # Device-bound AES encrypted vault
│           ├── data/
│           │   ├── model/                 # Course, Curriculum, Question, UserProgress
│           │   ├── api/AcademyApiClient.kt # Edge REST API client with offline fallback
│           │   └── repository/            # Reactive StateFlow repository
│           └── ui/
│               ├── theme/                 # Duolingo-style colors, shapes, typography
│               ├── components/            # 3D tactile buttons, QuestPathNode, TopStatusBar
│               └── screens/               # Learn, QuizPlay, Courses, Leaderboard, Profile
├── store-assets/                      # Google Play 512x512 icon & 1024x500 banner
└── release/                           # CDN release download links & cryptographic checksums
```

---

## 4. Change Ledger

| Date | Author | Summary |
|---|---|---|
| 2026-10-01 | Antigravity AI | Initial production setup: Target SDK 35, Duolingo gameplay engine, FLAG_SECURE armor, decoupled Edge APIs, Play Store compliance |
