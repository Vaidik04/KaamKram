package com.kaamkram.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.kaamkram.app.data.local.entity.EstimateEntity
import com.kaamkram.app.data.local.entity.EstimateLineItemEntity
import com.kaamkram.app.data.local.entity.EstimateVersionEntity
import com.kaamkram.app.domain.model.EstimateStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface EstimateDao {

    @Query("SELECT * FROM estimates WHERE jobId = :jobId LIMIT 1")
    suspend fun getEstimateForJob(jobId: String): EstimateEntity?

    @Query("SELECT * FROM estimates WHERE jobId = :jobId LIMIT 1")
    fun observeEstimateForJob(jobId: String): Flow<EstimateEntity?>

    @Query("SELECT * FROM estimate_versions WHERE estimateId = :estimateId ORDER BY versionNumber DESC")
    fun getVersionsForEstimate(estimateId: String): Flow<List<EstimateVersionEntity>>

    @Query("SELECT * FROM estimate_versions WHERE estimateId = :estimateId AND versionNumber = :versionNumber LIMIT 1")
    suspend fun getVersion(estimateId: String, versionNumber: Int): EstimateVersionEntity?

    @Query("SELECT * FROM estimate_line_items WHERE estimateVersionId = :versionId ORDER BY sortOrder ASC")
    fun getLineItemsForVersion(versionId: String): Flow<List<EstimateLineItemEntity>>

    @Query("SELECT * FROM estimate_line_items WHERE estimateVersionId = :versionId ORDER BY sortOrder ASC")
    suspend fun getLineItemsListForVersion(versionId: String): List<EstimateLineItemEntity>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertEstimate(estimate: EstimateEntity)

    @Update
    suspend fun updateEstimate(estimate: EstimateEntity)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertVersion(version: EstimateVersionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLineItems(items: List<EstimateLineItemEntity>)

    @Query("UPDATE estimates SET status = :status, updatedAtEpochMs = :updatedAt WHERE id = :estimateId")
    suspend fun updateEstimateStatus(estimateId: String, status: EstimateStatus, updatedAt: Long = System.currentTimeMillis())
}
