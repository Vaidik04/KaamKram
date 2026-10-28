package com.kaamkram.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.kaamkram.app.data.local.entity.JobEntity
import com.kaamkram.app.domain.model.JobStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface JobDao {

    @Query("SELECT * FROM jobs ORDER BY createdAtEpochMs DESC")
    fun getAllJobs(): Flow<List<JobEntity>>

    @Query("SELECT * FROM jobs WHERE id = :id LIMIT 1")
    suspend fun getJobById(id: String): JobEntity?

    @Query("SELECT * FROM jobs WHERE customerId = :customerId ORDER BY createdAtEpochMs DESC")
    fun getJobsForCustomer(customerId: String): Flow<List<JobEntity>>

    @Query("SELECT * FROM jobs WHERE scheduledDate = :date ORDER BY createdAtEpochMs ASC")
    fun getJobsScheduledOn(date: String): Flow<List<JobEntity>>

    @Query("SELECT * FROM jobs WHERE status = :status ORDER BY createdAtEpochMs DESC")
    fun getJobsByStatus(status: JobStatus): Flow<List<JobEntity>>

    @Query("SELECT COUNT(*) FROM jobs WHERE scheduledDate = :date AND status != 'CANCELLED'")
    fun countDueOnDate(date: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM jobs WHERE status = 'IN_PROGRESS'")
    fun countInProgress(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertJob(job: JobEntity)

    @Update
    suspend fun updateJob(job: JobEntity)

    @Query("UPDATE jobs SET status = :status, updatedAtEpochMs = :updatedAt WHERE id = :jobId")
    suspend fun updateJobStatus(jobId: String, status: JobStatus, updatedAt: Long = System.currentTimeMillis())
}
