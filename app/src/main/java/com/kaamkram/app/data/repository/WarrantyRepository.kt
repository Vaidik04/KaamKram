package com.kaamkram.app.data.repository

import com.kaamkram.app.data.local.dao.JobDao
import com.kaamkram.app.data.local.dao.JobEventDao
import com.kaamkram.app.data.local.dao.WarrantyDao
import com.kaamkram.app.data.local.entity.JobEventEntity
import com.kaamkram.app.data.local.entity.WarrantyEntity
import com.kaamkram.app.domain.model.JobEventType
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WarrantyRepository @Inject constructor(
    private val warrantyDao: WarrantyDao,
    private val jobEventDao: JobEventDao,
    private val jobDao: JobDao
) {

    fun getWarrantiesForJob(jobId: String): Flow<List<WarrantyEntity>> {
        return warrantyDao.getWarrantiesForJob(jobId)
    }

    fun getAllWarranties(): Flow<List<WarrantyEntity>> {
        return warrantyDao.getAllWarranties()
    }

    suspend fun addWarranty(
        jobId: String,
        serviceOrItemName: String,
        durationDays: Int,
        terms: String? = null
    ): WarrantyEntity {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val now = Date()
        val startDate = dateFormat.format(now)

        val calendar = Calendar.getInstance()
        calendar.time = now
        calendar.add(Calendar.DAY_OF_YEAR, durationDays)
        val expiryDate = dateFormat.format(calendar.time)

        val warranty = WarrantyEntity(
            id = UUID.randomUUID().toString(),
            jobId = jobId,
            serviceOrItemName = serviceOrItemName,
            durationDays = durationDays,
            startDate = startDate,
            expiryDate = expiryDate,
            terms = terms,
            createdAtEpochMs = System.currentTimeMillis()
        )

        warrantyDao.insertWarranty(warranty)

        // Append audit trail event
        val event = JobEventEntity(
            id = UUID.randomUUID().toString(),
            jobId = jobId,
            eventType = JobEventType.NOTE_ADDED,
            summary = "Warranty issued: $serviceOrItemName ($durationDays days, expires $expiryDate)",
            detailsJson = "{\"serviceOrItem\":\"$serviceOrItemName\",\"days\":$durationDays,\"expiryDate\":\"$expiryDate\"}",
            timestampEpochMs = System.currentTimeMillis()
        )
        jobEventDao.insertEvent(event)

        return warranty
    }
}
