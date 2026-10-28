# KaamKram (कामक्रम) — Google Play Store Release Checklist

---

## 1. Compliance & Security Verification

- [x] **No Hardcoded Secrets or Passwords**: Zero credentials or private keys in source control.
- [x] **Local-First / No Remote Servers**: Zero unencrypted data transmission; cleartext network traffic blocked in `network_security_config.xml`.
- [x] **Permission Minimization**:
  - `CAMERA`: Marked `android:required="false"`.
  - `POST_NOTIFICATIONS`: Requested only on Android 13+ (API 33+) for warranty expiry reminders.
  - `VIBRATE`: Declared for tactile haptic feedback.
  - No location tracking, no contacts snooping, no SMS reading.
- [x] **Scoped Storage & FileProvider**:
  - Direct external storage access avoided.
  - Documents and images shared securely via `androidx.core.content.FileProvider`.
- [x] **Target SDK**:
  - `compileSdk = 36`, `targetSdk = 36` (Android 16 compatible).
  - `minSdk = 24` (Supports Android 7.0+, covering >97% of active Indian Android devices).

---

## 2. Release Build & Signing Guide

### Generating Release App Bundle (AAB)
```bash
# 1. Ensure release keystore is stored outside source repository
export KEYSTORE_FILE="/path/to/secure/release-keystore.jks"
export KEY_ALIAS="kaamkram"

# 2. Build Release Android App Bundle
./gradlew bundleRelease
```

### Proguard / R8 Verification
- `app/proguard-rules.pro` contains rules for:
  - Kotlinx Serialization
  - Room Entities & DAOs
  - Dagger Hilt injection markers
  - WorkManager Worker reflections

---

## 3. Store Listing Assets (India-First)
- **App Name**: KaamKram — Technician Job & Invoice OS (कामक्रम)
- **Short Description**: Manage repair jobs, estimates, bills, GST invoices, and payments offline.
- **Categories**: Productivity / Business
- **Language Support**: English and Hindi (हिन्दी) native strings.
