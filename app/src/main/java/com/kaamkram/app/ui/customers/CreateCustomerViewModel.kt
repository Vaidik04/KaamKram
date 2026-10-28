package com.kaamkram.app.ui.customers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaamkram.app.data.repository.CustomerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateCustomerViewModel @Inject constructor(
    private val customerRepository: CustomerRepository
) : ViewModel() {

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name.asStateFlow()

    private val _phone = MutableStateFlow("")
    val phone: StateFlow<String> = _phone.asStateFlow()

    private val _address = MutableStateFlow("")
    val address: StateFlow<String> = _address.asStateFlow()

    private val _notes = MutableStateFlow("")
    val notes: StateFlow<String> = _notes.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun onNameChanged(value: String) {
        _name.value = value
        _errorMessage.value = null
    }

    fun onPhoneChanged(value: String) {
        _phone.value = value
        _errorMessage.value = null
    }

    fun onAddressChanged(value: String) {
        _address.value = value
    }

    fun onNotesChanged(value: String) {
        _notes.value = value
    }

    fun saveCustomer(onSuccess: (String) -> Unit) {
        val trimmedName = _name.value.trim()
        val trimmedPhone = _phone.value.trim().replace(" ", "").replace("-", "")

        if (trimmedName.isEmpty()) {
            _errorMessage.value = "Customer name is required"
            return
        }

        if (trimmedPhone.length < 10) {
            _errorMessage.value = "Enter a valid 10-digit mobile number"
            return
        }

        viewModelScope.launch {
            try {
                val customer = customerRepository.createCustomer(
                    name = trimmedName,
                    phone = trimmedPhone,
                    initialAddress = _address.value.trim().ifEmpty { null },
                    notes = _notes.value.trim().ifEmpty { null }
                )
                onSuccess(customer.id)
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "Failed to save customer"
            }
        }
    }
}
