package com.kaamkram.app.ui.invoices

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaamkram.app.core.money.MoneyEngine
import com.kaamkram.app.data.repository.EstimateRepository
import com.kaamkram.app.data.repository.InvoiceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class CreateInvoiceViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val invoiceRepository: InvoiceRepository,
    private val estimateRepository: EstimateRepository
) : ViewModel() {

    val jobId: String = checkNotNull(savedStateHandle["jobId"])

    data class ItemDraft(
        val description: String,
        val unitPriceRupeesStr: String,
        val quantityStr: String,
        val isPartMaterial: Boolean = false
    )

    private val _lineItems = MutableStateFlow<List<ItemDraft>>(emptyList())
    val lineItems: StateFlow<List<ItemDraft>> = _lineItems.asStateFlow()

    private val _discountPercent = MutableStateFlow("0")
    val discountPercent: StateFlow<String> = _discountPercent.asStateFlow()

    private val _taxRatePercent = MutableStateFlow("0")
    val taxRatePercent: StateFlow<String> = _taxRatePercent.asStateFlow()

    private val todayIso = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    private val _dueDate = MutableStateFlow(todayIso)
    val dueDate: StateFlow<String> = _dueDate.asStateFlow()

    private val _notes = MutableStateFlow("")
    val notes: StateFlow<String> = _notes.asStateFlow()

    private val _totals = MutableStateFlow(
        MoneyEngine.TotalsBreakdown(
            subtotalPaise = 0L,
            discountPaise = 0L,
            taxableAmountPaise = 0L,
            taxPaise = 0L,
            grandTotalPaise = 0L
        )
    )
    val totals: StateFlow<MoneyEngine.TotalsBreakdown> = _totals.asStateFlow()

    init {
        loadEstimateItems()
    }

    private fun loadEstimateItems() {
        viewModelScope.launch {
            val estimate = estimateRepository.getEstimateForJob(jobId)
            if (estimate != null) {
                val version = estimateRepository.getVersion(estimate.id, estimate.currentVersionNumber)
                if (version != null && version.items.isNotEmpty()) {
                    _lineItems.value = version.items.map {
                        ItemDraft(
                            description = it.description,
                            unitPriceRupeesStr = (it.unitPricePaise / 100).toString(),
                            quantityStr = it.quantity.toPlainString(),
                            isPartMaterial = it.isPartMaterial
                        )
                    }
                    _discountPercent.value = (version.discountBps / 100).toString()
                    _taxRatePercent.value = (version.taxRateBps / 100).toString()
                } else {
                    _lineItems.value = listOf(ItemDraft("Service Charge", "500", "1"))
                }
            } else {
                _lineItems.value = listOf(ItemDraft("Service Charge", "500", "1"))
            }
            recalculateTotals()
        }
    }

    fun addItem(description: String, unitPriceRupees: String, quantity: String) {
        val updated = _lineItems.value.toMutableList().apply {
            add(ItemDraft(description, unitPriceRupees, quantity))
        }
        _lineItems.value = updated
        recalculateTotals()
    }

    fun removeItem(index: Int) {
        if (_lineItems.value.size <= 1) return
        val updated = _lineItems.value.toMutableList().apply { removeAt(index) }
        _lineItems.value = updated
        recalculateTotals()
    }

    fun updateItemDescription(index: Int, text: String) {
        val updated = _lineItems.value.toMutableList()
        updated[index] = updated[index].copy(description = text)
        _lineItems.value = updated
    }

    fun updateItemQuantity(index: Int, qty: String) {
        val updated = _lineItems.value.toMutableList()
        updated[index] = updated[index].copy(quantityStr = qty)
        _lineItems.value = updated
        recalculateTotals()
    }

    fun updateItemPrice(index: Int, priceRupees: String) {
        val updated = _lineItems.value.toMutableList()
        updated[index] = updated[index].copy(unitPriceRupeesStr = priceRupees)
        _lineItems.value = updated
        recalculateTotals()
    }

    fun onDiscountChanged(percent: String) {
        _discountPercent.value = percent
        recalculateTotals()
    }

    fun onTaxRateChanged(percent: String) {
        _taxRatePercent.value = percent
        recalculateTotals()
    }

    fun onDueDateChanged(date: String) {
        _dueDate.value = date
    }

    fun onNotesChanged(text: String) {
        _notes.value = text
    }

    private fun recalculateTotals() {
        val amountsPaise = _lineItems.value.map { item ->
            val ratePaise = MoneyEngine.rupeesToPaise(item.unitPriceRupeesStr)
            val qty = try { BigDecimal(item.quantityStr.ifEmpty { "1" }) } catch (_: Exception) { BigDecimal.ONE }
            MoneyEngine.calculateLineItemAmount(ratePaise, qty)
        }

        val discountBps = try {
            (_discountPercent.value.toDoubleOrNull() ?: 0.0).times(100).toInt().coerceIn(0, 10000)
        } catch (_: Exception) { 0 }

        val taxRateBps = try {
            (_taxRatePercent.value.toDoubleOrNull() ?: 0.0).times(100).toInt().coerceAtLeast(0)
        } catch (_: Exception) { 0 }

        _totals.value = MoneyEngine.computeTotals(amountsPaise, discountBps, taxRateBps)
    }

    fun saveInvoice(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val drafts = _lineItems.value.map { item ->
                val ratePaise = MoneyEngine.rupeesToPaise(item.unitPriceRupeesStr)
                val qty = try { BigDecimal(item.quantityStr.ifEmpty { "1" }) } catch (_: Exception) { BigDecimal.ONE }
                InvoiceRepository.InvoiceLineItemDraft(
                    description = item.description.ifEmpty { "Service" },
                    quantity = qty,
                    unitPricePaise = ratePaise,
                    isPartMaterial = item.isPartMaterial
                )
            }

            val discountBps = try {
                (_discountPercent.value.toDoubleOrNull() ?: 0.0).times(100).toInt().coerceIn(0, 10000)
            } catch (_: Exception) { 0 }

            val taxRateBps = try {
                (_taxRatePercent.value.toDoubleOrNull() ?: 0.0).times(100).toInt().coerceAtLeast(0)
            } catch (_: Exception) { 0 }

            invoiceRepository.createInvoice(
                jobId = jobId,
                items = drafts,
                discountBps = discountBps,
                taxRateBps = taxRateBps,
                notes = _notes.value,
                dueDate = _dueDate.value
            )
            onSuccess()
        }
    }
}
