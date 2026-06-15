package com.edham.logistics.feature.driver.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edham.logistics.data.repository.DriverRepository
import com.edham.logistics.feature.driver.data.models.DriverStats
import com.edham.logistics.feature.driver.data.models.Trip
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DriverDashboardViewModel @Inject constructor(
    private val repository: DriverRepository
) : ViewModel() {

    private val _stats = MutableLiveData<DriverStats>()
    val stats: LiveData<DriverStats> = _stats

    private val _activeTrip = MutableLiveData<Trip?>()
    val activeTrip: LiveData<Trip?> = _activeTrip

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _isSosSent = MutableLiveData<Boolean>()
    val isSosSent: LiveData<Boolean> = _isSosSent

    // Legacy Support for DriverDashboardFragment using LiveData
    private val _profile = MutableLiveData<com.edham.logistics.core.utils.Resource<com.edham.logistics.feature.driver.data.models.DriverProfile>>()
    val profile: LiveData<com.edham.logistics.core.utils.Resource<com.edham.logistics.feature.driver.data.models.DriverProfile>> = _profile

    private val _newAssignment = MutableLiveData<Trip?>()
    val newAssignment: LiveData<Trip?> = _newAssignment

    private val _tripsResource = MutableLiveData<com.edham.logistics.core.utils.Resource<List<Trip>>>()
    val trips: LiveData<com.edham.logistics.core.utils.Resource<List<Trip>>> = _tripsResource

    private val _statsResource = MutableLiveData<com.edham.logistics.core.utils.Resource<com.edham.logistics.feature.driver.data.models.DriverStats>>()
    val statsResource: LiveData<com.edham.logistics.core.utils.Resource<com.edham.logistics.feature.driver.data.models.DriverStats>> = _statsResource

    fun refreshData() {
        // Implementation stub for legacy refresh
    }

    fun acceptAssignment(tripId: String) {
        // Implementation stub for legacy accept
    }

    fun rejectAssignment(tripId: String, reason: String) {
        // Implementation stub for legacy reject
    }

    fun loadData(driverId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val statsRes = repository.getDriverStats(driverId)
                if (statsRes.isSuccessful) {
                    statsRes.body()?.data?.let { _stats.value = it }
                }

                val tripsRes = repository.getTrips(driverId, status = "ACTIVE")
                if (tripsRes.isSuccessful) {
                    _activeTrip.value = tripsRes.body()?.data?.firstOrNull()
                }
            } catch (e: Exception) {
                _error.value = "فشل تحديث قمرة القيادة"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun sendSOS(driverId: String, lat: Double, lng: Double) {
        viewModelScope.launch {
            try {
                // In real app, we'd send location + other data
                repository.reportTelemetry(driverId, 100, 100, -18.0) 
                _isSosSent.value = true
            } catch (e: Exception) {
                _error.value = "فشل إرسال استغاثة"
            }
        }
    }

    fun startTrip(tripId: String) {
        viewModelScope.launch {
            try {
                val res = repository.updateTripStatus(tripId, "STARTED")
                if (res.isSuccessful) {
                    // Refresh data
                }
            } catch (e: Exception) {
                _error.value = "فشل بدء الرحلة"
            }
        }
    }
}
