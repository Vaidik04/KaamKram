package com.kaamkram.app.data.repository

import com.kaamkram.app.data.local.dao.BusinessProfileDao
import com.kaamkram.app.data.local.dao.JobDao
import com.kaamkram.app.data.local.dao.JobEventDao
import com.kaamkram.app.data.local.entity.JobEntity
import com.kaamkram.app.data.local.entity.JobEventEntity
import com.kaamkram.app.domain.model.Job
import com.kaamkram.app.domain.model.JobEvent
import com.kaamkram.app.domain.model.JobEventType
import com.kaamkram.app.domain.model.JobStatus
import com.kaamkram.app.domain.validation.JobStateMachine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JobRepository @Inject constructor(
    private val jobDao: JobDao,
    private val jobEventDao: JobEventDao,
    private val businessProfileDao: BusinessProfileDao
) {
    fun getAllJobs(): Flow<List<Job>> =
        jobDao.getAllJobs().map { list -> list.map { it.toDomain() } }

    fun getJobsScheduledOn(date: String): Flow<List<Job>> =
        jobDao.getJobsScheduledOn(date).map { list -> list.map { it.toDomain() } }

    suspend fun getJobById(id: String): Job? =
        jobDao.getJobById(id)?.toDomain()

    fun getEventsForJob(jobId: String): Flow<List<JobEvent>> =
        jobEventDao.getEventsForJob(jobId).map { list ->
            list.map {
                JobEvent(
                    id = it.id,
                    jobId = it.jobId,
                    eventType = it.eventType,
                    summary = it.summary,
                    detailsJson = it.detailsJson,
                    timestampEpochMs = it.timestampEpochMs
                )
            }
        }

    suspend fun createJob(
        customerId: String,
        siteId: String?,
        title: String,
        description: String? = null,
        scheduledDate: String? = null
    ): Job {
        val profile = businessProfileDao.getProfileDirect()
        val year = SimpleDateFormat("yyyy", Locale.getDefault()).format(Date())
        val nextSeq = profile?.nextJobNumber ?: 1
        val prefix = profile?.jobPrefix ?: "JOB"
        val jobNumber = String.format("%s-%s-%04d", prefix, year, nextSeq)

        val jobId = UUID.randomUUID().toString()
        val jobEntity = JobEntity(
            id = jobId,
            jobNumber = jobNumber,
            customerId = customerId,
            siteId = siteId,
            title = title.trim(),
            description = description?.trim()?.ifEmpty { null },
            status = JobStatus.REQUESTED,
            scheduledDate = scheduledDate ?: SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        )

        jobDao.insertJob(jobEntity)
        businessProfileDao.incrementJobNumber()

        // Append initial audit log event
        jobEventDao.insertEvent(
            JobEventEntity(
                id = UUID.randomUUID().toString(),
                jobId = jobId,
                eventType = JobEventType.CREATED,
                summary = "Job created: $jobNumber - ${title.trim()}"
            )
        )

        return jobEntity.toDomain()
    }

    suspend fun updateJobStatus(jobId: String, newStatus: JobStatus) {
        val currentJob = jobDao.getJobById(jobId) ?: return
        JobStateMachine.assertValidTransition(currentJob.status, newStatus)

        jobDao.updateJobStatus(jobId, newStatus)

        jobEventDao.insertEvent(
            JobEventEntity(
                id = UUID.randomUUID().toString(),
                jobId = jobId,
                eventType = JobEventType.STATUS_CHANGED,
                summary = "Status changed from ${currentJob.status} to $newStatus"
            )
        )
    }

    private fun JobEntity.toDomain() = Job(
        id = id,
        jobNumber = jobNumber,
        customerId = customerId,
        siteId = siteId,
        title = title,
        description = description,
        status = status,
        scheduledDate = scheduledDate,
        completedDate = completedDate,
        createdAtEpochMs = createdAtEpochMs,
        updatedAtEpochMs = updatedAtEpochMs
    )
}
