package com.kaamkram.app.core.money

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import java.math.BigDecimal

class MoneyEngineTest {

    @Test
    fun testRupeesToPaiseConversion() {
        assertEquals(0L, MoneyEngine.rupeesToPaise("0"))
        assertEquals(100L, MoneyEngine.rupeesToPaise("1"))
        assertEquals(15050L, MoneyEngine.rupeesToPaise("150.50"))
        assertEquals(12345678L, MoneyEngine.rupeesToPaise("1,23,456.78"))
        assertEquals(99L, MoneyEngine.rupeesToPaise("0.99"))
        assertEquals(0L, MoneyEngine.rupeesToPaise(""))
    }

    @Test
    fun testLineItemCalculationWithFractionalQuantity() {
        // 2.5 hours of AC servicing at ₹450 (45000 paise)
        val ratePaise = 45000L
        val qty = BigDecimal("2.5")
        val total = MoneyEngine.calculateLineItemAmount(ratePaise, qty)
        assertEquals(112500L, total) // ₹1,125.00

        // 3.33 units at ₹10 (1000 paise) -> 3330 paise (half-up rounding)
        val fractional = MoneyEngine.calculateLineItemAmount(1000L, BigDecimal("3.33"))
        assertEquals(3330L, fractional)
    }

    @Test
    fun testDiscountCalculation() {
        val totalPaise = 100000L // ₹1,000

        // 0% discount
        assertEquals(0L, MoneyEngine.calculateDiscount(totalPaise, 0))

        // 10% discount (1000 bps)
        assertEquals(10000L, MoneyEngine.calculateDiscount(totalPaise, 1000))

        // 100% discount (10000 bps)
        assertEquals(100000L, MoneyEngine.calculateDiscount(totalPaise, 10000))

        // Invalid discount bps
        assertThrows(IllegalArgumentException::class.java) {
            MoneyEngine.calculateDiscount(totalPaise, 10001)
        }
    }

    @Test
    fun testTaxCalculation() {
        val taxablePaise = 100000L // ₹1,000

        // 18% GST (1800 bps) -> ₹180
        assertEquals(18000L, MoneyEngine.calculateTax(taxablePaise, 1800))

        // 5% GST (500 bps) -> ₹50
        assertEquals(5000L, MoneyEngine.calculateTax(taxablePaise, 500))

        // 0% tax -> ₹0
        assertEquals(0L, MoneyEngine.calculateTax(taxablePaise, 0))
    }

    @Test
    fun testTotalsBreakdownComputation() {
        // Line items: ₹500 (50000 paise), ₹1,500 (150000 paise) -> Subtotal = ₹2,000 (200000 paise)
        val items = listOf(50000L, 150000L)
        // 10% discount (1000 bps) -> ₹200 discount, Taxable = ₹1,800 (180000 paise)
        // 18% GST (1800 bps) -> ₹324 tax (32400 paise)
        // Grand total = ₹2,124 (212400 paise)
        val totals = MoneyEngine.computeTotals(items, discountBps = 1000, taxRateBps = 1800)

        assertEquals(200000L, totals.subtotalPaise)
        assertEquals(20000L, totals.discountPaise)
        assertEquals(180000L, totals.taxableAmountPaise)
        assertEquals(32400L, totals.taxPaise)
        assertEquals(212400L, totals.grandTotalPaise)
    }

    @Test
    fun testSafeArithmeticOverflowCheck() {
        assertThrows(ArithmeticException::class.java) {
            MoneyEngine.add(Long.MAX_VALUE, 1L)
        }
        assertThrows(ArithmeticException::class.java) {
            MoneyEngine.subtract(Long.MIN_VALUE, 1L)
        }
    }

    @Test
    fun testIndianNumberFormatting() {
        assertEquals("0", MoneyFormatter.formatIndianGrouping(0))
        assertEquals("500", MoneyFormatter.formatIndianGrouping(500))
        assertEquals("1,000", MoneyFormatter.formatIndianGrouping(1000))
        assertEquals("12,345", MoneyFormatter.formatIndianGrouping(12345))
        assertEquals("1,23,456", MoneyFormatter.formatIndianGrouping(123456))
        assertEquals("12,34,567", MoneyFormatter.formatIndianGrouping(1234567))
        assertEquals("1,23,45,678", MoneyFormatter.formatIndianGrouping(12345678))

        // Full paise formatting with symbol
        assertEquals("₹1,234", MoneyFormatter.formatPaise(123400L, includeSymbol = true, showPaiseIfZero = false))
        assertEquals("₹1,234.50", MoneyFormatter.formatPaise(123450L, includeSymbol = true))
        assertEquals("₹0", MoneyFormatter.formatPaise(0L, includeSymbol = true, showPaiseIfZero = false))
        assertEquals("-₹500", MoneyFormatter.formatPaise(-50000L, includeSymbol = true, showPaiseIfZero = false))
    }

    @Test
    fun testAmountInWordsEnglish() {
        assertEquals("Zero Rupees Only", AmountInWords.toWordsEnglish(0L))
        assertEquals("Five Hundred Rupees Only", AmountInWords.toWordsEnglish(50000L))
        assertEquals("One Thousand Two Hundred Thirty Four Rupees Only", AmountInWords.toWordsEnglish(123400L))
        assertEquals("One Lakh Fifty Thousand Rupees Only", AmountInWords.toWordsEnglish(15000000L))
        assertEquals("One Thousand Two Hundred Thirty Four Rupees and Fifty Paise Only", AmountInWords.toWordsEnglish(123450L))
    }

    @Test
    fun testAmountInWordsHindi() {
        assertEquals("शून्य रुपये मात्र", AmountInWords.toWordsHindi(0L))
        assertEquals("पाँच सौ रुपये मात्र", AmountInWords.toWordsHindi(50000L))
        assertEquals("एक हज़ार दो सौ रुपये मात्र", AmountInWords.toWordsHindi(120000L))
        assertEquals("एक लाख पचास हज़ार रुपये मात्र", AmountInWords.toWordsHindi(15000000L))
        assertEquals("पाँच सौ रुपये और पचास पैसे मात्र", AmountInWords.toWordsHindi(50050L))
    }
}
