# KaamKram (कामक्रम) — Encrypted Backup File Specification

---

## 1. File Specification (`.kaamkram`)

A KaamKram backup file is a binary container holding an authenticated, encrypted snapshot of the entire local SQLite database.

### File Binary Layout

```
+-----------------------------------------------------------------------------------------+
| Offset (bytes) | Field Name       | Size (bytes) | Format / Description                 |
+----------------+------------------+--------------+--------------------------------------+
| 0x00 - 0x0A    | MAGIC_HEADER     | 11           | ASCII "KAAMKRAM_V1"                  |
| 0x0B - 0x2A    | KDF_SALT         | 32           | 256-bit cryptographically random salt|
| 0x2B - 0x36    | GCM_IV           | 12           | 96-bit unique AES-GCM IV             |
| 0x37 - EOF     | ENCRYPTED_PAYLOAD| Variable     | AES-256-GCM ciphertext + 16-byte tag |
+-----------------------------------------------------------------------------------------+
```

---

## 2. Cryptographic Parameters

1. **Magic Header**:
   - String: `"KAAMKRAM_V1"`
   - Used for quick validation and format versioning.
2. **Key Derivation Function (KDF)**:
   - Algorithm: `PBKDF2WithHmacSHA256`
   - Key Length: 256 bits (32 bytes)
   - Iterations: 100,000 iterations
   - Salt: 32 bytes generated via `SecureRandom`
3. **Symmetric Encryption (AEAD)**:
   - Algorithm: `AES/GCM/NoPadding`
   - IV / Nonce: 12 bytes generated via `SecureRandom`
   - Tag Length: 128 bits (16 bytes) appended automatically to ciphertext by Android `Cipher`
   - Additional Authenticated Data: (Included implicitly in GCM block)

---

## 3. Restoration & Safety Invariants
1. **Never Overwrite Live DB Directly**:
   - The ciphertext is decrypted into a temporary staging file: `cacheDir/staging_restore.db`.
2. **Authentication Verification**:
   - If the user enters an incorrect passphrase or if any byte in the file is modified/corrupted, `Cipher.doFinal()` throws `AEADBadTagException`.
   - The staging file is instantly deleted and the existing live database remains completely untouched.
3. **Atomic Replace**:
   - Only after successful decryption and tag validation is the staging file atomically copied over `KaamKramDatabase.DATABASE_NAME`.
