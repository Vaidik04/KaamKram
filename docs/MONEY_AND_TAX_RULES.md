# KaamKram (कामक्रम) — Money & Tax Calculation Rules

---

## 1. Golden Rules of Financial Arithmetic
1. **Never use Floating-Point Types (`Float` or `Double`)**: All internal computations and persistent storage use 64-bit integer paise (`Long`). 1 INR = 100 paise.
2. **Rounding Mode**: `RoundingMode.HALF_UP` (standard commercial and banking rounding in India). Half a paisa (0.5) rounds away from zero.
3. **Rates & Discounts**: Discount percentages and tax percentages are converted to Basis Points ($1\% = 100 \text{ bps}$, so $18\% = 1800 \text{ bps}$, scale of $10,000$).
4. **Non-Negative Invariants**: Money amounts, line item prices, quantities, and totals are validated to be non-negative.

---

## 2. Line Item & Invoice Formulas

### 2.1. Line Item Line Total
$$\text{LineTotal} = \text{UnitPricePaise} \times \text{Quantity}$$

### 2.2. Subtotal
$$\text{SubtotalPaise} = \sum_{i=1}^{N} \text{LineTotal}_i$$

### 2.3. Trade Discount
Given discount percentage $D$ (e.g. $5.0\%$):
$$\text{DiscountBps} = \text{round}(D \times 100)$$
$$\text{DiscountAmountPaise} = \frac{\text{SubtotalPaise} \times \text{DiscountBps} + 5000}{10000}$$
$$\text{TaxableAmountPaise} = \max(0, \text{SubtotalPaise} - \text{DiscountAmountPaise})$$

### 2.4. Goods and Services Tax (GST)
Given GST percentage $T$ (e.g. $18.0\%$):
$$\text{TaxBps} = \text{round}(T \times 100)$$
$$\text{TaxAmountPaise} = \frac{\text{TaxableAmountPaise} \times \text{TaxBps} + 5000}{10000}$$

If intra-state (same state transaction):
$$\text{CGST} = \text{SGST} = \frac{\text{TaxAmountPaise}}{2}$$

$$\text{GrandTotalPaise} = \text{TaxableAmountPaise} + \text{TaxAmountPaise}$$

### 2.5. Balance Due & Status Settlement
$$\text{BalanceDuePaise} = \max(0, \text{GrandTotalPaise} - \sum \text{PaymentsPaise})$$
When $\text{BalanceDuePaise} == 0$, the invoice is fully settled and the job status transitions to `PAID`.

---

## 3. Indian Rupee Formatting
- Currency symbol: `₹`
- Number grouping follows the Indian numbering system ($12,34,567.89$ instead of Western $1,234,567.89$):
  - Last three digits grouped together: hundreds.
  - Preceding digits grouped in pairs of two: thousands, lakhs, crores.
- Amount in words supported in both **English** (e.g. *"Rupees Twelve Lakh Thirty-Four Thousand Five Hundred Sixty-Seven and Eighty-Nine Paise Only"*) and **Hindi** (e.g. *"बारह लाख चौंतीस हज़ार पाँच सौ सड़सठ रुपये और नवासी पैसे मात्र"*).
