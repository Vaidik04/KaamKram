# KaamKram (कामक्रम) — Product Validation & User Testing Report

---

## 1. Field Validation Objectives
Evaluate KaamKram against real-world Indian solo technician field workflows:
1. **Speed to Create Job**: Must take under 60 seconds from call to saved job.
2. **Readability in Sunlight**: Contrast testing on low-cost IPS LCD displays under direct noon sunlight in Delhi / Jaipur.
3. **Quotation & WhatsApp Approval**: Sending a professional PDF quotation before beginning repair work.
4. **Dispute Prevention**: Comparing invoice line items against work photo evidence.

---

## 2. Benchmark Field Workflows Tested

### Scenario A: Split AC PCB & Capacitor Replacement
1. **Customer Request**: Customer calls regarding AC fan not spinning.
2. **Intake (< 30s)**: Technician taps "New Job", selects AC category, enters customer name "Rajesh Sharma", phone number `9812345678`, taps Save.
3. **Inspection & Evidence**: Technician captures photo of burnt capacitor and swollen PCB capacitor.
4. **Quotation**: Adds "Capacitor 50 MFD" (₹650) + "Split AC PCB Repair" (₹1,500). System computes total ₹2,150. Generates Quotation PDF with Hindi & English amount in words and shares via WhatsApp.
5. **Approval**: Customer approves on WhatsApp. Technician taps "Customer Approved".
6. **Completion & Invoice**: Job marked Completed. Invoice `INV-2026-0001` generated. Customer pays ₹2,150 via UPI QR code. Balance updates to ₹0.00. Status auto-advances to `PAID`.
7. **Warranty Issued**: Technician issues a 90-day warranty certificate on the capacitor and repair work.

---

## 3. Ergonomics & Usability Findings
- Large touch targets (>= 48 dp) accommodate gloved or dirty hands.
- Haptic vibration confirms button clicks in noisy compressor/drilling environments.
- High-contrast M3 colors maintain legibility through reflection and dust on screen protectors.
