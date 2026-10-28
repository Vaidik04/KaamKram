package com.kaamkram.app.domain.validation

import com.kaamkram.app.domain.model.JobStatus

object JobStateMachine {

    private val VALID_TRANSITIONS: Map<JobStatus, Set<JobStatus>> = mapOf(
        JobStatus.REQUESTED to setOf(JobStatus.QUOTED, JobStatus.SCHEDULED, JobStatus.CANCELLED),
        JobStatus.QUOTED to setOf(JobStatus.APPROVED, JobStatus.REQUESTED, JobStatus.CANCELLED),
        JobStatus.APPROVED to setOf(JobStatus.SCHEDULED, JobStatus.IN_PROGRESS, JobStatus.CANCELLED),
        JobStatus.SCHEDULED to setOf(JobStatus.IN_PROGRESS, JobStatus.CANCELLED),
        JobStatus.IN_PROGRESS to setOf(JobStatus.COMPLETED, JobStatus.CANCELLED),
        JobStatus.COMPLETED to setOf(JobStatus.INVOICED, JobStatus.PAID, JobStatus.CANCELLED),
        JobStatus.INVOICED to setOf(JobStatus.PAID, JobStatus.CANCELLED),
        JobStatus.PAID to emptySet(),
        JobStatus.CANCELLED to emptySet()
    )

    fun canTransition(from: JobStatus, to: JobStatus): Boolean {
        if (from == to) return true
        return VALID_TRANSITIONS[from]?.contains(to) == true
    }

    fun assertValidTransition(from: JobStatus, to: JobStatus) {
        if (!canTransition(from, to)) {
            throw IllegalStateException("Invalid status transition from $from to $to")
        }
    }
}
