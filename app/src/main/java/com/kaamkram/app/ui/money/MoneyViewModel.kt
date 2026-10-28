package com.kaamkram.app.ui.money

import androidx.lifecycle.ViewModel
import com.kaamkram.app.data.local.dao.InvoiceDao
import com.kaamkram.app.data.local.dao.PaymentDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class MoneyViewModel @Inject constructor(
    private val invoiceDao: InvoiceDao,
    private val paymentDao: PaymentDao
) : ViewModel() {

    val totalInvoicedPaise: Flow<Long> = invoiceDao.observeTotalInvoicedPaise()
    val totalCollectedPaise: Flow<Long> = paymentDao.observeTotalCollectedPaise()
    val totalPendingPaise: Flow<Long> = invoiceDao.observeTotalPendingPaise()
}
