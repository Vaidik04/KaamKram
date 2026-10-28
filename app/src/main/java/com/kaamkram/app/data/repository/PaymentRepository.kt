package com.kaamkram.app.data.repository

import com.kaamkram.app.core.money.MoneyEngine
import com.kaamkram.app.data.local.dao.InvoiceDao
import com.kaamkram.app.data.local.dao.JobDao
import com.kaamkram.app.data.local.dao.JobEventDao
import com.kaamkram.app.data.local.dao.PaymentDao
import com.kaamkram.app.data.local.entity.JobEventEntity
import com.kaamkram.app.data.local.entity.PaymentEntity
import com.kaamkram.app.domain.model.InvoiceStatus
import com.kaamkram.app.domain.model.JobEventType
import com.kaamkram.app.domain.model.JobStatus
import com.kaamkram.app.domain.model.Payment
import com.kaamkram.app.domain.model.PaymentMode
import com.kaamkram.app.domain.model.PaymentType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PaymentRepository @Inject constructor(
    private val paymentDao: PaymentDao,
    private val invoiceDao: InvoiceDao,
    private val jobDao: JobDao,
    private val jobEventDao: JobEventDao
) {
    fun getPaymentsForJob(jobId: String): Flow<List<Payment>> =
        paymentDao.getPaymentsForJob(jobId).map { list -> list.map { it.toDomain() } }

    suspend fun recordPayment(
        jobId: String,
        invoiceId: String?,
        amountPaise: Long,
        paymentMode: PaymentMode,
        paymentType: PaymentType = PaymentType.PARTIAL,
        referenceNumber: String? = null,
        notes: String? = null
    ): Payment {
        val paymentId = UUID.randomUUID().toString()
        val todayIso = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        val paymentEntity = PaymentEntity(
            id = paymentId,
            jobId = jobId,
            invoiceId = invoiceId,
            amountPaise = amountPaise,
            paymentMode = paymentMode,
            paymentType = paymentType,
            referenceNumber = referenceNumber?.trim()?.ifEmpty { null },
            receivedDate = todayIso,
            notes = notes?.trim()?.ifEmpty { null }
        )
        paymentDao.insertPayment(paymentEntity)

        // Update Invoice progress if invoiceId present
        if (invoiceId != null) {
            val invoice = invoiceDao.getInvoiceById(invoiceId)
            if (invoice != null) {
                val newPaid = MoneyEngine.add(invoice.paidAmountPaise, amountPaise)
                val newBalance = Math.max(0L, MoneyEngine.subtract(invoice.grandTotalPaise, newPaid))
                val newStatus = if (newBalance == 0L) InvoiceStatus.PAID else InvoiceStatus.PARTIALLY_PAID
                invoiceDao.updatePaymentProgress(invoiceId, newPaid, newBalance, newStatus)

                if (newStatus == InvoiceStatus.PAID) {
                    jobDao.updateJobStatus(jobId, JobStatus.PAID)
                }
            }
        }

        // Log audit trail event
        jobEventDao.insertEvent(
            JobEventEntity(
                id = UUID.randomUUID().toString(),
                jobId = jobId,
                eventType = JobEventType.PAYMENT_RECORDED,
                summary = "Payment received: ₹${amountPaise / 100} via $paymentMode ($paymentType)"
            )
        )

        return paymentEntity.toDomain()
    }

    private fun PaymentEntity.toDomain() = Payment(
        id = id,
        jobId = jobId,
        invoiceId = invoiceId,
        amountPaise = amountPaise,
        paymentMode = paymentMode,
        paymentType = paymentType,
        referenceNumber = referenceNumber,
        receivedDate = receivedDate,
        notes = notes,
        createdAtEpochMs = createdAtEpochMs
    )
}
