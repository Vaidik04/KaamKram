# Architecture Decision Records (ADRs)

## ADR-001: Single-Module Architecture for Initial V1
- **Status:** Accepted
- **Context:** Rapid development and seamless compilation across AGP 9.4 and KSP2.
- **Decision:** Keep all core, data, domain, and UI logic within `:app` while maintaining strict package-level clean separation (`core/`, `domain/`, `data/`, `ui/`).
- **Consequences:** Avoids multi-module Gradle configuration overhead; straightforward refactoring into feature modules later if needed.

## ADR-002: Integer Paise (Long) for All Currency Storage
- **Status:** Accepted
- **Context:** Floating-point inaccuracies cause catastrophic accumulation errors in quotes and tax invoices.
- **Decision:** All monetary amounts are stored strictly as 64-bit integer paise (`Long`). 1 INR = 100 paise.
- **Consequences:** Zero representation drift. Rounding is explicit with `RoundingMode.HALF_UP` at paise boundaries.

## ADR-003: Local-First with No Remote Server in V1
- **Status:** Accepted
- **Context:** Indian solo tradespeople frequently work in basements, rural outskirts, and high-rise service shafts with zero network.
- **Decision:** Complete operational autonomy using local SQLite/Room database. No mandatory login, no backend API, no telemetry.
- **Consequences:** Instant performance, absolute user privacy, zero server downtime risk.

## ADR-004: Built-in Android PdfDocument over Heavy Third-Party Libraries
- **Status:** Accepted
- **Context:** 3rd party PDF engines add 15MB+ to APK size and present licensing or native crash risks.
- **Decision:** Use native `android.graphics.pdf.PdfDocument` with Compose/Canvas rendering for PDF quotations and invoices.
- **Consequences:** Ultra-lightweight APK, complete control over visual invoice layout.
