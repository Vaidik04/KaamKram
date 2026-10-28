package com.kaamkram.app.core.money

/**
 * Formats monetary amounts in paise to standard Indian Rupee notation (1,23,456.78).
 */
object MoneyFormatter {

    const val RUPEE_SYMBOL = "₹"

    /**
     * Formats [paise] as INR string with rupee symbol.
     * e.g. 12345678L -> "₹1,23,456.78"
     * 100000L -> "₹1,000" (if showPaiseIfZero is false)
     */
    fun formatPaise(
        paise: Long,
        includeSymbol: Boolean = true,
        showPaiseIfZero: Boolean = false
    ): String {
        val isNegative = paise < 0
        val absPaise = Math.abs(paise)
        val rupees = absPaise / 100
        val remainderPaise = absPaise % 100

        val formattedRupees = formatIndianGrouping(rupees)
        val result = StringBuilder()

        if (isNegative) {
            result.append("-")
        }
        if (includeSymbol) {
            result.append(RUPEE_SYMBOL)
        }

        result.append(formattedRupees)

        if (remainderPaise > 0 || showPaiseIfZero) {
            result.append(".")
            result.append(String.format("%02d", remainderPaise))
        }

        return result.toString()
    }

    /**
     * Applies Indian number grouping (last 3 digits, then groups of 2).
     * e.g. 1234567 -> "12,34,567"
     */
    fun formatIndianGrouping(number: Long): String {
        val s = number.toString()
        val len = s.length
        if (len <= 3) return s

        val lastThree = s.substring(len - 3)
        val rest = s.substring(0, len - 3)

        val sb = StringBuilder()
        var count = 0
        for (i in rest.length - 1 downTo 0) {
            sb.append(rest[i])
            count++
            if (count % 2 == 0 && i != 0) {
                sb.append(',')
            }
        }
        val groupedRest = sb.reverse().toString()
        return "$groupedRest,$lastThree"
    }
}
