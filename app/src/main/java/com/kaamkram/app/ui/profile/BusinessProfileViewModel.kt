package com.kaamkram.app.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaamkram.app.data.local.dao.BusinessProfileDao
import com.kaamkram.app.data.local.entity.BusinessProfileEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BusinessProfileViewModel @Inject constructor(
    private val businessProfileDao: BusinessProfileDao
) : ViewModel() {

    private val _businessName = MutableStateFlow("")
    val businessName: StateFlow<String> = _businessName.asStateFlow()

    private val _ownerName = MutableStateFlow("")
    val ownerName: StateFlow<String> = _ownerName.asStateFlow()

    private val _phone = MutableStateFlow("")
    val phone: StateFlow<String> = _phone.asStateFlow()

    private val _upiId = MutableStateFlow("")
    val upiId: StateFlow<String> = _upiId.asStateFlow()

    private val _address = MutableStateFlow("")
    val address: StateFlow<String> = _address.asStateFlow()

    private val _tradeCategory = MutableStateFlow("ELECTRICIAN")
    val tradeCategory: StateFlow<String> = _tradeCategory.asStateFlow()

    private val _gstin = MutableStateFlow("")
    val gstin: StateFlow<String> = _gstin.asStateFlow()

    private val _isGstRegistered = MutableStateFlow(false)
    val isGstRegistered: StateFlow<Boolean> = _isGstRegistered.asStateFlow()

    private val _invoicePrefix = MutableStateFlow("INV")
    val invoicePrefix: StateFlow<String> = _invoicePrefix.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            val profile = businessProfileDao.getProfileDirect()
            if (profile != null) {
                _businessName.value = profile.businessName
                _ownerName.value = profile.ownerName
                _phone.value = profile.phone
                _upiId.value = profile.upiId ?: ""
                _address.value = profile.address ?: ""
                _tradeCategory.value = profile.tradeCategory
                _gstin.value = profile.gstin ?: ""
                _isGstRegistered.value = profile.isGstRegistered
                _invoicePrefix.value = profile.invoicePrefix
            }
        }
    }

    fun onBusinessNameChanged(v: String) { _businessName.value = v }
    fun onOwnerNameChanged(v: String) { _ownerName.value = v }
    fun onPhoneChanged(v: String) { _phone.value = v }
    fun onUpiIdChanged(v: String) { _upiId.value = v }
    fun onAddressChanged(v: String) { _address.value = v }
    fun onTradeCategoryChanged(v: String) { _tradeCategory.value = v }
    fun onGstinChanged(v: String) { _gstin.value = v }
    fun onGstToggle(v: Boolean) { _isGstRegistered.value = v }
    fun onInvoicePrefixChanged(v: String) { _invoicePrefix.value = v }

    fun saveProfile(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val existing = businessProfileDao.getProfileDirect()
            val entity = BusinessProfileEntity(
                id = "default_profile",
                businessName = _businessName.value.trim().ifEmpty { "My Trade Shop" },
                ownerName = _ownerName.value.trim().ifEmpty { "Technician" },
                phone = _phone.value.trim(),
                upiId = _upiId.value.trim().ifEmpty { null },
                address = _address.value.trim().ifEmpty { null },
                tradeCategory = _tradeCategory.value,
                gstin = if (_isGstRegistered.value) _gstin.value.trim().ifEmpty { null } else null,
                isGstRegistered = _isGstRegistered.value,
                invoicePrefix = _invoicePrefix.value.trim().ifEmpty { "INV" },
                jobPrefix = existing?.jobPrefix ?: "JOB",
                nextInvoiceNumber = existing?.nextInvoiceNumber ?: 1,
                nextJobNumber = existing?.nextJobNumber ?: 1
            )
            businessProfileDao.insertOrUpdateProfile(entity)
            onSuccess()
        }
    }
}
