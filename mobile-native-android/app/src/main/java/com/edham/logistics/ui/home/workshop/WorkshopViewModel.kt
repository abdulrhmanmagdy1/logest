package com.edham.logistics.ui.home.workshop

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edham.logistics.core.network.api.*
import com.edham.logistics.data.repository.WorkshopRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkshopViewModel @Inject constructor(
    private val repository: WorkshopRepository,
    private val latencyInterceptor: com.edham.logistics.core.network.LatencyInterceptor
) : ViewModel() {

    private val _systemLatency = MutableLiveData<Long>()
    val systemLatency: LiveData<Long> = _systemLatency

    fun refreshHealth() {
        _systemLatency.value = latencyInterceptor.getLastLatency()
    }

    private val _stats = MutableLiveData<WorkshopStats>()
    val stats: LiveData<WorkshopStats> = _stats

    private val _inventory = MutableLiveData<List<PartItem>>()
    val inventory: LiveData<List<PartItem>> = _inventory

    private val _records = MutableLiveData<List<MaintenanceRecord>>()
    val records: LiveData<List<MaintenanceRecord>> = _records

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _vehicles = MutableLiveData<List<VehicleItem>>()
    val vehicles: LiveData<List<VehicleItem>> = _vehicles

    fun releaseVehicle(id: String) {
        viewModelScope.launch {
            try {
                val res = repository.releaseVehicle(id)
                if (res.isSuccessful) {
                    loadFleetData()
                }
            } catch (e: Exception) {
                _error.value = "فشل إعادة المركبة"
            }
        }
    }

    fun loadFleetData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val res = repository.getVehicles()
                if (res.isSuccessful) {
                    _vehicles.value = res.body()?.data ?: emptyList()
                }
            } catch (e: Exception) {
                _error.value = "فشل جلب بيانات الأسطول"
            }
            finally {
                _isLoading.value = false
            }
        }
    }

    private val _oilChangeAlerts = MutableLiveData<List<String>>()
    val oilChangeAlerts: LiveData<List<String>> = _oilChangeAlerts

    fun checkOilChanges() {
        viewModelScope.launch {
            try {
                val res = repository.getVehicles()
                if (res.isSuccessful) {
                    val alerts = res.body()?.data
                        ?.filter { (it.mileage % 5000) > 4500 } // تنبيه قبل 500 كم من الموعد (كل 5000 كم)
                        ?.map { "مركبة ${it.plateNumber} تحتاج لتغيير زيت قريباً (المسافة: ${it.mileage} كم)" }
                        ?: emptyList()
                    _oilChangeAlerts.value = alerts
                }
            } catch (e: Exception) {}
        }
    }

    fun loadDashboardData() {
        checkOilChanges()
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val statsRes = repository.getWorkshopStats()
                if (statsRes.isSuccessful) {
                    statsRes.body()?.data?.let { _stats.value = it }
                }
            } catch (e: Exception) {
                _error.value = "فشل في تحديث بيانات الورشة"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadMaintenanceTasks() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // For now reuse stats but we can add a specific repo call
                _error.value = null
            } catch (e: Exception) {}
            finally {
                _isLoading.value = false
            }
        }
    }

    fun loadInventory() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val res = repository.getInventory()
                if (res.isSuccessful) {
                    val items = res.body()?.data ?: emptyList()
                    _inventory.value = items
                    
                    // Auto-sync with Accountant for critical stock
                    items.filter { it.quantity < it.minQuantity }.forEach { part ->
                        requestPart(part.code, 10, "HIGH") // Draft request
                    }
                }
            } catch (e: Exception) {
                _error.value = "فشل جلب المخزون"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun groundVehicle(id: String, reason: String) {
        viewModelScope.launch {
            try {
                val res = repository.groundVehicle(id, reason)
                if (res.isSuccessful) {
                    loadDashboardData()
                }
            } catch (e: Exception) {
                _error.value = "فشل تأريض المركبة"
            }
        }
    }

    fun requestPart(partId: String, qty: Int, priority: String) {
        viewModelScope.launch {
            try {
                val res = repository.requestPart(partId, qty, priority)
                if (res.isSuccessful) {
                    loadInventory()
                }
            } catch (e: Exception) {
                _error.value = "فشل طلب القطعة"
            }
        }
    }
}
