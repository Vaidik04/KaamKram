package com.kaamkram.app.ui.jobs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaamkram.app.data.repository.CustomerRepository
import com.kaamkram.app.data.repository.JobRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class CreateJobViewModel @Inject constructor(
    private val jobRepository: JobRepository,
    private val customerRepository: CustomerRepository
) : ViewModel() {

    private val todayIso = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    private val _customerName = MutableStateFlow("")
    val customerName: StateFlow<String> = _customerName.asStateFlow()

    private val _customerPhone = MutableStateFlow("")
    val customerPhone: StateFlow<String> = _customerPhone.asStateFlow()

    private val _siteAddress = MutableStateFlow("")
    val siteAddress: StateFlow<String> = _siteAddress.asStateFlow()

    private val _jobTitle = MutableStateFlow("")
    val jobTitle: StateFlow<String> = _jobTitle.asStateFlow()

    private val _jobDescription = MutableStateFlow("")
    val jobDescription: StateFlow<String> = _jobDescription.asStateFlow()

    private val _scheduledDate = MutableStateFlow(todayIso)
    val scheduledDate: StateFlow<String> = _scheduledDate.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun onCustomerNameChanged(value: String) {
        _customerName.value = value
        _errorMessage.value = null
    }

    fun onCustomerPhoneChanged(value: String) {
        _customerPhone.value = value
        _errorMessage.value = null
    }

    fun onSiteAddressChanged(value: String) {
        _siteAddress.value = value
    }

    fun onJobTitleChanged(value: String) {
        _jobTitle.value = value
        _errorMessage.value = null
    }

    fun onJobDescriptionChanged(value: String) {
        _jobDescription.value = value
    }

    fun onScheduledDateChanged(value: String) {
        _scheduledDate.value = value
    }

    fun createJob(onSuccess: (String) -> Unit) {
        val name = _customerName.value.trim()
        val phone = _customerPhone.value.trim().replace(" ", "").replace("-", "")
        val title = _jobTitle.value.trim()

        if (name.isEmpty()) {
            _errorMessage.value = "Customer name is required"
            return
        }

        if (phone.length < 10) {
            _errorMessage.value = "Enter a valid 10-digit mobile number"
            return
        }

        if (title.isEmpty()) {
            _errorMessage.value = "Job title is required"
            return
        }

        viewModelScope.launch {
            try {
                // Create customer first
                val customer = customerRepository.createCustomer(
                    name = name,
                    phone = phone,
                    initialAddress = _siteAddress.value.trim().ifEmpty { null }
                )

                // Create job
                val job = jobRepository.createJob(
                    customerId = customer.id,
                    siteId = null,
                    title = title,
                    description = _jobDescription.value.trim().ifEmpty { null },
                    scheduledDate = _scheduledDate.value.trim().ifEmpty { todayIso }
                )

                onSuccess(job.id)
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "Failed to create job"
            }
        }
    }
}
