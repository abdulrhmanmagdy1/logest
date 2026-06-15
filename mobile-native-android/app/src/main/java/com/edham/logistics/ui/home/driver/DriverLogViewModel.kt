package com.edham.logistics.ui.home.driver

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edham.logistics.feature.driver.data.models.Trip
import com.edham.logistics.core.network.api.DriverApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DriverLogViewModel @Inject constructor(
    private val api: DriverApi
) : ViewModel() {

    private val _logs = MutableLiveData<List<Trip>>()
    val logs: LiveData<List<Trip>> = _logs

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun loadHistory(driverId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val res = api.getTrips(driverId, status = "completed")
                if (res.isSuccessful) {
                    _logs.value = res.body()?.data ?: emptyList()
                }
            } catch (e: Exception) {
                // Error handling
            } finally {
                _isLoading.value = false
            }
        }
    }
}
