package com.kaamkram.app.domain.model

import java.math.BigDecimal

enum class EstimateStatus {
    DRAFT,
    SENT,
    APPROVED,
    REJECTED
}

data class EstimateLineItem(
    val id: String,
    val estimateVersionId: String,
    val description: String,
    val quantity: BigDecimal,
    val unitPricePaise: Long,
    val totalAmountPaise: Long,
    val isPartMaterial: Boolean = false,
    val sortOrder: Int = 0
)

data class Estimate(
    val id: String,
    val jobId: String,
    val currentVersionNumber: Int = 1,
    val status: EstimateStatus = EstimateStatus.DRAFT,
    val createdAtEpochMs: Long = System.currentTimeMillis(),
    val updatedAtEpochMs: Long = System.currentTimeMillis()
)

data class EstimateVersion(
    val id: String,
    val estimateId: String,
    val versionNumber: Int,
    val subtotalPaise: Long,
    val discountBps: Int = 0,
    val discountPaise: Long = 0L,
    val taxRateBps: Int = 0,
    val taxPaise: Long = 0L,
    val grandTotalPaise: Long,
    val notes: String? = null,
    val items: List<EstimateLineItem> = emptyList(),
    val createdAtEpochMs: Long = System.currentTimeMillis()
)
