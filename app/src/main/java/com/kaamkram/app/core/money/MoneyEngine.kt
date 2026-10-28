package com.kaamkram.app.core.money

import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Money Engine for KaamKram.
 * NON-NEGOTIABLE PRINCIPLE:
 * All monetary amounts are represented as [Long] in paise (1 INR = 100 paise).
 * No monetary calculation or storage uses Double or Float directly.
 */
object MoneyEngine {

    const val PAISE_PER_RUPEE = 100L

    /**
     * Converts a rupee amount in String/Double-safe format to paise.
     * e.g. "150.50" -> 15050L
     */
    fun rupeesToPaise(rupees: String): Long {
        val trimmed = rupees.trim().replace(",", "")
        if (trimmed.isEmpty()) return 0L
        val bd = BigDecimal(trimmed)
        return bd.multiply(BigDecimal(PAISE_PER_RUPEE))
            .setScale(0, RoundingMode.HALF_UP)
            .longValueExact()
    }

    /**
     * Converts paise to rupee BigDecimal for precise calculations.
     */
    fun paiseToRupeesBigDecimal(paise: Long): BigDecimal {
        return BigDecimal(paise).divide(BigDecimal(PAISE_PER_RUPEE), 2, RoundingMode.HALF_UP)
    }

    /**
     * Multiplies unit price in paise by quantity (e.g. 2.5 hours, 3 units).
     * Rounding: HALF_UP at the paise boundary.
     */
    fun calculateLineItemAmount(unitPricePaise: Long, quantity: BigDecimal): Long {
        if (unitPricePaise == 0L || quantity.compareTo(BigDecimal.ZERO) == 0) return 0L
        return BigDecimal(unitPricePaise)
            .multiply(quantity)
            .setScale(0, RoundingMode.HALF_UP)
            .longValueExact()
    }

    /**
     * Applies a discount percentage (expressed in basis points: 100 bps = 1.00%, 10000 bps = 100%).
     */
    fun calculateDiscount(amountPaise: Long, discountBps: Int): Long {
        require(discountBps in 0..10000) { "Discount must be between 0 and 10000 bps (0% to 100%)" }
        if (discountBps == 0 || amountPaise == 0L) return 0L
        return BigDecimal(amountPaise)
            .multiply(BigDecimal(discountBps))
            .divide(BigDecimal(10000), 0, RoundingMode.HALF_UP)
            .longValueExact()
    }

    /**
     * Calculates tax on taxable amount.
     * [taxRateBps]: Tax in basis points (e.g., 18% GST = 1800 bps, 5% = 500 bps).
     */
    fun calculateTax(taxableAmountPaise: Long, taxRateBps: Int): Long {
        require(taxRateBps >= 0) { "Tax rate must be non-negative" }
        if (taxRateBps == 0 || taxableAmountPaise == 0L) return 0L
        return BigDecimal(taxableAmountPaise)
            .multiply(BigDecimal(taxRateBps))
            .divide(BigDecimal(10000), 0, RoundingMode.HALF_UP)
            .longValueExact()
    }

    /**
     * Safe addition with overflow detection.
     */
    fun add(a: Long, b: Long): Long {
        return Math.addExact(a, b)
    }

    /**
     * Safe subtraction with overflow detection.
     */
    fun subtract(a: Long, b: Long): Long {
        return Math.subtractExact(a, b)
    }

    /**
     * Calculates complete breakdown for an estimate or invoice.
     */
    fun computeTotals(
        itemAmountsPaise: List<Long>,
        discountBps: Int = 0,
        taxRateBps: Int = 0
    ): TotalsBreakdown {
        var subtotal = 0L
        for (amount in itemAmountsPaise) {
            subtotal = add(subtotal, amount)
        }

        val discount = calculateDiscount(subtotal, discountBps)
        val taxableAmount = Math.max(0L, subtract(subtotal, discount))
        val tax = calculateTax(taxableAmount, taxRateBps)
        val grandTotal = add(taxableAmount, tax)

        return TotalsBreakdown(
            subtotalPaise = subtotal,
            discountPaise = discount,
            taxableAmountPaise = taxableAmount,
            taxPaise = tax,
            grandTotalPaise = grandTotal
        )
    }

    data class TotalsBreakdown(
        val subtotalPaise: Long,
        val discountPaise: Long,
        val taxableAmountPaise: Long,
        val taxPaise: Long,
        val grandTotalPaise: Long
    )
}
