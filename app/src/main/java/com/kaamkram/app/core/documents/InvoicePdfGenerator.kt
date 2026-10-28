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
import com.kaamkram.app.domain.model.Invoice
import com.kaamkram.app.domain.model.Job
import java.io.File
import java.io.FileOutputStream

object InvoicePdfGenerator {

    private const val PAGE_WIDTH = 595
    private const val PAGE_HEIGHT = 842
    private const val MARGIN = 40f

    fun generateInvoicePdf(
        context: Context,
        job: Job,
        customer: Customer,
        invoice: Invoice,
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
            color = Color.rgb(226, 232, 240)
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

        if (!businessProfile?.gstin.isNullOrEmpty()) {
            y += 14f
            canvas.drawText("GSTIN: ${businessProfile!!.gstin}", MARGIN, y, textPaint)
        }

        // Title on Right
        textPaint.color = Color.rgb(15, 23, 42)
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textPaint.textSize = 20f
        val titleText = "TAX INVOICE"
        val titleWidth = textPaint.measureText(titleText)
        canvas.drawText(titleText, PAGE_WIDTH - MARGIN - titleWidth, 50f, textPaint)

        textPaint.color = Color.rgb(180, 83, 9)
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textPaint.textSize = 11f
        val invNum = invoice.invoiceNumber
        val numWidth = textPaint.measureText(invNum)
        canvas.drawText(invNum, PAGE_WIDTH - MARGIN - numWidth, 68f, textPaint)

        y += 24f
        canvas.drawLine(MARGIN, y, PAGE_WIDTH - MARGIN, y, linePaint)
        y += 20f

        // 2. Customer & Invoice Info
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textPaint.textSize = 11f
        textPaint.color = Color.rgb(15, 23, 42)
        canvas.drawText("Billed To:", MARGIN, y, textPaint)

        textPaint.typeface = Typeface.DEFAULT
        textPaint.textSize = 10f
        y += 14f
        canvas.drawText("Customer: ${customer.name}", MARGIN, y, textPaint)
        y += 14f
        canvas.drawText("Phone: ${customer.phone}", MARGIN, y, textPaint)

        val dateText = "Invoice Date: ${invoice.issueDate}  |  Due Date: ${invoice.dueDate}"
        canvas.drawText(dateText, PAGE_WIDTH - MARGIN - textPaint.measureText(dateText), y - 14f, textPaint)

        y += 20f

        // 3. Line Items Table Header
        val colNo = MARGIN
        val colDesc = MARGIN + 30f
        val colQty = PAGE_WIDTH - MARGIN - 180f
        val colRate = PAGE_WIDTH - MARGIN - 110f
        val colTotal = PAGE_WIDTH - MARGIN - 50f

        val headerPaint = Paint().apply {
            color = Color.rgb(241, 245, 249)
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

        // 4. Rows
        textPaint.typeface = Typeface.DEFAULT
        textPaint.textSize = 9f
        textPaint.color = Color.BLACK

        invoice.items.forEachIndexed { index, item ->
            canvas.drawText("${index + 1}", colNo, y, textPaint)
            val desc = if (item.description.length > 40) item.description.take(37) + "..." else item.description
            canvas.drawText(desc, colDesc, y, textPaint)
            canvas.drawText("${item.quantity}", colQty, y, textPaint)
            canvas.drawText(MoneyFormatter.formatPaise(item.unitPricePaise), colRate, y, textPaint)
            canvas.drawText(MoneyFormatter.formatPaise(item.totalAmountPaise), colTotal, y, textPaint)

            y += 18f
            canvas.drawLine(MARGIN, y - 6f, PAGE_WIDTH - MARGIN, y - 6f, linePaint)
        }

        y += 14f

        // 5. Totals Breakdown
        val totalsX = PAGE_WIDTH - MARGIN - 140f
        val valX = PAGE_WIDTH - MARGIN - 50f

        textPaint.textSize = 10f
        canvas.drawText("Subtotal:", totalsX, y, textPaint)
        canvas.drawText(MoneyFormatter.formatPaise(invoice.subtotalPaise), valX, y, textPaint)

        if (invoice.discountPaise > 0) {
            y += 16f
            canvas.drawText("Discount:", totalsX, y, textPaint)
            canvas.drawText("-${MoneyFormatter.formatPaise(invoice.discountPaise)}", valX, y, textPaint)
        }

        if (invoice.taxPaise > 0) {
            y += 16f
            canvas.drawText("GST / Tax:", totalsX, y, textPaint)
            canvas.drawText(MoneyFormatter.formatPaise(invoice.taxPaise), valX, y, textPaint)
        }

        y += 18f
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textPaint.textSize = 12f
        primaryPaint.textSize = 12f
        canvas.drawText("Total Amount:", totalsX, y, textPaint)
        canvas.drawText(MoneyFormatter.formatPaise(invoice.grandTotalPaise), valX, y, primaryPaint)

        y += 16f
        textPaint.textSize = 11f
        val duePaint = Paint().apply {
            color = Color.rgb(185, 28, 28) // Red 700
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textSize = 11f
            isAntiAlias = true
        }
        canvas.drawText("Balance Due:", totalsX, y, textPaint)
        canvas.drawText(MoneyFormatter.formatPaise(invoice.balanceDuePaise), valX, y, duePaint)

        y += 24f

        // 6. Words
        textPaint.typeface = Typeface.DEFAULT
        textPaint.textSize = 9f
        textPaint.color = Color.rgb(71, 85, 105)
        canvas.drawText("Amount in Words (English): ${AmountInWords.toWordsEnglish(invoice.grandTotalPaise)}", MARGIN, y, textPaint)
        y += 14f
        canvas.drawText("राशि शब्दों में (हिंदी): ${AmountInWords.toWordsHindi(invoice.grandTotalPaise)}", MARGIN, y, textPaint)

        // 7. UPI Payment Box
        if (!businessProfile?.upiId.isNullOrEmpty()) {
            y += 24f
            val qrBoxPaint = Paint().apply {
                color = Color.rgb(254, 243, 199) // Amber 100
            }
            canvas.drawRoundRect(MARGIN, y, PAGE_WIDTH - MARGIN, y + 40f, 8f, 8f, qrBoxPaint)
            textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textPaint.textSize = 10f
            textPaint.color = Color.rgb(120, 53, 15)
            canvas.drawText("Pay instantly via any UPI App (GPay, PhonePe, Paytm):", MARGIN + 12f, y + 18f, textPaint)
            textPaint.typeface = Typeface.DEFAULT
            textPaint.color = Color.BLACK
            canvas.drawText("UPI ID: ${businessProfile!!.upiId}  |  Ref: ${invoice.invoiceNumber}", MARGIN + 12f, y + 32f, textPaint)
        }

        // 8. Footer
        y = PAGE_HEIGHT - 50f
        canvas.drawLine(MARGIN, y, PAGE_WIDTH - MARGIN, y, linePaint)
        y += 18f
        textPaint.textSize = 8f
        textPaint.color = Color.rgb(148, 163, 184)
        canvas.drawText("KaamKram Tax Invoice • Computer Generated", MARGIN, y, textPaint)

        pdfDocument.finishPage(page)

        val docsDir = File(context.filesDir, "documents").apply { mkdirs() }
        val outputFile = File(docsDir, "Invoice_${invoice.invoiceNumber}.pdf")
        FileOutputStream(outputFile).use { out ->
            pdfDocument.writeTo(out)
        }
        pdfDocument.close()

        return outputFile
    }
}
