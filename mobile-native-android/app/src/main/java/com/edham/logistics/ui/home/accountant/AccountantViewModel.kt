package com.edham.logistics.ui.home.accountant

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edham.logistics.core.network.api.*
import com.edham.logistics.data.repository.FinancialRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountantViewModel @Inject constructor(
    private val repository: FinancialRepository,
    private val latencyInterceptor: com.edham.logistics.core.network.LatencyInterceptor
) : ViewModel() {

    private val _systemLatency = MutableLiveData<Long>()
    val systemLatency: LiveData<Long> = _systemLatency

    fun refreshHealth() {
        _systemLatency.value = latencyInterceptor.getLastLatency()
    }

    private val _dashboardStats = MutableLiveData<AccountantDashboardStats>()
    val dashboardStats: LiveData<AccountantDashboardStats> = _dashboardStats

    private val _settlements = MutableLiveData<List<DriverSettlement>>()
    val settlements: LiveData<List<DriverSettlement>> = _settlements

    private val _debtAging = MutableLiveData<DebtAgingReport>()
    val debtAging: LiveData<DebtAgingReport> = _debtAging

    private val _workshopRequests = MutableLiveData<List<WorkshopFinancialRequest>>()
    val workshopRequests: LiveData<List<WorkshopFinancialRequest>> = _workshopRequests

    private val _soa = MutableLiveData<StatementOfAccount>()
    val soa: LiveData<StatementOfAccount> = _soa

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _voucherSubmitted = MutableLiveData<Boolean>()
    val voucherSubmitted: LiveData<Boolean> = _voucherSubmitted

    fun submitReceiptVoucher(clientName: String, amount: Double, method: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val res = repository.submitReceiptVoucher(clientName, amount, method)
                if (res.isSuccessful) {
                    _voucherSubmitted.value = true
                    loadDashboardData() // Refresh
                }
            } catch (e: Exception) {
                _error.value = "فشل حفظ سند القبض"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadDashboardData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val res = repository.getDashboardStats()
                if (res.isSuccessful) {
                    res.body()?.data?.let { _dashboardStats.value = it }
                }
            } catch (e: Exception) {
                _error.value = "فشل تحديث البيانات المالية"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadSettlements() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val res = repository.getDriverSettlements()
                if (res.isSuccessful) {
                    _settlements.value = res.body()?.data ?: emptyList()
                }
            } catch (e: Exception) {
                _error.value = "فشل جلب العهد"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun approveExpense(id: String) {
        viewModelScope.launch {
            try {
                val res = repository.approveExpense(id)
                if (res.isSuccessful) {
                    loadSettlements()
                    loadDashboardData() // Refresh stats
                }
            } catch (e: Exception) {
                _error.value = "فشل اعتماد المصروف"
            }
        }
    }

    fun rejectExpense(id: String, reason: String) {
        viewModelScope.launch {
            try {
                val res = repository.rejectExpense(id, reason)
                if (res.isSuccessful) {
                    loadSettlements()
                }
            } catch (e: Exception) {
                _error.value = "فشل رفض المصروف"
            }
        }
    }

    fun loadDebtReport() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val res = repository.getDebtAgingReport()
                if (res.isSuccessful) {
                    res.body()?.data?.let { _debtAging.value = it }
                }
            } catch (e: Exception) {
                _error.value = "فشل جلب تقرير الديون"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun collectPayment(id: String, amount: Double, method: String, notes: String? = null) {
        viewModelScope.launch {
            try {
                val res = repository.collectPayment(id, amount, method, notes)
                if (res.isSuccessful) {
                    loadDebtReport()
                    loadDashboardData()
                }
            } catch (e: Exception) {
                _error.value = "فشل تسجيل التحصيل"
            }
        }
    }

    fun loadWorkshopRequests() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val res = repository.getWorkshopRequests()
                if (res.isSuccessful) {
                    _workshopRequests.value = res.body()?.data ?: emptyList()
                }
            } catch (e: Exception) {
                _error.value = "فشل جلب طلبات الورشة"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun approveWorkshopRequest(id: String) {
        viewModelScope.launch {
            try {
                val res = repository.approveWorkshopRequest(id)
                if (res.isSuccessful) {
                    loadWorkshopRequests()
                }
            } catch (e: Exception) {
                _error.value = "فشل اعتماد طلب الورشة"
            }
        }
    }

    fun loadSoA(clientId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val res = repository.getStatementOfAccount(clientId)
                if (res.isSuccessful) {
                    res.body()?.data?.let { _soa.value = it }
                }
            } catch (e: Exception) {
                _error.value = "فشل جلب كشف الحساب"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
