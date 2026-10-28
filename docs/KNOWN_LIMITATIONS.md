# KaamKram (कामक्रम) — Known Limitations & Future Roadmap

---

## 1. Architectural Trade-offs & Limitations

### 1.1. Local-Only Storage
- **Trade-off**: Because KaamKram is strictly local-first and does not upload data to a proprietary cloud server, losing or destroying a phone without having created an encrypted backup (`.kaamkram`) will result in permanent data loss.
- **Mitigation**: Prominent encrypted backup feature with one-tap export to Google Drive / WhatsApp / external SD card.

### 1.2. Single-Device Solo Operation
- **Trade-off**: Designed specifically for individual solo tradespeople and technicians. Real-time multi-technician fleet synchronization is out of scope for the single-user release.
- **Future Direction**: Peer-to-peer Wi-Fi Direct sync or opt-in end-to-end encrypted backup synchronization.

### 1.3. Android PDF Canvas Rendering
- **Trade-off**: `PdfDocument` rendering is performed using standard Android vector drawing (`Canvas`) without heavy third-party HTML/WebKit dependencies. While this produces lightweight, instant PDFs (< 50 KB), multi-page invoice pagination is capped at standard item limits (up to ~15 items per single-page invoice sheet).

---

## 2. Future Roadmap

1. **OCR / Voice Invoice Line Item Input**: Voice input in Hindi/Hinglish to quickly dictate parts and labor items while hands are greasy on site.
2. **Bluetooth Thermal Receipt Printer**: Direct ESC/POS printing over Bluetooth for on-the-spot 2-inch or 3-inch paper receipts.
3. **Regional Indian Language Expansion**: Adding Gujarati, Marathi, Tamil, Telugu, and Bengali translations.
