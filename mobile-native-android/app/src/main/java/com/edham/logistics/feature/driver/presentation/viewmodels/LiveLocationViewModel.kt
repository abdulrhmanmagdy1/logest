package com.edham.logistics.feature.driver.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edham.logistics.feature.driver.data.models.LocationUpdate
import com.edham.logistics.feature.driver.service.LocationForegroundService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LiveLocationViewModel @Inject constructor() : ViewModel() {

    private val _locationUpdate = MutableStateFlow<LocationUpdate?>(null)
    val locationUpdate = _locationUpdate.asStateFlow()

    private val _lastUpdatedSeconds = MutableStateFlow(0)
    val lastUpdatedSeconds = _lastUpdatedSeconds.asStateFlow()

    private var timerJob: Job? = null

    init {
        viewModelScope.launch {
            LocationForegroundService.locationFlow.collect { update ->
                _locationUpdate.value = update
                startTimer()
            }
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        _lastUpdatedSeconds.value = 0
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                _lastUpdatedSeconds.value += 1
            }
        }
    }
}
