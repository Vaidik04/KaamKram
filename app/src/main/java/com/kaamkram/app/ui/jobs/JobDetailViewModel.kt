package com.kaamkram.app.ui.jobs

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaamkram.app.core.documents.QuotePdfGenerator
import com.kaamkram.app.data.local.dao.BusinessProfileDao
import com.kaamkram.app.data.repository.CustomerRepository
import com.kaamkram.app.data.repository.EstimateRepository
import com.kaamkram.app.data.repository.JobRepository
import com.kaamkram.app.domain.model.BusinessProfile
import com.kaamkram.app.domain.model.Customer
import com.kaamkram.app.domain.model.Estimate
import com.kaamkram.app.domain.model.EstimateVersion
import com.kaamkram.app.domain.model.Job
import com.kaamkram.app.domain.model.JobEvent
import com.kaamkram.app.domain.model.JobStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class JobDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val jobRepository: JobRepository,
    private val customerRepository: CustomerRepository,
    private val estimateRepository: EstimateRepository,
    private val businessProfileDao: BusinessProfileDao
) : ViewModel() {

    val jobId: String = checkNotNull(savedStateHandle["jobId"])

    private val _job = MutableStateFlow<Job?>(null)
    val job: StateFlow<Job?> = _job.asStateFlow()

    private val _customer = MutableStateFlow<Customer?>(null)
    val customer: StateFlow<Customer?> = _customer.asStateFlow()

    private val _estimate = MutableStateFlow<Estimate?>(null)
    val estimate: StateFlow<Estimate?> = _estimate.asStateFlow()

    private val _currentEstimateVersion = MutableStateFlow<EstimateVersion?>(null)
    val currentEstimateVersion: StateFlow<EstimateVersion?> = _currentEstimateVersion.asStateFlow()

    val events: Flow<List<JobEvent>> = jobRepository.getEventsForJob(jobId)

    init {
        loadJobDetails()
    }

    private fun loadJobDetails() {
        viewModelScope.launch {
            val j = jobRepository.getJobById(jobId)
            _job.value = j
            if (j != null) {
                _customer.value = customerRepository.getCustomerById(j.customerId)
                val est = estimateRepository.getEstimateForJob(j.id)
                _estimate.value = est
                if (est != null) {
                    _currentEstimateVersion.value = estimateRepository.getVersion(est.id, est.currentVersionNumber)
                }
            }
        }
    }

    fun updateJobStatus(newStatus: JobStatus) {
        viewModelScope.launch {
            jobRepository.updateJobStatus(jobId, newStatus)
            loadJobDetails()
        }
    }

    fun approveEstimate() {
        val est = _estimate.value ?: return
        viewModelScope.launch {
            estimateRepository.markEstimateApproved(jobId, est.id)
            loadJobDetails()
        }
    }

    fun generateAndShareQuotePdf(context: Context, onPdfReady: (File) -> Unit) {
        val currentJob = _job.value ?: return
        val currentCust = _customer.value ?: return
        val currentVer = _currentEstimateVersion.value ?: return

        viewModelScope.launch {
            val profileEntity = businessProfileDao.getProfileDirect()
            val profile = profileEntity?.let {
                BusinessProfile(
                    businessName = it.businessName,
                    ownerName = it.ownerName,
                    phone = it.phone,
                    upiId = it.upiId
                )
            }

            val file = QuotePdfGenerator.generateQuotePdf(
                context = context,
                job = currentJob,
                customer = currentCust,
                estimateVersion = currentVer,
                businessProfile = profile
            )
            onPdfReady(file)
        }
    }
}
