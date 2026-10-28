package com.kaamkram.app.ui.customers

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaamkram.app.data.local.dao.CustomerDao
import com.kaamkram.app.data.local.dao.InvoiceDao
import com.kaamkram.app.data.local.dao.JobDao
import com.kaamkram.app.data.local.dao.PaymentDao
import com.kaamkram.app.data.repository.CustomerRepository
import com.kaamkram.app.domain.model.Customer
import com.kaamkram.app.domain.model.Job
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CustomerDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val customerRepository: CustomerRepository,
    private val jobDao: JobDao
) : ViewModel() {

    val customerId: String = checkNotNull(savedStateHandle["customerId"])

    private val _customer = MutableStateFlow<Customer?>(null)
    val customer: StateFlow<Customer?> = _customer.asStateFlow()

    val customerJobs: Flow<List<Job>> = jobDao.getJobsForCustomer(customerId).map { list ->
        list.map {
            Job(
                id = it.id,
                jobNumber = it.jobNumber,
                customerId = it.customerId,
                siteId = it.siteId,
                title = it.title,
                description = it.description,
                status = it.status,
                scheduledDate = it.scheduledDate,
                completedDate = it.completedDate,
                createdAtEpochMs = it.createdAtEpochMs,
                updatedAtEpochMs = it.updatedAtEpochMs
            )
        }
    }

    private val _totalSpentPaise = MutableStateFlow(0L)
    val totalSpentPaise: StateFlow<Long> = _totalSpentPaise.asStateFlow()

    private val _balanceDuePaise = MutableStateFlow(0L)
    val balanceDuePaise: StateFlow<Long> = _balanceDuePaise.asStateFlow()

    init {
        loadCustomer()
    }

    private fun loadCustomer() {
        viewModelScope.launch {
            _customer.value = customerRepository.getCustomerById(customerId)
        }
    }
}
