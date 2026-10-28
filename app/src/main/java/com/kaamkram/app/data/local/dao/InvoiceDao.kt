package com.kaamkram.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.kaamkram.app.data.local.entity.InvoiceEntity
import com.kaamkram.app.data.local.entity.InvoiceLineItemEntity
import com.kaamkram.app.domain.model.InvoiceStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface InvoiceDao {

    @Query("SELECT * FROM invoices ORDER BY createdAtEpochMs DESC")
    fun getAllInvoices(): Flow<List<InvoiceEntity>>

    @Query("SELECT * FROM invoices WHERE jobId = :jobId LIMIT 1")
    suspend fun getInvoiceForJob(jobId: String): InvoiceEntity?

    @Query("SELECT * FROM invoices WHERE jobId = :jobId LIMIT 1")
    fun observeInvoiceForJob(jobId: String): Flow<InvoiceEntity?>

    @Query("SELECT * FROM invoices WHERE id = :id LIMIT 1")
    suspend fun getInvoiceById(id: String): InvoiceEntity?

    @Query("SELECT * FROM invoice_line_items WHERE invoiceId = :invoiceId ORDER BY sortOrder ASC")
    fun getLineItemsForInvoice(invoiceId: String): Flow<List<InvoiceLineItemEntity>>

    @Query("SELECT * FROM invoice_line_items WHERE invoiceId = :invoiceId ORDER BY sortOrder ASC")
    suspend fun getLineItemsListForInvoice(invoiceId: String): List<InvoiceLineItemEntity>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertInvoice(invoice: InvoiceEntity)

    @Update
    suspend fun updateInvoice(invoice: InvoiceEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLineItems(items: List<InvoiceLineItemEntity>)

    @Query("UPDATE invoices SET paidAmountPaise = :paidAmount, balanceDuePaise = :balanceDue, status = :status, updatedAtEpochMs = :updatedAt WHERE id = :invoiceId")
    suspend fun updatePaymentProgress(invoiceId: String, paidAmount: Long, balanceDue: Long, status: InvoiceStatus, updatedAt: Long = System.currentTimeMillis())

    @Query("SELECT COALESCE(SUM(grandTotalPaise), 0) FROM invoices WHERE status != 'VOID'")
    fun observeTotalInvoicedPaise(): Flow<Long>

    @Query("SELECT COALESCE(SUM(balanceDuePaise), 0) FROM invoices WHERE status != 'VOID' AND status != 'PAID'")
    fun observeTotalPendingPaise(): Flow<Long>
}
