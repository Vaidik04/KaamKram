package com.kaamkram.app.ui.estimates

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaamkram.app.core.money.MoneyEngine
import com.kaamkram.app.data.local.dao.PriceBookDao
import com.kaamkram.app.data.local.entity.PriceBookItemEntity
import com.kaamkram.app.data.repository.EstimateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class CreateEstimateViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val estimateRepository: EstimateRepository,
    private val priceBookDao: PriceBookDao
) : ViewModel() {

    val jobId: String = checkNotNull(savedStateHandle["jobId"])

    val priceBookItems: Flow<List<PriceBookItemEntity>> = priceBookDao.getAllItems()

    data class ItemDraft(
        val description: String,
        val unitPriceRupeesStr: String,
        val quantityStr: String,
        val isPartMaterial: Boolean = false
    )

    private val _lineItems = MutableStateFlow<List<ItemDraft>>(
        listOf(ItemDraft(description = "Service / Inspection", unitPriceRupeesStr = "250", quantityStr = "1"))
    )
    val lineItems: StateFlow<List<ItemDraft>> = _lineItems.asStateFlow()

    private val _discountPercent = MutableStateFlow("0")
    val discountPercent: StateFlow<String> = _discountPercent.asStateFlow()

    private val _taxRatePercent = MutableStateFlow("0")
    val taxRatePercent: StateFlow<String> = _taxRatePercent.asStateFlow()

    private val _notes = MutableStateFlow("")
    val notes: StateFlow<String> = _notes.asStateFlow()

    private val _totals = MutableStateFlow(
        MoneyEngine.TotalsBreakdown(
            subtotalPaise = 25000L,
            discountPaise = 0L,
            taxableAmountPaise = 25000L,
            taxPaise = 0L,
            grandTotalPaise = 25000L
        )
    )
    val totals: StateFlow<MoneyEngine.TotalsBreakdown> = _totals.asStateFlow()

    init {
        recalculateTotals()
    }

    fun addItem(description: String, unitPriceRupees: String, quantity: String, isPart: Boolean) {
        val updated = _lineItems.value.toMutableList().apply {
            add(ItemDraft(description, unitPriceRupees, quantity, isPart))
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

    fun saveEstimate(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val drafts = _lineItems.value.map { item ->
                val ratePaise = MoneyEngine.rupeesToPaise(item.unitPriceRupeesStr)
                val qty = try { BigDecimal(item.quantityStr.ifEmpty { "1" }) } catch (_: Exception) { BigDecimal.ONE }
                EstimateRepository.EstimateLineItemDraft(
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

            estimateRepository.saveEstimateVersion(
                jobId = jobId,
                items = drafts,
                discountBps = discountBps,
                taxRateBps = taxRateBps,
                notes = _notes.value
            )
            onSuccess()
        }
    }
}
