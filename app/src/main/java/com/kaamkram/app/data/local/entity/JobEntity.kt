package com.kaamkram.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.kaamkram.app.domain.model.JobStatus

@Entity(
    tableName = "jobs",
    foreignKeys = [
        ForeignKey(
            entity = CustomerEntity::class,
            parentColumns = ["id"],
            childColumns = ["customerId"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = SiteEntity::class,
            parentColumns = ["id"],
            childColumns = ["siteId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["customerId"]),
        Index(value = ["siteId"]),
        Index(value = ["status"]),
        Index(value = ["scheduledDate"]),
        Index(value = ["jobNumber"], unique = true)
    ]
)
data class JobEntity(
    @PrimaryKey
    val id: String,
    val jobNumber: String,
    val customerId: String,
    val siteId: String?,
    val title: String,
    val description: String? = null,
    val status: JobStatus = JobStatus.REQUESTED,
    val scheduledDate: String? = null,
    val completedDate: String? = null,
    val createdAtEpochMs: Long = System.currentTimeMillis(),
    val updatedAtEpochMs: Long = System.currentTimeMillis()
)
