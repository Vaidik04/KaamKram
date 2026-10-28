package com.kaamkram.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "business_profile")
data class BusinessProfileEntity(
    @PrimaryKey
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

@Entity(
    tableName = "price_book_items",
    indices = [
        Index(value = ["tradeCategory"]),
        Index(value = ["name"])
    ]
)
data class PriceBookItemEntity(
    @PrimaryKey
    val id: String,
    val tradeCategory: String,
    val name: String,
    val description: String? = null,
    val defaultUnitPricePaise: Long,
    val unit: String = "unit",
    val isPartMaterial: Boolean = false,
    val standardCostPaise: Long = 0L,
    val isCustom: Boolean = false,
    val sortOrder: Int = 0
)
