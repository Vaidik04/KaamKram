package com.kaamkram.app.domain.model

import java.math.BigDecimal

enum class InvoiceStatus {
    DRAFT,
    ISSUED,
    PARTIALLY_PAID,
    PAID,
    VOID
}

data class InvoiceLineItem(
    val id: String,
    val invoiceVersionId: String,
    val description: String,
    val quantity: BigDecimal,
    val unitPricePaise: Long,
    val totalAmountPaise: Long,
    val isPartMaterial: Boolean = false,
    val sortOrder: Int = 0
)

data class Invoice(
    val id: String,
    val jobId: String,
    val invoiceNumber: String, // e.g. "INV-2026-0001"
    val issueDate: String,     // YYYY-MM-DD
    val dueDate: String,       // YYYY-MM-DD
    val status: InvoiceStatus = InvoiceStatus.DRAFT,
    val subtotalPaise: Long,
    val discountBps: Int = 0,
    val discountPaise: Long = 0L,
    val taxRateBps: Int = 0,
    val taxPaise: Long = 0L,
    val grandTotalPaise: Long,
    val paidAmountPaise: Long = 0L,
    val balanceDuePaise: Long = grandTotalPaise,
    val notes: String? = null,
    val items: List<InvoiceLineItem> = emptyList(),
    val createdAtEpochMs: Long = System.currentTimeMillis(),
    val updatedAtEpochMs: Long = System.currentTimeMillis()
)
