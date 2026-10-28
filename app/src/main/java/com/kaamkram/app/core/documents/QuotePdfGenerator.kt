package com.kaamkram.app.core.documents

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import com.kaamkram.app.core.money.AmountInWords
import com.kaamkram.app.core.money.MoneyFormatter
import com.kaamkram.app.domain.model.BusinessProfile
import com.kaamkram.app.domain.model.Customer
import com.kaamkram.app.domain.model.EstimateVersion
import com.kaamkram.app.domain.model.Job
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object QuotePdfGenerator {

    private const val PAGE_WIDTH = 595
    private const val PAGE_HEIGHT = 842
    private const val MARGIN = 40f

    fun generateQuotePdf(
        context: Context,
        job: Job,
        customer: Customer,
        estimateVersion: EstimateVersion,
        businessProfile: BusinessProfile?
    ): File {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        val textPaint = Paint().apply {
            color = Color.BLACK
            isAntiAlias = true
        }

        val primaryPaint = Paint().apply {
            color = Color.rgb(180, 83, 9) // Amber/Gold accent
            isAntiAlias = true
        }

        val linePaint = Paint().apply {
            color = Color.rgb(226, 232, 240) // Slate 200
            strokeWidth = 1f
            isAntiAlias = true
        }

        var y = 50f

        // 1. Business Header
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textPaint.textSize = 18f
        val bizName = businessProfile?.businessName?.ifEmpty { "KaamKram Technician" } ?: "KaamKram Technician"
        canvas.drawText(bizName, MARGIN, y, textPaint)

        textPaint.typeface = Typeface.DEFAULT
        textPaint.textSize = 10f
        textPaint.color = Color.rgb(100, 116, 139)
        y += 16f
        val ownerPhone = "Contact: ${businessProfile?.phone ?: ""}  |  Owner: ${businessProfile?.ownerName ?: ""}"
        canvas.drawText(ownerPhone, MARGIN, y, textPaint)

        if (!businessProfile?.upiId.isNullOrEmpty()) {
            y += 14f
            canvas.drawText("UPI ID for Payments: ${businessProfile!!.upiId}", MARGIN, y, textPaint)
        }

        // Title on Right
        textPaint.color = Color.rgb(180, 83, 9)
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textPaint.textSize = 20f
        val titleText = "QUOTATION"
        val titleWidth = textPaint.measureText(titleText)
        canvas.drawText(titleText, PAGE_WIDTH - MARGIN - titleWidth, 50f, textPaint)

        textPaint.color = Color.BLACK
        textPaint.typeface = Typeface.DEFAULT
        textPaint.textSize = 9f
        val quoteNum = "Job: ${job.jobNumber} (Rev ${estimateVersion.versionNumber})"
        val numWidth = textPaint.measureText(quoteNum)
        canvas.drawText(quoteNum, PAGE_WIDTH - MARGIN - numWidth, 66f, textPaint)

        y += 24f
        canvas.drawLine(MARGIN, y, PAGE_WIDTH - MARGIN, y, linePaint)
        y += 20f

        // 2. Customer & Job Information
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textPaint.textSize = 11f
        textPaint.color = Color.rgb(15, 23, 42)
        canvas.drawText("Quotation For:", MARGIN, y, textPaint)

        textPaint.typeface = Typeface.DEFAULT
        textPaint.textSize = 10f
        y += 14f
        canvas.drawText("Customer: ${customer.name}", MARGIN, y, textPaint)
        y += 14f
        canvas.drawText("Phone: ${customer.phone}", MARGIN, y, textPaint)

        val dateFormatted = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(Date(estimateVersion.createdAtEpochMs))
        val dateText = "Date: $dateFormatted"
        canvas.drawText(dateText, PAGE_WIDTH - MARGIN - textPaint.measureText(dateText), y - 14f, textPaint)

        y += 20f

        // 3. Line Items Table Header
        val colNo = MARGIN
        val colDesc = MARGIN + 30f
        val colQty = PAGE_WIDTH - MARGIN - 180f
        val colRate = PAGE_WIDTH - MARGIN - 110f
        val colTotal = PAGE_WIDTH - MARGIN - 50f

        // Table header background
        val headerPaint = Paint().apply {
            color = Color.rgb(241, 245, 249) // Slate 100
        }
        canvas.drawRect(MARGIN, y - 12f, PAGE_WIDTH - MARGIN, y + 16f, headerPaint)

        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textPaint.textSize = 9f
        textPaint.color = Color.rgb(30, 41, 59)
        canvas.drawText("#", colNo, y + 4f, textPaint)
        canvas.drawText("Item / Description", colDesc, y + 4f, textPaint)
        canvas.drawText("Qty", colQty, y + 4f, textPaint)
        canvas.drawText("Rate", colRate, y + 4f, textPaint)
        canvas.drawText("Total", colTotal, y + 4f, textPaint)

        y += 24f

        // 4. Line Items Rows
        textPaint.typeface = Typeface.DEFAULT
        textPaint.textSize = 9f
        textPaint.color = Color.BLACK

        estimateVersion.items.forEachIndexed { index, item ->
            canvas.drawText("${index + 1}", colNo, y, textPaint)
            val desc = if (item.description.length > 40) item.description.take(37) + "..." else item.description
            canvas.drawText(desc, colDesc, y, textPaint)
            canvas.drawText("${item.quantity}", colQty, y, textPaint)
            canvas.drawText(MoneyFormatter.formatPaise(item.unitPricePaise, includeSymbol = true), colRate, y, textPaint)
            canvas.drawText(MoneyFormatter.formatPaise(item.totalAmountPaise, includeSymbol = true), colTotal, y, textPaint)

            y += 18f
            canvas.drawLine(MARGIN, y - 6f, PAGE_WIDTH - MARGIN, y - 6f, linePaint)
        }

        y += 14f

        // 5. Totals
        val totalsX = PAGE_WIDTH - MARGIN - 140f
        val valX = PAGE_WIDTH - MARGIN - 50f

        textPaint.textSize = 10f
        canvas.drawText("Subtotal:", totalsX, y, textPaint)
        canvas.drawText(MoneyFormatter.formatPaise(estimateVersion.subtotalPaise), valX, y, textPaint)

        if (estimateVersion.discountPaise > 0) {
            y += 16f
            canvas.drawText("Discount:", totalsX, y, textPaint)
            canvas.drawText("-${MoneyFormatter.formatPaise(estimateVersion.discountPaise)}", valX, y, textPaint)
        }

        if (estimateVersion.taxPaise > 0) {
            y += 16f
            canvas.drawText("Tax (GST):", totalsX, y, textPaint)
            canvas.drawText(MoneyFormatter.formatPaise(estimateVersion.taxPaise), valX, y, textPaint)
        }

        y += 18f
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textPaint.textSize = 12f
        primaryPaint.textSize = 12f
        canvas.drawText("Grand Total:", totalsX, y, textPaint)
        canvas.drawText(MoneyFormatter.formatPaise(estimateVersion.grandTotalPaise), valX, y, primaryPaint)

        y += 26f

        // 6. Amount in Words (English & Hindi)
        textPaint.typeface = Typeface.DEFAULT
        textPaint.textSize = 9f
        textPaint.color = Color.rgb(71, 85, 105)
        canvas.drawText("Amount in Words (English): ${AmountInWords.toWordsEnglish(estimateVersion.grandTotalPaise)}", MARGIN, y, textPaint)
        y += 14f
        canvas.drawText("राशि शब्दों में (हिंदी): ${AmountInWords.toWordsHindi(estimateVersion.grandTotalPaise)}", MARGIN, y, textPaint)

        // 7. Footer
        y = PAGE_HEIGHT - 60f
        canvas.drawLine(MARGIN, y, PAGE_WIDTH - MARGIN, y, linePaint)
        y += 20f
        textPaint.textSize = 8f
        textPaint.color = Color.rgb(148, 163, 184)
        canvas.drawText("Generated via KaamKram • Thank you for your business!", MARGIN, y, textPaint)

        pdfDocument.finishPage(page)

        // Save to file
        val docsDir = File(context.filesDir, "documents").apply { mkdirs() }
        val outputFile = File(docsDir, "Quote_${job.jobNumber}_v${estimateVersion.versionNumber}.pdf")
        FileOutputStream(outputFile).use { out ->
            pdfDocument.writeTo(out)
        }
        pdfDocument.close()

        return outputFile
    }
}
