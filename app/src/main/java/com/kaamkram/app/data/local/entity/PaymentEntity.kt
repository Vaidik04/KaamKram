package com.kaamkram.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.kaamkram.app.domain.model.PaymentMode
import com.kaamkram.app.domain.model.PaymentType

@Entity(
    tableName = "payments",
    foreignKeys = [
        ForeignKey(
            entity = JobEntity::class,
            parentColumns = ["id"],
            childColumns = ["jobId"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = InvoiceEntity::class,
            parentColumns = ["id"],
            childColumns = ["invoiceId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["jobId"]),
        Index(value = ["invoiceId"]),
        Index(value = ["receivedDate"]),
        Index(value = ["paymentMode"])
    ]
)
data class PaymentEntity(
    @PrimaryKey
    val id: String,
    val jobId: String,
    val invoiceId: String?,
    val amountPaise: Long,
    val paymentMode: PaymentMode,
    val paymentType: PaymentType,
    val referenceNumber: String? = null,
    val receivedDate: String,
    val notes: String? = null,
    val createdAtEpochMs: Long = System.currentTimeMillis()
)
