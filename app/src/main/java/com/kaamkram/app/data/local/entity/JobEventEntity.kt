package com.kaamkram.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.kaamkram.app.domain.model.JobEventType
import com.kaamkram.app.domain.model.PhotoCategory
import com.kaamkram.app.domain.model.PhotoSource

@Entity(
    tableName = "job_events",
    foreignKeys = [
        ForeignKey(
            entity = JobEntity::class,
            parentColumns = ["id"],
            childColumns = ["jobId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["jobId"]),
        Index(value = ["timestampEpochMs"])
    ]
)
data class JobEventEntity(
    @PrimaryKey
    val id: String,
    val jobId: String,
    val eventType: JobEventType,
    val summary: String,
    val detailsJson: String? = null,
    val timestampEpochMs: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "photo_evidence",
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
data class PhotoEvidenceEntity(
    @PrimaryKey
    val id: String,
    val jobId: String,
    val filePath: String,
    val category: PhotoCategory,
    val source: PhotoSource,
    val caption: String? = null,
    val capturedAtEpochMs: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "warranties",
    foreignKeys = [
        ForeignKey(
            entity = JobEntity::class,
            parentColumns = ["id"],
            childColumns = ["jobId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["jobId"]),
        Index(value = ["expiryDate"])
    ]
)
data class WarrantyEntity(
    @PrimaryKey
    val id: String,
    val jobId: String,
    val serviceOrItemName: String,
    val durationDays: Int,
    val startDate: String,
    val expiryDate: String,
    val terms: String? = null,
    val createdAtEpochMs: Long = System.currentTimeMillis()
)
