package com.kaamkram.app.domain.model

data class BusinessProfile(
    val id: String = "default_profile",
    val businessName: String,
    val ownerName: String,
    val phone: String,
    val upiId: String? = null,
    val address: String? = null,
    val tradeCategory: String = "ELECTRICIAN",
    val gstin: String? = null,
    val isGstRegistered: Boolean = false,
    val invoicePrefix: String = "INV",
    val jobPrefix: String = "JOB",
    val nextInvoiceNumber: Int = 1,
    val nextJobNumber: Int = 1,
    val updatedAtEpochMs: Long = System.currentTimeMillis()
)

data class PriceBookItem(
    val id: String,
    val tradeCategory: String,
    val name: String,
    val description: String? = null,
    val defaultUnitPricePaise: Long,
    val unit: String = "unit", // "hr", "point", "unit", "mtr", "sqft"
    val isPartMaterial: Boolean = false,
    val standardCostPaise: Long = 0L,
    val isCustom: Boolean = false,
    val sortOrder: Int = 0
)
