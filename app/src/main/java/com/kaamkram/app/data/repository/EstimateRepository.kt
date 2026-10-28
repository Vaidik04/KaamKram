package com.kaamkram.app.data.repository

import com.kaamkram.app.core.money.MoneyEngine
import com.kaamkram.app.data.local.dao.EstimateDao
import com.kaamkram.app.data.local.dao.JobDao
import com.kaamkram.app.data.local.dao.JobEventDao
import com.kaamkram.app.data.local.entity.EstimateEntity
import com.kaamkram.app.data.local.entity.EstimateLineItemEntity
import com.kaamkram.app.data.local.entity.EstimateVersionEntity
import com.kaamkram.app.data.local.entity.JobEventEntity
import com.kaamkram.app.domain.model.Estimate
import com.kaamkram.app.domain.model.EstimateLineItem
import com.kaamkram.app.domain.model.EstimateStatus
import com.kaamkram.app.domain.model.EstimateVersion
import com.kaamkram.app.domain.model.JobEventType
import com.kaamkram.app.domain.model.JobStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.math.BigDecimal
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EstimateRepository @Inject constructor(
    private val estimateDao: EstimateDao,
    private val jobDao: JobDao,
    private val jobEventDao: JobEventDao
) {
    suspend fun getEstimateForJob(jobId: String): Estimate? =
        estimateDao.getEstimateForJob(jobId)?.toDomain()

    fun observeEstimateForJob(jobId: String): Flow<Estimate?> =
        estimateDao.observeEstimateForJob(jobId).map { it?.toDomain() }

    suspend fun getVersion(estimateId: String, versionNumber: Int): EstimateVersion? {
        val versionEntity = estimateDao.getVersion(estimateId, versionNumber) ?: return null
        val items = estimateDao.getLineItemsListForVersion(versionEntity.id).map { it.toDomain() }
        return versionEntity.toDomain(items)
    }

    suspend fun saveEstimateVersion(
        jobId: String,
        items: List<EstimateLineItemDraft>,
        discountBps: Int = 0,
        taxRateBps: Int = 0,
        notes: String? = null
    ): EstimateVersion {
        var estimate = estimateDao.getEstimateForJob(jobId)
        val isFirstEstimate = (estimate == null)

        val estimateId = estimate?.id ?: UUID.randomUUID().toString()
        val nextVersionNumber = if (isFirstEstimate) 1 else (estimate!!.currentVersionNumber + 1)

        if (isFirstEstimate) {
            estimate = EstimateEntity(
                id = estimateId,
                jobId = jobId,
                currentVersionNumber = 1,
                status = EstimateStatus.DRAFT
            )
            estimateDao.insertEstimate(estimate)
        } else {
            estimateDao.updateEstimate(
                estimate!!.copy(
                    currentVersionNumber = nextVersionNumber,
                    updatedAtEpochMs = System.currentTimeMillis()
                )
            )
        }

        // Calculate totals with MoneyEngine
        val itemCalculatedAmounts = items.map {
            MoneyEngine.calculateLineItemAmount(it.unitPricePaise, it.quantity)
        }
        val totals = MoneyEngine.computeTotals(
            itemAmountsPaise = itemCalculatedAmounts,
            discountBps = discountBps,
            taxRateBps = taxRateBps
        )

        val versionId = UUID.randomUUID().toString()
        val versionEntity = EstimateVersionEntity(
            id = versionId,
            estimateId = estimateId,
            versionNumber = nextVersionNumber,
            subtotalPaise = totals.subtotalPaise,
            discountBps = discountBps,
            discountPaise = totals.discountPaise,
            taxRateBps = taxRateBps,
            taxPaise = totals.taxPaise,
            grandTotalPaise = totals.grandTotalPaise,
            notes = notes?.trim()?.ifEmpty { null }
        )
        estimateDao.insertVersion(versionEntity)

        val lineItemEntities = items.mapIndexed { index, draft ->
            val total = MoneyEngine.calculateLineItemAmount(draft.unitPricePaise, draft.quantity)
            EstimateLineItemEntity(
                id = UUID.randomUUID().toString(),
                estimateVersionId = versionId,
                description = draft.description.trim(),
                quantity = draft.quantity,
                unitPricePaise = draft.unitPricePaise,
                totalAmountPaise = total,
                isPartMaterial = draft.isPartMaterial,
                sortOrder = index + 1
            )
        }
        estimateDao.insertLineItems(lineItemEntities)

        // Update Job Status to QUOTED if still in REQUESTED
        val currentJob = jobDao.getJobById(jobId)
        if (currentJob != null && currentJob.status == JobStatus.REQUESTED) {
            jobDao.updateJobStatus(jobId, JobStatus.QUOTED)
        }

        // Log audit event
        jobEventDao.insertEvent(
            JobEventEntity(
                id = UUID.randomUUID().toString(),
                jobId = jobId,
                eventType = JobEventType.ESTIMATE_CREATED,
                summary = "Estimate Version $nextVersionNumber created: Total ₹${totals.grandTotalPaise / 100}"
            )
        )

        return versionEntity.toDomain(lineItemEntities.map { it.toDomain() })
    }

    suspend fun markEstimateApproved(jobId: String, estimateId: String) {
        estimateDao.updateEstimateStatus(estimateId, EstimateStatus.APPROVED)
        val currentJob = jobDao.getJobById(jobId)
        if (currentJob != null && (currentJob.status == JobStatus.QUOTED || currentJob.status == JobStatus.REQUESTED)) {
            jobDao.updateJobStatus(jobId, JobStatus.APPROVED)
        }

        jobEventDao.insertEvent(
            JobEventEntity(
                id = UUID.randomUUID().toString(),
                jobId = jobId,
                eventType = JobEventType.ESTIMATE_APPROVED,
                summary = "Customer approved quote"
            )
        )
    }

    suspend fun markEstimateRejected(jobId: String, estimateId: String) {
        estimateDao.updateEstimateStatus(estimateId, EstimateStatus.REJECTED)

        jobEventDao.insertEvent(
            JobEventEntity(
                id = UUID.randomUUID().toString(),
                jobId = jobId,
                eventType = JobEventType.ESTIMATE_REJECTED,
                summary = "Customer rejected quote"
            )
        )
    }

    data class EstimateLineItemDraft(
        val description: String,
        val quantity: BigDecimal,
        val unitPricePaise: Long,
        val isPartMaterial: Boolean = false
    )

    private fun EstimateEntity.toDomain() = Estimate(
        id = id,
        jobId = jobId,
        currentVersionNumber = currentVersionNumber,
        status = status,
        createdAtEpochMs = createdAtEpochMs,
        updatedAtEpochMs = updatedAtEpochMs
    )

    private fun EstimateVersionEntity.toDomain(items: List<EstimateLineItem>) = EstimateVersion(
        id = id,
        estimateId = estimateId,
        versionNumber = versionNumber,
        subtotalPaise = subtotalPaise,
        discountBps = discountBps,
        discountPaise = discountPaise,
        taxRateBps = taxRateBps,
        taxPaise = taxPaise,
        grandTotalPaise = grandTotalPaise,
        notes = notes,
        items = items,
        createdAtEpochMs = createdAtEpochMs
    )

    private fun EstimateLineItemEntity.toDomain() = EstimateLineItem(
        id = id,
        estimateVersionId = estimateVersionId,
        description = description,
        quantity = quantity,
        unitPricePaise = unitPricePaise,
        totalAmountPaise = totalAmountPaise,
        isPartMaterial = isPartMaterial,
        sortOrder = sortOrder
    )
}
