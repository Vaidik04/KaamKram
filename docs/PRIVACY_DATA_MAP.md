# KaamKram (कामक्रम) — Privacy Data Map & Data Lifecycle

---

## 1. Data Classification

| Data Category | Specific Fields | Storage Location | Retention / Deletion | External Disclosure |
|---|---|---|---|---|
| **Customer PII** | Full name, 10-digit mobile number, address, notes | Local SQLite (`customers` table) | Retained indefinitely locally until user deletes customer | Shared only when user explicitly exports/shares quote or invoice |
| **Financial Data** | Itemized prices, quantities, discount rates, tax rates, payment mode, reference numbers, balances | Local SQLite (`invoices`, `payments`, `estimates`, `estimate_items`) | Retained indefinitely locally until user deletes job | Shared only when technician generates PDF via FileProvider |
| **Business Profile** | Shop name, owner name, UPI ID, GSTIN, trade category, invoice prefix | Local SQLite (`business_profile` table) | Retained locally until edited | Displayed in generated quotes and invoices |
| **Media (Work Evidence)** | Before/After site photos | Internal app storage (`Context.filesDir/photos/`) | Retained with job; deleted upon job deletion | Never synced or indexed by Android MediaStore |
| **Audit Logs** | Timestamps, transition states, event summaries, JSON payloads | Local SQLite (`job_events` table) | Append-only; cascade deleted with parent job | Never transmitted outside the device |

---

## 2. Permissions Declared & Justifications

1. **`android.permission.CAMERA`**:
   - *Rationale*: Allows technicians to photograph damaged equipment (e.g. burnt capacitor, leaking pipe) directly inside the app before beginning work and after finishing work.
   - *Scope*: Hardware camera feature marked `required="false"`. Devices without cameras can import photos from local file picker.
2. **`android.permission.POST_NOTIFICATIONS`**:
   - *Rationale*: Required on Android 13+ (API 33+) for WorkManager to alert the technician about upcoming warranty expirations (7-day window).
3. **`android.permission.VIBRATE`**:
   - *Rationale*: Immediate haptic feedback for field technicians operating under harsh sunlight or noisy work environments when recording a payment or approving a job.

**Zero Internet Permission**:
No `android.permission.INTERNET` is requested. The application cannot communicate over cellular or Wi-Fi data channels.
