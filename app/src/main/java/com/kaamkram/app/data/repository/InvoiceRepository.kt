package com.kaamkram.app.data.repository

import com.kaamkram.app.core.money.MoneyEngine
import com.kaamkram.app.data.local.dao.BusinessProfileDao
import com.kaamkram.app.data.local.dao.InvoiceDao
import com.kaamkram.app.data.local.dao.JobDao
import com.kaamkram.app.data.local.dao.JobEventDao
import com.kaamkram.app.data.local.entity.InvoiceEntity
import com.kaamkram.app.data.local.entity.InvoiceLineItemEntity
import com.kaamkram.app.data.local.entity.JobEventEntity
import com.kaamkram.app.domain.model.Invoice
import com.kaamkram.app.domain.model.InvoiceLineItem
import com.kaamkram.app.domain.model.InvoiceStatus
import com.kaamkram.app.domain.model.JobEventType
import com.kaamkram.app.domain.model.JobStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InvoiceRepository @Inject constructor(
    private val invoiceDao: InvoiceDao,
    private val jobDao: JobDao,
    private val jobEventDao: JobEventDao,
    private val businessProfileDao: BusinessProfileDao
) {
    suspend fun getInvoiceForJob(jobId: String): Invoice? {
        val invoiceEntity = invoiceDao.getInvoiceForJob(jobId) ?: return null
        val items = invoiceDao.getLineItemsListForInvoice(invoiceEntity.id).map { it.toDomain() }
        return invoiceEntity.toDomain(items)
    }

    fun observeInvoiceForJob(jobId: String): Flow<Invoice?> =
        invoiceDao.observeInvoiceForJob(jobId).map { it?.toDomain(emptyList()) }

    suspend fun createInvoice(
        jobId: String,
        items: List<InvoiceLineItemDraft>,
        discountBps: Int = 0,
        taxRateBps: Int = 0,
        notes: String? = null,
        dueDate: String? = null
    ): Invoice {
        val profile = businessProfileDao.getProfileDirect()
        val year = SimpleDateFormat("yyyy", Locale.getDefault()).format(Date())
        val nextSeq = profile?.nextInvoiceNumber ?: 1
        val prefix = profile?.invoicePrefix ?: "INV"
        val invoiceNumber = String.format("%s-%s-%04d", prefix, year, nextSeq)

        val itemAmounts = items.map {
            MoneyEngine.calculateLineItemAmount(it.unitPricePaise, it.quantity)
        }
        val totals = MoneyEngine.computeTotals(itemAmounts, discountBps, taxRateBps)

        val invoiceId = UUID.randomUUID().toString()
        val todayIso = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        val invoiceEntity = InvoiceEntity(
            id = invoiceId,
            jobId = jobId,
            invoiceNumber = invoiceNumber,
            issueDate = todayIso,
            dueDate = dueDate ?: todayIso,
            status = InvoiceStatus.ISSUED,
            subtotalPaise = totals.subtotalPaise,
            discountBps = discountBps,
            discountPaise = totals.discountPaise,
            taxRateBps = taxRateBps,
            taxPaise = totals.taxPaise,
            grandTotalPaise = totals.grandTotalPaise,
            paidAmountPaise = 0L,
            balanceDuePaise = totals.grandTotalPaise,
            notes = notes?.trim()?.ifEmpty { null }
        )

        invoiceDao.insertInvoice(invoiceEntity)
        businessProfileDao.incrementInvoiceNumber()

        val lineEntities = items.mapIndexed { index, draft ->
            val total = MoneyEngine.calculateLineItemAmount(draft.unitPricePaise, draft.quantity)
            InvoiceLineItemEntity(
                id = UUID.randomUUID().toString(),
                invoiceId = invoiceId,
                description = draft.description.trim(),
                quantity = draft.quantity,
                unitPricePaise = draft.unitPricePaise,
                totalAmountPaise = total,
                isPartMaterial = draft.isPartMaterial,
                sortOrder = index + 1
            )
        }
        invoiceDao.insertLineItems(lineEntities)

        // Update Job Status to INVOICED
        jobDao.updateJobStatus(jobId, JobStatus.INVOICED)

        // Log audit trail event
        jobEventDao.insertEvent(
            JobEventEntity(
                id = UUID.randomUUID().toString(),
                jobId = jobId,
                eventType = JobEventType.INVOICE_GENERATED,
                summary = "Invoice issued: $invoiceNumber for ₹${totals.grandTotalPaise / 100}"
            )
        )

        return invoiceEntity.toDomain(lineEntities.map { it.toDomain() })
    }

    data class InvoiceLineItemDraft(
        val description: String,
        val quantity: BigDecimal,
        val unitPricePaise: Long,
        val isPartMaterial: Boolean = false
    )

    private fun InvoiceEntity.toDomain(items: List<InvoiceLineItem>) = Invoice(
        id = id,
        jobId = jobId,
        invoiceNumber = invoiceNumber,
        issueDate = issueDate,
        dueDate = dueDate,
        status = status,
        subtotalPaise = subtotalPaise,
        discountBps = discountBps,
        discountPaise = discountPaise,
        taxRateBps = taxRateBps,
        taxPaise = taxPaise,
        grandTotalPaise = grandTotalPaise,
        paidAmountPaise = paidAmountPaise,
        balanceDuePaise = balanceDuePaise,
        notes = notes,
        items = items,
        createdAtEpochMs = createdAtEpochMs,
        updatedAtEpochMs = updatedAtEpochMs
    )

    private fun InvoiceLineItemEntity.toDomain() = InvoiceLineItem(
        id = id,
        invoiceVersionId = invoiceId,
        description = description,
        quantity = quantity,
        unitPricePaise = unitPricePaise,
        totalAmountPaise = totalAmountPaise,
        isPartMaterial = isPartMaterial,
        sortOrder = sortOrder
    )
}
