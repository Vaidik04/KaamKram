package com.kaamkram.app.domain.model

enum class PaymentMode {
    CASH,
    UPI,
    BANK_TRANSFER,
    CHEQUE,
    OTHER
}

enum class PaymentType {
    ADVANCE,
    PARTIAL,
    FINAL,
    REFUND
}

data class Payment(
    val id: String,
    val jobId: String,
    val invoiceId: String?,
    val amountPaise: Long,
    val paymentMode: PaymentMode,
    val paymentType: PaymentType,
    val referenceNumber: String? = null, // UPI UTR or Cheque number
    val receivedDate: String,            // YYYY-MM-DD
    val notes: String? = null,
    val createdAtEpochMs: Long = System.currentTimeMillis()
)
