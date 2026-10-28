# KaamKram (कामक्रम)
### The Job-to-Payment OS for Indian Solo Tradespeople

[![Platform](https://img.shields.io/badge/Platform-Android%20(API%2024%2B)-brightgreen.svg)](https://developer.android.com)
[![Architecture](https://img.shields.io/badge/Architecture-MVI%2FClean%20Architecture-blue.svg)]()
[![Database](https://img.shields.io/badge/Database-Room%20(Local--First)-orange.svg)]()
[![License](https://img.shields.io/badge/License-Proprietary-red.svg)]()

KaamKram is a **local-first Android application** crafted specifically for independent Indian field-service professionals (electricians, AC mechanics, plumbers, RO water purifier technicians, CCTV installers, and appliance repairers).

It manages the entire lifecycle of a trade service job from initial customer call to quote, work evidence, change orders, billing, instant UPI reconciliation, and warranty history.

---

## Non-Negotiable Core Principles

1. **Local-First & Resilient:** Works 100% offline. Zero remote server dependency for core functionality.
2. **Integer Paise Financial Calculations:** Every rupee and paise calculation is stored as `Long` (paise). Floats and doubles are strictly forbidden in monetary math.
3. **India-First UX:** Standard Indian number grouping (`₹1,23,456`), Hindi & English localization, UPI deep-linking, GST-ready invoicing, and high-contrast sunlight readability.
4. **Append-Only Material Audit Trail:** Critical job milestones (approval, work proof, price changes, payments) are recorded immutably.
5. **No Placeholder Code:** No mock screens, fake buttons, or silent failures.

---

## Tech Stack & Architecture

- **Language:** Kotlin 2.2.10
- **Build System:** Android Gradle Plugin 9.4.0, Gradle 9.7.1
- **UI Framework:** Jetpack Compose with Material 3 Design
- **Dependency Injection:** Hilt
- **Local Persistence:** Room Database 2.7.2 (with KSP2 & exported schemas)
- **Background Tasks:** AndroidX WorkManager
- **Security:** AndroidX Biometric, AES-GCM encrypted local backups

---

## Directory Structure

```
d:/KaamKram/
├── app/
│   ├── src/main/java/com/kaamkram/app/
│   │   ├── core/money/           # MoneyEngine, INR formatter, Amount-in-words
│   │   ├── data/local/           # Room Database, Entities, DAOs, SeedData
│   │   ├── domain/model/         # Job, Customer, Estimate, Invoice, Payment
│   │   ├── domain/validation/    # Job state machine, input validators
│   │   ├── di/                   # Hilt DI modules
│   │   ├── navigation/           # Compose NavHost & navigation routes
│   │   └── ui/                   # Jetpack Compose UI (Today, Jobs, Customers, Money, More)
│   ├── src/test/                 # Unit tests (MoneyEngine, State Machine)
│   └── src/androidTest/          # Instrumentation tests
├── docs/                         # Engineering, architecture, and compliance docs
└── gradle/                       # Version catalogs and Gradle wrapper
```

---

## Running the App

To run tests:
```powershell
.\gradlew.bat testDebugUnitTest
```

To build debug APK:
```powershell
.\gradlew.bat assembleDebug
```
