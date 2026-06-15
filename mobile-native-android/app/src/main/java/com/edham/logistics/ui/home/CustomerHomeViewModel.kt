package com.edham.logistics.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edham.logistics.Load
import com.edham.logistics.core.network.api.Invoice
import com.edham.logistics.core.network.api.CustomerStats
import com.edham.logistics.data.repository.CustomerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CustomerHomeViewModel @Inject constructor(
    private val repository: CustomerRepository
) : ViewModel() {

    private val _stats = MutableLiveData<CustomerStats>()
    val stats: LiveData<CustomerStats> = _stats

    private val _recentShipments = MutableLiveData<List<Load>>()
    val recentShipments: LiveData<List<Load>> = _recentShipments

    private val _walletBalance = MutableLiveData<Double>()
    val walletBalance: LiveData<Double> = _walletBalance

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _invoices = MutableLiveData<List<Invoice>>()
    val invoices: LiveData<List<Invoice>> = _invoices

    private val _trackingData = MutableLiveData<Load?>()
    val trackingData: LiveData<Load?> = _trackingData

    fun loadDashboardData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val statsRes = repository.getCustomerStats()
                if (statsRes.isSuccessful) {
                    statsRes.body()?.data?.let {
                        _stats.value = it
                        _walletBalance.value = it.wallet_balance
                    }
                }

                val shipmentsRes = repository.getRecentShipments()
                if (shipmentsRes.isSuccessful) {
                    _recentShipments.value = shipmentsRes.body()?.data?.data ?: emptyList()
                }

                val invoicesRes = repository.getInvoices()
                if (invoicesRes.isSuccessful) {
                    _invoices.value = invoicesRes.body()?.data ?: emptyList()
                }
                
                _error.value = null
            } catch (e: Exception) {
                _error.value = "فشل في تحديث البيانات"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadTrackingInfo(shipmentId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val res = repository.getRecentShipments()
                if (res.isSuccessful) {
                    val load = res.body()?.data?.data?.find { it.id == shipmentId }
                    _trackingData.value = load
                }
            } catch (e: Exception) {
                _error.value = "تعذر تحميل بيانات التتبع"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
