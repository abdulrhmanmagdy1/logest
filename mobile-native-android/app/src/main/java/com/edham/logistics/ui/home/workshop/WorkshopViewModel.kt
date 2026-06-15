package com.edham.logistics.ui.home.workshop

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edham.logistics.core.network.api.MaintenanceRecord
import com.edham.logistics.core.network.api.PartItem
import com.edham.logistics.core.network.api.SupervisorApi
import com.edham.logistics.core.network.api.SupervisorStats
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkshopViewModel @Inject constructor(
    private val api: SupervisorApi
) : ViewModel() {

    private val _stats = MutableLiveData<SupervisorStats>()
    val stats: LiveData<SupervisorStats> = _stats

    private val _currentJobs = MutableLiveData<List<MaintenanceRecord>>()
    val currentJobs: LiveData<List<MaintenanceRecord>> = _currentJobs

    private val _partsStock = MutableLiveData<List<PartItem>>()
    val partsStock: LiveData<List<PartItem>> = _partsStock

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun loadWorkshopDashboard() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Use supervisor stats for common metrics like maintenance alerts
                val statsRes = api.getStats()
                if (statsRes.isSuccessful) {
                    statsRes.body()?.data?.let { _stats.value = it }
                }

                val jobsRes = api.getMaintenanceRecords()
                if (jobsRes.isSuccessful) {
                    jobsRes.body()?.data?.let { _currentJobs.value = it }
                }

                val partsRes = api.getPartsInventory()
                if (partsRes.isSuccessful) {
                    partsRes.body()?.data?.let { _partsStock.value = it }
                }

                _error.value = null
            } catch (e: Exception) {
                _error.value = "فشل في تحديث بيانات الورشة"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
