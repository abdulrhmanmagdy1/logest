package com.edham.logistics.ui.home.supervisor

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edham.logistics.core.network.api.*
import com.edham.logistics.feature.driver.data.models.Trip
import com.edham.logistics.feature.driver.data.models.DriverProfile
import com.edham.logistics.data.repository.SupervisorRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SupervisorViewModel @Inject constructor(
    private val repository: SupervisorRepository,
    private val shipmentApi: com.edham.logistics.core.network.api.ShipmentApi,
    private val latencyInterceptor: com.edham.logistics.core.network.LatencyInterceptor
) : ViewModel() {

    private val _systemLatency = MutableLiveData<Long>()
    val systemLatency: LiveData<Long> = _systemLatency

    fun refreshSystemHealth() {
        _systemLatency.value = latencyInterceptor.getLastLatency()
    }

    private val _stats = MutableLiveData<SupervisorStats>()
    val stats: LiveData<SupervisorStats> = _stats

    private val _invoices = MutableLiveData<List<Invoice>>()
    val invoices: LiveData<List<Invoice>> = _invoices

    private val _maintenanceRecords = MutableLiveData<List<MaintenanceRecord>>()
    val maintenanceRecords: LiveData<List<MaintenanceRecord>> = _maintenanceRecords

    private val _parts = MutableLiveData<List<PartItem>>()
    val parts: LiveData<List<PartItem>> = _parts

    private val _revenueChart = MutableLiveData<List<ChartData>>()
    val revenueChart: LiveData<List<ChartData>> = _revenueChart

    private val _vehicles = MutableLiveData<List<VehicleItem>>()
    val vehicles: LiveData<List<VehicleItem>> = _vehicles

    private val _drivers = MutableLiveData<List<DriverProfile>>()
    val drivers: LiveData<List<DriverProfile>> = _drivers

    private val _locations = MutableLiveData<List<Map<String, Any>>>()
    val locations: LiveData<List<Map<String, Any>>> = _locations

    private val _trips = MutableLiveData<List<Trip>>()
    val trips: LiveData<List<Trip>> = _trips

    private val _alerts = MutableLiveData<List<SmartAlert>>()
    val alerts: LiveData<List<SmartAlert>> = _alerts

    private val _surveys = MutableLiveData<List<com.edham.logistics.feature.driver.data.models.SurveySubmission>>()
    val surveys: LiveData<List<com.edham.logistics.feature.driver.data.models.SurveySubmission>> = _surveys

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _activeShipment = MutableLiveData<Trip?>()
    val activeShipment: LiveData<Trip?> = _activeShipment

    private val _pendingOrders = MutableLiveData<List<com.edham.logistics.Load>>()
    val pendingOrders: LiveData<List<com.edham.logistics.Load>> = _pendingOrders

    fun loadPendingOrders() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = shipmentApi.getShipments(status = "PENDING")
                if (response.isSuccessful) {
                    _pendingOrders.value = response.body()?.data?.data ?: emptyList()
                }
            } catch (e: Exception) {
                _error.value = "فشل في جلب الطلبات المعلقة"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private val _auditLog = MutableLiveData<List<AuditEntry>>()
    val auditLog: LiveData<List<AuditEntry>> = _auditLog

    fun updatePrice(shipmentId: String, price: Double, notes: String? = null) {
        viewModelScope.launch {
            try {
                val res = repository.updateShipmentPrice(shipmentId, price, notes)
                if (res.isSuccessful) {
                    _error.value = "تم تحديث السعر بنجاح"
                }
            } catch (e: Exception) {
                _error.value = "فشل تحديث السعر"
            }
        }
    }

    fun loadAuditLog(shipmentId: String) {
        viewModelScope.launch {
            try {
                val res = repository.getShipmentAuditLog(shipmentId)
                if (res.isSuccessful) {
                    res.body()?.data?.let { _auditLog.value = it }
                }
            } catch (e: Exception) {
                _error.value = "فشل تحميل سجل العمليات"
            }
        }
    }

    fun loadDashboardData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val statsRes = repository.getStats()
                if (statsRes.isSuccessful) {
                    statsRes.body()?.data?.let { _stats.value = it }
                }

                val driversRes = repository.getDrivers()
                if (driversRes.isSuccessful) {
                    driversRes.body()?.data?.let { _drivers.value = it }
                }

                val alertsRes = repository.getSmartAlerts()
                if (alertsRes.isSuccessful) {
                    alertsRes.body()?.data?.let { _alerts.value = it }
                }

                _error.value = null
            } catch (e: Exception) {
                _error.value = "فشل في تحديث لوحة التحكم"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadActiveShipment(driverId: Long) {
        viewModelScope.launch {
            try {
                val res = repository.getActiveShipmentByDriver(driverId)
                if (res.isSuccessful) {
                    _activeShipment.value = res.body()?.data
                }
            } catch (e: Exception) {
                _error.value = "فشل في جلب بيانات الشحنة"
            }
        }
    }

    fun loadMaintenanceData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.getMaintenanceRecords()
                if (response.isSuccessful) {
                    response.body()?.data?.let { _maintenanceRecords.value = it }
                }
            } catch (e: Exception) {
                _error.value = "فشل في جلب بيانات الصيانة"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadPartsData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.getPartsInventory()
                if (response.isSuccessful) {
                    response.body()?.data?.let { _parts.value = it }
                }
            } catch (e: Exception) {
                _error.value = "فشل في جلب بيانات المستودع"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadReportsData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.getRevenueReport()
                if (response.isSuccessful) {
                    response.body()?.data?.let { _revenueChart.value = it }
                }
            } catch (e: Exception) {
                _error.value = "فشل في جلب تقارير الإيرادات"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadFleetData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.getVehicles()
                if (response.isSuccessful) {
                    response.body()?.data?.let { _vehicles.value = it }
                }
            } catch (e: Exception) {
                _error.value = "فشل في جلب بيانات الأسطول"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addVehicle(vehicle: VehicleItem) {
        viewModelScope.launch {
            try {
                val response = repository.addVehicle(vehicle)
                if (response.isSuccessful) {
                    loadFleetData()
                }
            } catch (e: Exception) {
                _error.value = "فشل في إضافة المركبة"
            }
        }
    }

    fun loadInvoices() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.getInvoices()
                if (response.isSuccessful) {
                    response.body()?.data?.let { _invoices.value = it }
                    _error.value = null
                }
            } catch (e: Exception) {
                _error.value = "فشل في جلب الفواتير"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadActiveTrips() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.getOrders("ACTIVE")
                if (response.isSuccessful) {
                    response.body()?.data?.let { _trips.value = it }
                }
            } catch (e: Exception) {
                _error.value = "فشل في جلب الرحلات النشطة"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun assignDriver(tripId: String, driverId: String) {
        viewModelScope.launch {
            try {
                val response = repository.assignTrip(tripId, driverId)
                if (response.isSuccessful) {
                    loadActiveTrips() // Refresh
                }
            } catch (e: Exception) {
                _error.value = "فشل في تعيين السائق"
            }
        }
    }

    fun updateTripStatus(tripId: String, status: String) {
        viewModelScope.launch {
            try {
                val response = shipmentApi.updateStatus(tripId, status)
                if (response.isSuccessful) {
                    _error.value = "تم تحديث الحالة بنجاح"
                    loadActiveTrips()
                } else {
                    _error.value = "خطأ في التحديث: ${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = "فشل تحديث الحالة"
            }
        }
    }

    fun loadLocations() {
        viewModelScope.launch {
            try {
                val res = repository.getAllDriverLocations()
                if (res.isSuccessful) {
                    val locs = res.body()?.data ?: emptyList()
                    _locations.value = locs
                    checkForCriticalAlerts(locs)
                }
            } catch (e: Exception) {
                // Silently fail for background tracking
            }
        }
    }

    private fun checkForCriticalAlerts(locations: List<Map<String, Any>>) {
        locations.forEach { loc ->
            val temp = loc["temperature"] as? Double
            val driverName = loc["driverName"] as? String ?: "سائق"
            val lat = loc["latitude"] as? Double ?: return@forEach
            val lng = loc["longitude"] as? Double ?: return@forEach
            val destLat = loc["destLatitude"] as? Double
            val destLng = loc["destLongitude"] as? Double
            
            // 1. Temperature Alert
            if (temp != null && temp > -5.0) {
                _error.postValue("🚨 تنبيه حرارة: الشاحنة الخاصة بـ $driverName ارتفعت حرارتها إلى $temp°م")
            }

            // 2. Geofencing Alert (Simplified path deviation check)
            if (destLat != null && destLng != null) {
                val distanceToDest = com.edham.logistics.core.utils.GeoUtils.getDistance(lat, lng, destLat, destLng)
                // If the logic detects unusual movement (e.g. static for too long or extreme detour)
                // This is a placeholder for a more complex route-matching algorithm
            }
        }
    }

    fun loadSurveyData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.getAllSurveys()
                if (response.isSuccessful) {
                    response.body()?.data?.let { _surveys.value = it }
                }
            } catch (e: Exception) {
                _error.value = "فشل في جلب الاستبيانات"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
