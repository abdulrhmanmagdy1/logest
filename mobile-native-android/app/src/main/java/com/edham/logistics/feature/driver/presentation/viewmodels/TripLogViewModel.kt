package com.edham.logistics.feature.driver.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edham.logistics.core.network.api.DriverApi
import com.edham.logistics.core.utils.Resource
import com.edham.logistics.core.utils.TokenManager
import com.edham.logistics.feature.driver.data.models.Trip
import com.edham.logistics.feature.driver.data.repository.TripRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class TripLogViewModel @Inject constructor(
    private val repository: TripRepository,
    private val api: DriverApi
) : ViewModel() {

    private val _trips = MutableStateFlow<Resource<List<Trip>>>(Resource.Loading())
    val trips = _trips.asStateFlow()

    private val _cargoTypes = MutableStateFlow<List<String>>(emptyList())
    val cargoTypes = _cargoTypes.asStateFlow()

    private val _totalEarnings = MutableStateFlow(0.0)
    val totalEarnings = _totalEarnings.asStateFlow()

    private val _totalDistance = MutableStateFlow(0.0)
    val totalDistance = _totalDistance.asStateFlow()

    init {
        loadCargoTypes()
        loadTrips()
    }

    fun loadTrips() {
        val driverId = TokenManager.getUserId() ?: return
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        
        viewModelScope.launch {
            repository.getTrips(driverId, today).collect { resource ->
                _trips.value = resource
                if (resource is Resource.Success) {
                    _totalEarnings.value = resource.data?.sumOf { it.earnings } ?: 0.0
                    _totalDistance.value = resource.data?.sumOf { it.distance } ?: 0.0
                }
            }
        }
    }

    private fun loadCargoTypes() {
        viewModelScope.launch {
            try {
                val response = api.getCargoTypes()
                if (response.isSuccessful) {
                    _cargoTypes.value = response.body() ?: emptyList()
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}
