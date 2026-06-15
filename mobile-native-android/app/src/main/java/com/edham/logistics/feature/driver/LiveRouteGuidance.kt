package com.edham.logistics.feature.driver

import android.content.Context
import android.location.Location
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import timber.log.Timber
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LiveRouteGuidance(private val context: Context) {
    
    private val _currentRoute = MutableStateFlow<List<LatLng>>(emptyList())
    val currentRoute: StateFlow<List<LatLng>> = _currentRoute
    
    private val _currentInstruction = MutableStateFlow<String>("")
    val currentInstruction: StateFlow<String> = _currentInstruction
    
    private val _distanceToDestination = MutableStateFlow<Float>(0f)
    val distanceToDestination: StateFlow<Float> = _distanceToDestination
    
    private val _estimatedTimeOfArrival = MutableStateFlow<Long>(0L)
    val estimatedTimeOfArrival: StateFlow<Long> = _estimatedTimeOfArrival
    
    private var isGuidanceActive = false
    
    fun startRouteGuidance(route: List<LatLng>, destination: String) {
        _currentRoute.value = route
        isGuidanceActive = true
        
        Timber.d("Route guidance started to $destination with ${route.size} waypoints")
        updateNavigationInstructions()
    }
    
    fun updateCurrentLocation(location: Location) {
        if (!isGuidanceActive) return
        
        val currentLatLng = LatLng(location.latitude, location.longitude)
        val route = _currentRoute.value
        
        if (route.isNotEmpty()) {
            val distance = calculateDistance(currentLatLng, route.last())
            _distanceToDestination.value = distance
            
            val eta = calculateETA(distance, location.speed)
            _estimatedTimeOfArrival.value = eta
            
            updateNavigationInstructions(currentLatLng)
        }
    }
    
    private fun updateNavigationInstructions(currentLocation: LatLng? = null) {
        val instruction = when {
            _distanceToDestination.value < 50f -> "You have arrived at your destination"
            _distanceToDestination.value < 200f -> "Destination is ahead"
            _distanceToDestination.value < 500f -> "Approaching destination"
            else -> "Continue on route"
        }
        
        _currentInstruction.value = instruction
    }
    
    private fun calculateDistance(start: LatLng, end: LatLng): Float {
        val results = FloatArray(1)
        Location.distanceBetween(
            start.latitude, start.longitude,
            end.latitude, end.longitude,
            results
        )
        return results[0]
    }
    
    private fun calculateETA(distance: Float, speed: Float): Long {
        if (speed <= 0) return 0L
        return (distance / speed * 1000).toLong()
    }
    
    fun stopRouteGuidance() {
        isGuidanceActive = false
        _currentRoute.value = emptyList()
        _currentInstruction.value = ""
        _distanceToDestination.value = 0f
        _estimatedTimeOfArrival.value = 0L
        
        Timber.d("Route guidance stopped")
    }
    
    fun getRoutePolylineOptions(): PolylineOptions {
        return PolylineOptions()
            .addAll(_currentRoute.value)
            .color(android.graphics.Color.BLUE)
            .width(8f)
    }
}
