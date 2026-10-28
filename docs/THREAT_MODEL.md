# KaamKram (कामक्रम) — Threat Model (STRIDE Methodology)

---

## 1. System Overview & Trust Boundaries
KaamKram operates strictly within an isolated local Android sandbox. There is **no outbound client-server channel**; no telemetry, analytics SDKs, or cloud storage endpoints exist within the codebase.

### Primary Assets
1. **Customer Records**: Full names, 10-digit telephone numbers, and street/building addresses.
2. **Financial Records**: Quotations, line items, discounts, tax calculations, invoices, cash/UPI payment history, and business bank/UPI IDs.
3. **Work Evidence**: Job site photographic evidence stored in private app-internal storage.
4. **Encrypted Backups**: Encrypted SQLite exports shared via Android system intent.

---

## 2. Threat Analysis (STRIDE)

| Threat Category | Potential Attack Vector | KaamKram Countermeasure & Architectural Defense |
|---|---|---|
| **Spoofing (Identity)** | Unauthorized user picking up unlocked technician device | Device biometric authentication integration (`androidx.biometric`) prompt support. |
| **Tampering (Data)** | Attacker modifying SQLite database files on disk or tampering with export | 1. Direct cleartext disk access blocked via Android private directory sandbox.<br>2. Room database schema version verification.<br>3. Backups protected via authenticated AES-256-GCM encryption with 128-bit authentication tag. Any tampered ciphertext causes `AEADBadTagException` and aborts restore. |
| **Repudiation** | Customer denies verbal quote approval or scope extension | Strict append-only `JobEvent` audit log recording every quote revision, approval timestamp, work photo, and payment transaction. |
| **Information Disclosure** | 1. Network sniffing / MITM.<br>2. Third-party gallery apps reading private customer photos.<br>3. Auto-backup leaking SQLite DB to Google Cloud unencrypted. | 1. Zero `android.permission.INTERNET` declared; `network_security_config.xml` blocks all cleartext traffic.<br>2. Photos written exclusively to internal `filesDir/photos` using private app UID; never indexed by `MediaStore`.<br>3. `android:allowBackup="false"` in `AndroidManifest.xml` and explicit exclusion rules in `backup_rules.xml`. |
| **Denial of Service** | App crash due to malformed or corrupted SQLite file | Staging file restoration in `BackupManager`. Backups are validated, parsed, and verified in `cacheDir/staging_restore.db` before atomically replacing the live production database. |
| **Elevation of Privilege** | Malicious third-party apps reading shared invoice/quote PDFs | `FileProvider` configured with granular `FLAG_GRANT_READ_URI_PERMISSION` targeting only the specific Chooser intent. Root filesystem paths are never exposed. |

---

## 3. Cryptographic Controls
- **Algorithm**: AES-256-GCM (`AES/GCM/NoPadding`).
- **Key Derivation**: PBKDF2 with HMAC-SHA256 (`PBKDF2WithHmacSHA256`).
- **Salt**: 256-bit cryptographically secure random (`SecureRandom`).
- **IV / Nonce**: 96-bit unique IV generated per backup.
- **Iterations**: 100,000 iterations for resistance against brute-force attacks.
- **Integrity**: 128-bit GCM authentication tag guarantees tamper detection.
