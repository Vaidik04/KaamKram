package com.kaamkram.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.kaamkram.app.domain.model.EstimateStatus
import java.math.BigDecimal

@Entity(
    tableName = "estimates",
    foreignKeys = [
        ForeignKey(
            entity = JobEntity::class,
            parentColumns = ["id"],
            childColumns = ["jobId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["jobId"])]
)
data class EstimateEntity(
    @PrimaryKey
    val id: String,
    val jobId: String,
    val currentVersionNumber: Int = 1,
    val status: EstimateStatus = EstimateStatus.DRAFT,
    val createdAtEpochMs: Long = System.currentTimeMillis(),
    val updatedAtEpochMs: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "estimate_versions",
    foreignKeys = [
        ForeignKey(
            entity = EstimateEntity::class,
            parentColumns = ["id"],
            childColumns = ["estimateId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["estimateId"]),
        Index(value = ["estimateId", "versionNumber"], unique = true)
    ]
)
data class EstimateVersionEntity(
    @PrimaryKey
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
    val createdAtEpochMs: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "estimate_line_items",
    foreignKeys = [
        ForeignKey(
            entity = EstimateVersionEntity::class,
            parentColumns = ["id"],
            childColumns = ["estimateVersionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["estimateVersionId"])]
)
data class EstimateLineItemEntity(
    @PrimaryKey
    val id: String,
    val estimateVersionId: String,
    val description: String,
    val quantity: BigDecimal,
    val unitPricePaise: Long,
    val totalAmountPaise: Long,
    val isPartMaterial: Boolean = false,
    val sortOrder: Int = 0
)
