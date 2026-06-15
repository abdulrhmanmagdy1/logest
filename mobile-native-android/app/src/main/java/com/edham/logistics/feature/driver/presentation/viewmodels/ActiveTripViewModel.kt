package com.edham.logistics.feature.driver.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edham.logistics.core.network.api.DriverApi
import com.edham.logistics.core.utils.Resource
import com.edham.logistics.feature.driver.data.models.PathAnalysis
import com.edham.logistics.feature.driver.data.models.Trip
import com.edham.logistics.feature.driver.data.models.analyzeTripPath
import com.edham.logistics.feature.driver.service.LocationForegroundService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ActiveTripViewModel @Inject constructor(
    private val api: DriverApi
) : ViewModel() {

    private val _trip = MutableStateFlow<Resource<Trip>>(Resource.Loading())
    val trip = _trip.asStateFlow()

    private val _pathAnalysis = MutableStateFlow<PathAnalysis?>(null)
    val pathAnalysis = _pathAnalysis.asStateFlow()

    private val _eta = MutableStateFlow("Recalculating...")
    val eta = _eta.asStateFlow()

    init {
        observeLocationUpdates()
    }

    fun loadTrip(tripId: String) {
        viewModelScope.launch {
            _trip.value = Resource.Loading()
            try {
                // Fetch active trips and find the specific one
                val response = api.getTrips("", status = "active")
                if (response.isSuccessful && response.body()?.success == true) {
                    val activeTrip = response.body()?.data?.find { it.tripId == tripId || it.id == tripId }
                    if (activeTrip != null) {
                        _trip.value = Resource.Success(activeTrip)
                        // Perform smart path analysis
                        _pathAnalysis.value = analyzeTripPath(emptyList())
                    } else {
                        _trip.value = Resource.Error("Trip not found")
                    }
                } else {
                    _trip.value = Resource.Error(response.message())
                }
            } catch (e: Exception) {
                _trip.value = Resource.Error(e.message ?: "Network error")
            }
        }
    }

    private fun observeLocationUpdates() {
        viewModelScope.launch {
            LocationForegroundService.locationFlow.collect { update ->
                val currentTrip = (_trip.value as? Resource.Success)?.data ?: return@collect
                recalculateETA(update.lat, update.lng, currentTrip.destLat, currentTrip.destLng)
            }
        }
    }

    private fun recalculateETA(curLat: Double, curLng: Double, destLat: Double, destLng: Double) {
        val distance = FloatArray(1)
        android.location.Location.distanceBetween(curLat, curLng, destLat, destLng, distance)
        val minutes = (distance[0] / 1000 / 40 * 60).toInt() // Assume 40km/h avg speed
        _eta.value = "$minutes دقيقة"
    }
}
