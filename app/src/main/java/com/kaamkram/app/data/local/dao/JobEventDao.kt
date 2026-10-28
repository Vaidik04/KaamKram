package com.kaamkram.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kaamkram.app.data.local.entity.JobEventEntity
import com.kaamkram.app.data.local.entity.PhotoEvidenceEntity
import com.kaamkram.app.data.local.entity.WarrantyEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface JobEventDao {

    @Query("SELECT * FROM job_events WHERE jobId = :jobId ORDER BY timestampEpochMs ASC")
    fun getEventsForJob(jobId: String): Flow<List<JobEventEntity>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertEvent(event: JobEventEntity)
}

@Dao
interface PhotoEvidenceDao {

    @Query("SELECT * FROM photo_evidence WHERE jobId = :jobId ORDER BY capturedAtEpochMs ASC")
    fun getPhotosForJob(jobId: String): Flow<List<PhotoEvidenceEntity>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertPhoto(photo: PhotoEvidenceEntity)

    @Query("DELETE FROM photo_evidence WHERE id = :id")
    suspend fun deletePhoto(id: String)
}

@Dao
interface WarrantyDao {

    @Query("SELECT * FROM warranties WHERE jobId = :jobId")
    fun getWarrantiesForJob(jobId: String): Flow<List<WarrantyEntity>>

    @Query("SELECT * FROM warranties ORDER BY expiryDate ASC")
    fun getAllWarranties(): Flow<List<WarrantyEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWarranty(warranty: WarrantyEntity)
}
