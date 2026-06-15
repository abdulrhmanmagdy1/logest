package com.edham.logistics.ui.home.accountant

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edham.logistics.core.network.api.AccountantStats
import com.edham.logistics.core.network.api.Invoice
import com.edham.logistics.core.network.api.SupervisorApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountantViewModel @Inject constructor(
    private val api: SupervisorApi
) : ViewModel() {

    private val _stats = MutableLiveData<AccountantStats>()
    val stats: LiveData<AccountantStats> = _stats

    private val _pendingInvoices = MutableLiveData<List<Invoice>>()
    val pendingInvoices: LiveData<List<Invoice>> = _pendingInvoices

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun loadFinancialData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val statsRes = api.getAccountantStats()
                if (statsRes.isSuccessful) {
                    statsRes.body()?.data?.let { _stats.value = it }
                }

                val invoicesRes = api.getInvoices()
                if (invoicesRes.isSuccessful) {
                    // Filter or use specific accountant endpoint if available
                    invoicesRes.body()?.data?.let { _pendingInvoices.value = it }
                }

                _error.value = null
            } catch (e: Exception) {
                _error.value = "فشل في تحديث البيانات المالية"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
