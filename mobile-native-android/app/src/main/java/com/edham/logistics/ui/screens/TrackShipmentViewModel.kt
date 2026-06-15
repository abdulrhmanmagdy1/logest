package com.edham.logistics.ui.screens

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edham.logistics.Load
import com.edham.logistics.core.network.api.ShipmentApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrackShipmentViewModel @Inject constructor(
    private val shipmentApi: ShipmentApi
) : ViewModel() {

    private val _shipment = MutableLiveData<Load>()
    val shipment: LiveData<Load> = _shipment

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private var isTracking = false

    fun startTracking(shipmentId: String) {
        isTracking = true
        viewModelScope.launch {
            while (isTracking) {
                try {
                    val response = shipmentApi.getShipment(shipmentId)
                    if (response.isSuccessful) {
                        response.body()?.data?.let {
                            _shipment.value = it
                        }
                    }
                } catch (e: Exception) {
                    _error.value = "فشل تحديث موقع الشحنة"
                }
                delay(15000) // Poll every 15 seconds
            }
        }
    }

    fun stopTracking() {
        isTracking = false
    }

    override fun onCleared() {
        super.onCleared()
        stopTracking()
    }
}
