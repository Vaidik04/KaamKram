package com.kaamkram.app.domain.model

data class Job(
    val id: String,
    val jobNumber: String, // e.g. "JOB-2026-0001"
    val customerId: String,
    val siteId: String?,
    val title: String,
    val description: String? = null,
    val status: JobStatus = JobStatus.REQUESTED,
    val scheduledDate: String? = null, // YYYY-MM-DD
    val completedDate: String? = null,
    val createdAtEpochMs: Long = System.currentTimeMillis(),
    val updatedAtEpochMs: Long = System.currentTimeMillis()
)
