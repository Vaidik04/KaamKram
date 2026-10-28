package com.kaamkram.app.data.repository

import com.kaamkram.app.core.money.MoneyEngine
import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigDecimal

class EstimateCalculationTest {

    @Test
    fun testMultiItemQuoteWithDiscountAndTax() {
        // Items:
        // 1. Ceiling fan installation: 2 units @ ₹250 (25000 paise each) = ₹500 (50000 paise)
        // 2. Copper wire bundle: 1 roll @ ₹1,850 (185000 paise) = ₹1,850 (185000 paise)
        // Subtotal = ₹2,350 (235000 paise)
        val item1 = MoneyEngine.calculateLineItemAmount(25000L, BigDecimal("2"))
        val item2 = MoneyEngine.calculateLineItemAmount(185000L, BigDecimal("1"))

        assertEquals(50000L, item1)
        assertEquals(185000L, item2)

        // 5% discount (500 bps) -> ₹117.50 (11750 paise, rounded to 11800 or half-up: 11750 paise = ₹117.50)
        // 18% GST (1800 bps) on taxable amount
        val totals = MoneyEngine.computeTotals(
            itemAmountsPaise = listOf(item1, item2),
            discountBps = 500, // 5%
            taxRateBps = 1800  // 18% GST
        )

        assertEquals(235000L, totals.subtotalPaise)
        assertEquals(11750L, totals.discountPaise) // 235000 * 500 / 10000 = 11750
        assertEquals(223250L, totals.taxableAmountPaise) // 235000 - 11750 = 223250
        assertEquals(40185L, totals.taxPaise) // 223250 * 1800 / 10000 = 40185
        assertEquals(263435L, totals.grandTotalPaise) // 223250 + 40185 = 263435 (₹2,634.35)
    }

    @Test
    fun testQuoteWithoutTaxAndDiscount() {
        val item1 = MoneyEngine.calculateLineItemAmount(45000L, BigDecimal("1"))
        val totals = MoneyEngine.computeTotals(listOf(item1), discountBps = 0, taxRateBps = 0)

        assertEquals(45000L, totals.subtotalPaise)
        assertEquals(0L, totals.discountPaise)
        assertEquals(45000L, totals.taxableAmountPaise)
        assertEquals(0L, totals.taxPaise)
        assertEquals(45000L, totals.grandTotalPaise)
    }
}
