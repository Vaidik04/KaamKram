package com.kaamkram.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.kaamkram.app.domain.model.InvoiceStatus
import java.math.BigDecimal

@Entity(
    tableName = "invoices",
    foreignKeys = [
        ForeignKey(
            entity = JobEntity::class,
            parentColumns = ["id"],
            childColumns = ["jobId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["jobId"]),
        Index(value = ["invoiceNumber"], unique = true),
        Index(value = ["status"]),
        Index(value = ["dueDate"])
    ]
)
data class InvoiceEntity(
    @PrimaryKey
    val id: String,
    val jobId: String,
    val invoiceNumber: String,
    val issueDate: String,
    val dueDate: String,
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
    val createdAtEpochMs: Long = System.currentTimeMillis(),
    val updatedAtEpochMs: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "invoice_line_items",
    foreignKeys = [
        ForeignKey(
            entity = InvoiceEntity::class,
            parentColumns = ["id"],
            childColumns = ["invoiceId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["invoiceId"])]
)
data class InvoiceLineItemEntity(
    @PrimaryKey
    val id: String,
    val invoiceId: String,
    val description: String,
    val quantity: BigDecimal,
    val unitPricePaise: Long,
    val totalAmountPaise: Long,
    val isPartMaterial: Boolean = false,
    val sortOrder: Int = 0
)
