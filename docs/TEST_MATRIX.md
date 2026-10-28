# KaamKram (कामक्रम) — Verification & Test Matrix

---

## 1. Test Overview

| Test Suite / Area | Execution Target | Verification Type | Status |
|---|---|---|---|
| **`MoneyEngineTest`** | Unit Test (JVM) | Exact paise math, half-up rounding, basis-point discounts & taxes, overflow bounds | **PASSED** |
| **`JobStateMachineTest`** | Unit Test (JVM) | State transitions (Valid flow, invalid skips, cancellation constraints) | **PASSED** |
| **`EstimateCalculationTest`** | Unit Test (JVM) | Multi-item price calculation, percentage discounts, 18% GST calculation | **PASSED** |
| **`BackupManagerTest`** | Unit Test (JVM) | PBKDF2 key derivation, AES-256-GCM encryption, decryption, incorrect passphrase rejection (`AEADBadTagException`) | **PASSED** |
| **`assembleDebug`** | Gradle Build | Complete compilation, desugaring, Proguard/Dexing, resource packaging | **PASSED** |

---

## 2. Test Cases Executed

### 2.1. Money Engine Tests
- **`formatRupeesAndPaise_standardValues`**: Verifies exact decimal representation without floating-point errors.
- **`formatPaise_indianGrouping`**: Verifies INR formatting (`₹1,23,456.78`).
- **`calculateDiscount_halfUpRounding`**: Verifies 10.5% discount on ₹1,000.00 yields ₹105.00.
- **`calculateTax_gst18Percent`**: Verifies 18% GST on ₹1,000.00 yields ₹180.00.
- **`preventNegativeValues`**: Verifies attempts to produce negative values clamp safely or throw `IllegalArgumentException`.

### 2.2. Job State Machine Tests
- **`normalLifecycle_allStepsValid`**:
  `REQUESTED` ➔ `QUOTED` ➔ `APPROVED` ➔ `IN_PROGRESS` ➔ `COMPLETED` ➔ `INVOICED` ➔ `PAID`.
- **`invalidSkip_rejected`**:
  `REQUESTED` directly to `PAID` throws `IllegalStateException`.
- **`cancellationRules`**:
  Jobs can be cancelled from `REQUESTED`, `QUOTED`, `APPROVED`, or `IN_PROGRESS`, but not after `COMPLETED` or `PAID`.

### 2.3. Encrypted Backup Tests
- **`encryptAndDecrypt_validPassphrase`**: Verifies byte-level integrity matching original input.
- **`decrypt_wrongPassphrase_fails`**: Verifies that an attacker attempting decryption with an incorrect passphrase fails immediately with an AEAD authentication error.
- **`headerTamper_fails`**: Verifies that modifying the "KAAMKRAM_V1" header rejects restoration.

---

## 3. Manual Device Acceptance Gates
- **Outdoor Sunlight Readability**: High-contrast Material 3 theme tested under intense direct light.
- **Bilingual Interface**: Verified English and Hindi strings across UI cards, buttons, dialogs, and generated PDF reports.
- **Battery & Memory Profile**: Zero persistent background battery drain; WorkManager executes only a low-power daily periodic check.
