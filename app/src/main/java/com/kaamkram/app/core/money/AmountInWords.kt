package com.kaamkram.app.core.money

/**
 * Converts monetary amounts (in paise) to words in Indian numbering format.
 * Supports English and Hindi for official invoices and quotes.
 */
object AmountInWords {

    private val UNITS_EN = arrayOf(
        "", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine",
        "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen",
        "Seventeen", "Eighteen", "Nineteen"
    )

    private val TENS_EN = arrayOf(
        "", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"
    )

    private val UNITS_HI = arrayOf(
        "", "एक", "दो", "तीन", "चार", "पाँच", "छह", "सात", "आठ", "नौ",
        "दस", "ग्यारह", "बारह", "तेरह", "चौदह", "पंद्रह", "सोलह", "सत्रह", "अठारह", "उन्नीस"
    )

    private val TENS_HI = arrayOf(
        "", "दस", "बीस", "तीस", "चालीस", "पचास", "साठ", "सत्तर", "अस्सी", "नब्बे"
    )

    fun toWordsEnglish(paise: Long): String {
        if (paise == 0L) return "Zero Rupees Only"

        val absPaise = Math.abs(paise)
        val rupees = absPaise / 100
        val remainingPaise = absPaise % 100

        val sb = StringBuilder()
        if (paise < 0) sb.append("Minus ")

        if (rupees > 0) {
            sb.append(convertNumberToWordsEn(rupees)).append(" Rupees")
        }

        if (remainingPaise > 0) {
            if (rupees > 0) sb.append(" and ")
            sb.append(convertNumberToWordsEn(remainingPaise)).append(" Paise")
        }

        sb.append(" Only")
        return sb.toString().trim()
    }

    fun toWordsHindi(paise: Long): String {
        if (paise == 0L) return "शून्य रुपये मात्र"

        val absPaise = Math.abs(paise)
        val rupees = absPaise / 100
        val remainingPaise = absPaise % 100

        val sb = StringBuilder()
        if (paise < 0) sb.append("ऋण ")

        if (rupees > 0) {
            sb.append(convertNumberToWordsHi(rupees)).append(" रुपये")
        }

        if (remainingPaise > 0) {
            if (rupees > 0) sb.append(" और ")
            sb.append(convertNumberToWordsHi(remainingPaise)).append(" पैसे")
        }

        sb.append(" मात्र")
        return sb.toString().trim()
    }

    private fun convertNumberToWordsEn(n: Long): String {
        if (n == 0L) return ""

        var num = n
        val sb = StringBuilder()

        val crore = num / 10000000
        num %= 10000000

        val lakh = num / 100000
        num %= 100000

        val thousand = num / 1000
        num %= 1000

        val hundred = num / 100
        num %= 100

        if (crore > 0) {
            sb.append(convertNumberToWordsEn(crore)).append(" Crore ")
        }
        if (lakh > 0) {
            sb.append(convertNumberToWordsEn(lakh)).append(" Lakh ")
        }
        if (thousand > 0) {
            sb.append(convertNumberToWordsEn(thousand)).append(" Thousand ")
        }
        if (hundred > 0) {
            sb.append(UNITS_EN[hundred.toInt()]).append(" Hundred ")
        }

        if (num > 0) {
            if (num < 20) {
                sb.append(UNITS_EN[num.toInt()])
            } else {
                sb.append(TENS_EN[(num / 10).toInt()])
                val remainder = num % 10
                if (remainder > 0) {
                    sb.append(" ").append(UNITS_EN[remainder.toInt()])
                }
            }
        }

        return sb.toString().trim()
    }

    private fun convertNumberToWordsHi(n: Long): String {
        if (n == 0L) return ""

        var num = n
        val sb = StringBuilder()

        val crore = num / 10000000
        num %= 10000000

        val lakh = num / 100000
        num %= 100000

        val thousand = num / 1000
        num %= 1000

        val hundred = num / 100
        num %= 100

        if (crore > 0) {
            sb.append(convertNumberToWordsHi(crore)).append(" करोड़ ")
        }
        if (lakh > 0) {
            sb.append(convertNumberToWordsHi(lakh)).append(" लाख ")
        }
        if (thousand > 0) {
            sb.append(convertNumberToWordsHi(thousand)).append(" हज़ार ")
        }
        if (hundred > 0) {
            sb.append(UNITS_HI[hundred.toInt()]).append(" सौ ")
        }

        if (num > 0) {
            if (num < 20) {
                sb.append(UNITS_HI[num.toInt()])
            } else {
                sb.append(TENS_HI[(num / 10).toInt()])
                val remainder = num % 10
                if (remainder > 0) {
                    sb.append(" ").append(UNITS_HI[remainder.toInt()])
                }
            }
        }

        return sb.toString().trim()
    }
}
