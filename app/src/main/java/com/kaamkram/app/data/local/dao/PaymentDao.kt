package com.kaamkram.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kaamkram.app.data.local.entity.PaymentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PaymentDao {

    @Query("SELECT * FROM payments ORDER BY receivedDate DESC, createdAtEpochMs DESC")
    fun getAllPayments(): Flow<List<PaymentEntity>>

    @Query("SELECT * FROM payments WHERE jobId = :jobId ORDER BY receivedDate ASC")
    fun getPaymentsForJob(jobId: String): Flow<List<PaymentEntity>>

    @Query("SELECT * FROM payments WHERE invoiceId = :invoiceId ORDER BY receivedDate ASC")
    fun getPaymentsForInvoice(invoiceId: String): Flow<List<PaymentEntity>>

    @Query("SELECT COALESCE(SUM(amountPaise), 0) FROM payments WHERE jobId = :jobId")
    suspend fun getTotalPaidForJob(jobId: String): Long

    @Query("SELECT COALESCE(SUM(amountPaise), 0) FROM payments WHERE invoiceId = :invoiceId")
    suspend fun getTotalPaidForInvoice(invoiceId: String): Long

    @Query("SELECT COALESCE(SUM(amountPaise), 0) FROM payments WHERE paymentType != 'REFUND'")
    fun observeTotalCollectedPaise(): Flow<Long>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertPayment(payment: PaymentEntity)
}
