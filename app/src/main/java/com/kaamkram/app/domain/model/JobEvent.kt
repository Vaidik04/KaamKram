package com.kaamkram.app.domain.model

enum class JobEventType {
    CREATED,
    STATUS_CHANGED,
    ESTIMATE_CREATED,
    ESTIMATE_APPROVED,
    ESTIMATE_REJECTED,
    PHOTO_ADDED,
    PAYMENT_RECORDED,
    INVOICE_GENERATED,
    NOTE_ADDED,
    CANCELLED
}

data class JobEvent(
    val id: String,
    val jobId: String,
    val eventType: JobEventType,
    val summary: String,
    val detailsJson: String? = null,
    val timestampEpochMs: Long = System.currentTimeMillis()
)

enum class PhotoCategory {
    BEFORE,
    DURING,
    AFTER,
    SITE_ISSUE,
    RECEIPT
}

enum class PhotoSource {
    CAPTURED_IN_APP,
    IMPORTED_FROM_GALLERY
}

data class PhotoEvidence(
    val id: String,
    val jobId: String,
    val filePath: String,
    val category: PhotoCategory,
    val source: PhotoSource,
    val caption: String? = null,
    val capturedAtEpochMs: Long = System.currentTimeMillis()
)

data class Warranty(
    val id: String,
    val jobId: String,
    val serviceOrItemName: String,
    val durationDays: Int,
    val startDate: String, // YYYY-MM-DD
    val expiryDate: String, // YYYY-MM-DD
    val terms: String? = null,
    val createdAtEpochMs: Long = System.currentTimeMillis()
)
