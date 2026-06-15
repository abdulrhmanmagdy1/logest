package com.edham.logistics.feature.driver.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edham.logistics.core.network.api.DriverApi
import com.edham.logistics.core.utils.Resource
import com.edham.logistics.core.utils.TokenManager
import com.edham.logistics.feature.driver.data.models.DriverStats
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
class DriverDashboardViewModel @Inject constructor(
    private val repository: TripRepository,
    private val api: DriverApi
) : ViewModel() {

    private val _stats = MutableStateFlow<Resource<DriverStats>>(Resource.Loading())
    val stats = _stats.asStateFlow()

    private val _trips = MutableStateFlow<Resource<List<Trip>>>(Resource.Loading())
    val trips = _trips.asStateFlow()

    init {
        refreshData()
    }

    fun refreshData() {
        val driverId = TokenManager.getUserId() ?: return
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        viewModelScope.launch {
            _stats.value = Resource.Loading()
            try {
                val response = api.getDriverStats(driverId, today)
                if (response.isSuccessful && response.body()?.success == true) {
                    _stats.value = Resource.Success(response.body()!!.data!!)
                } else {
                    _stats.value = Resource.Error(response.message())
                }
            } catch (e: Exception) {
                _stats.value = Resource.Error(e.message ?: "Network error")
            }
        }

        viewModelScope.launch {
            repository.getTrips(driverId, today).collect {
                _trips.value = it
            }
        }
    }
}
