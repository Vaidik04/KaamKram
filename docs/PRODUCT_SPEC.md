# KaamKram (कामक्रम) — Product Specification
**Job-to-Payment Operating System for Indian Solo Tradespeople**

---

## 1. Executive Summary & Problem Statement
In India, millions of independent tradespeople (electricians, AC & refrigeration mechanics, plumbers, RO water purifier technicians, and CCTV/appliance installers) run their micro-enterprises across a disorganized combination of phone calls, WhatsApp messages, paper diaries, and mental math.

This leads to:
1. **Uncollected Money & Scope Creep**: Additional parts and labor delivered on-site are frequently disputed during billing because verbal approvals leave no paper trail.
2. **Delayed Invoicing**: Technicians wait hours or days until returning home to hand-write or type bills, delaying cash flow and customer settlements.
3. **Lost Warranty & History Context**: When a customer calls 3 months later complaining of an appliance fault, the technician cannot quickly verify whether the compressor, capacitor, or piping was under warranty.
4. **Cloud SaaS Traps**: Most mobile apps require monthly cloud subscriptions, internet connectivity, or upload private customer phone numbers to unknown servers.

**KaamKram (कामक्रम)** solves this by providing a 100% offline, local-first Android operating system that turns field visits into structured, professional, and audited transactions in seconds.

---

## 2. Target Personas & Use Cases

### Primary Trades Supported
1. **Electricians (विद्युत तकनीशियन)**: House wiring, MCB tripping resolution, inverter/battery installation, fan/light fittings.
2. **AC & Refrigeration Mechanics (एसी एवं प्रशीतन मैकेनिक)**: Split/window AC service, gas charging (R32/R410A), compressor replacement, PCB repair.
3. **Plumbers (प्लंबर)**: Pipe leakages, sanitary fittings, bathroom fixtures, overhead water tank connection.
4. **RO Water Purifier Technicians (आरओ तकनीशियन)**: Membrane replacement, filter service, pump repair, TDS calibration, scheduled filter change intervals.
5. **CCTV & Appliance Technicians (सीसीटीवी एवं उपकरण इंस्टॉलर)**: DVR/NVR setup, camera wiring, microwave/geyser installation.

---

## 3. Core Functional Pillars

### 3.1. Fast Job Creation Flow (< 60 Seconds)
- One-screen job intake.
- Auto-formatted sequential numbering: `JOB-YYYY-XXXX` (e.g., `JOB-2026-0001`).
- Customer selection or instant customer creation with Indian 10-digit mobile number validation.
- Trade category chip selector and date scheduler.

### 3.2. Strict Job State Machine & Audit Trail
Every job advances through rigid lifecycle states:
```
REQUESTED ➔ QUOTED ➔ APPROVED ➔ IN_PROGRESS ➔ COMPLETED ➔ INVOICED ➔ PAID
                                     ↓
                                 CANCELLED
```
- Append-only event store (`JobEvent`) captures timestamps, status transitions, quote revisions, and payments.

### 3.3. Estimate / Quotation Engine
- Pre-seeded trade rate lists (Price Book) for Electricians, AC mechanics, Plumbers, and RO technicians.
- Add custom items, labor, and materials.
- Quantity, unit rate, trade discount percentage, and applicable tax rate.
- Real-time calculation using pure integer paise arithmetic (`MoneyEngine`).
- Formal revision history (`EstimateVersion`) tracking each version and timestamp.

### 3.4. Professional PDF Generation
- Native Android `android.graphics.pdf.PdfDocument` generation (zero external heavy rendering libraries).
- Bilingual amount in words (e.g., "Rupees One Thousand Two Hundred Fifty Only" and "एक हज़ार दो सौ पचास रुपये मात्र").
- Quotation PDF and Formal Tax Invoice PDF with GSTIN, PAN, and UPI payment box.
- Seamless WhatsApp/email sharing via Android `FileProvider`.

### 3.5. On-Site Work Evidence
- Private app-internal storage for Before and After photo evidence.
- Scoped to application storage directory (`Context.filesDir/photos`), inaccessible to third-party gallery snooping.

### 3.6. Payment Reconciliation & UPI Deep Linking
- Record Cash, UPI, and Bank Transfer payments with transaction reference numbers.
- Live balance reconciliation against the invoice.
- Auto-transition from `INVOICED` to `PAID` when outstanding balance reaches zero paise.

### 3.7. Service & Parts Warranties
- Record warranty certificates with explicit start dates, duration in days (30, 90, 180, 365), and warranty terms.
- Daily background verification (`WarrantyReminderWorker`) via AndroidX WorkManager alerting technicians to expiring warranties for AMC follow-ups.

### 3.8. Encrypted Local Backups
- Authenticated AES-256-GCM encryption with PBKDF2 key derivation (100,000 iterations).
- User passphrase protects the entire SQLite database file.
- Shareable `.kaamkram` backup file for export to Google Drive, USB drives, or local storage.
