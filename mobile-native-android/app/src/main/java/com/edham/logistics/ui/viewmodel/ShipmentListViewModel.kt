package com.edham.logistics.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edham.logistics.Load
import com.edham.logistics.MockData
import com.edham.logistics.core.di.ServiceLocator
import com.edham.logistics.core.network.api.ShipmentApi
import kotlinx.coroutines.launch

/**
 * ViewModel for shipment lists. Falls back to [MockData] when the API is unavailable.
 */
class ShipmentListViewModel : ViewModel() {

    private val _shipments = MutableLiveData<List<Load>>(emptyList())
    val shipments: LiveData<List<Load>> = _shipments

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    private val api: ShipmentApi by lazy { ServiceLocator.api() }

    fun loadShipments(status: String? = null) {
        _loading.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                val response = api.getShipments(status = status)
                if (response.isSuccessful) {
                    val body = response.body()
                    _shipments.value = body?.data?.data ?: emptyList()
                } else {
                    // Graceful fallback to mock data until backend is live
                    _shipments.value = MockData.loads.filter {
                        status == null || it.status.equals(status, ignoreCase = true)
                    }
                }
            } catch (e: Exception) {
                // Network / init failure → mock fallback
                _shipments.value = MockData.loads.filter {
                    status == null || it.status.equals(status, ignoreCase = true)
                }
            } finally {
                _loading.value = false
            }
        }
    }
}
