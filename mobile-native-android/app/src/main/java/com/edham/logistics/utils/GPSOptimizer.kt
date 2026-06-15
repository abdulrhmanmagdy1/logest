package com.edham.logistics.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import timber.log.Timber
import kotlin.math.max
import kotlin.math.min

/**
 * GPS Optimizer for Driver Experience
 * Optimizes GPS usage for battery efficiency and accuracy
 */
class GPSOptimizer(private val context: Context) : LocationListener {
    
    private val locationManager: LocationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    private val _gpsStatus = MutableLiveData<String>()
    val gpsStatus: LiveData<String> = _gpsStatus
    
    private val _currentLocation = MutableLiveData<Location>()
    val currentLocation: LiveData<Location> = _currentLocation
    
    private val _locationAccuracy = MutableLiveData<Float>()
    val locationAccuracy: LiveData<Float> = _locationAccuracy
    
    private var isTracking = false
    private var updateInterval = DEFAULT_UPDATE_INTERVAL
    private var accuracyThreshold = DEFAULT_ACCURACY_THRESHOLD
    private var lastLocation: Location? = null
    private var lastUpdateTime = 0L
    
    // Battery optimization levels
    private var optimizationLevel = OptimizationLevel.BALANCED
    
    companion object {
        private const val DEFAULT_UPDATE_INTERVAL = 5000L // 5 seconds
        private const val DEFAULT_ACCURACY_THRESHOLD = 20f // 20 meters
        private const val MIN_TIME_BETWEEN_UPDATES = 2000L // 2 seconds minimum
        private const val FASTEST_UPDATE_INTERVAL = 2000L // 2 seconds
        private const val MAX_AGE_OF_LOCATION = 30000L // 30 seconds
        private const val TWO_MINUTES = 1000 * 60 * 2 // 2 minutes
    }
    
    enum class OptimizationLevel(val intervalMs: Long, val accuracyThreshold: Float, val description: String) {
        HIGH_PERFORMANCE(2000L, 10f, "High Performance"),
        BALANCED(5000L, 20f, "Balanced"),
        POWER_SAVING(10000L, 50f, "Power Saving"),
        CRITICAL_POWER_SAVING(30000L, 100f, "Critical Power Saving")
    }
    
    /**
     * Optimize GPS settings for driver usage
     */
    fun optimizeForDriver() {
        optimizationLevel = OptimizationLevel.BALANCED
        updateInterval = optimizationLevel.intervalMs
        accuracyThreshold = optimizationLevel.accuracyThreshold
        
        Timber.d("GPS optimized for driver: ${optimizationLevel.description}")
        updateGPSStatus()
    }
    
    /**
     * Set optimization level based on battery or user preference
     */
    fun setOptimizationLevel(level: OptimizationLevel) {
        optimizationLevel = level
        updateInterval = level.intervalMs
        accuracyThreshold = level.accuracyThreshold
        
        if (isTracking) {
            restartLocationUpdates()
        }
        
        Timber.d("GPS optimization level changed to: ${level.description}")
        updateGPSStatus()
    }
    
    /**
     * Start location updates with optimized settings
     */
    fun startLocationUpdates() {
        if (!hasLocationPermission()) {
            Timber.w("Location permission not granted")
            return
        }
        
        try {
            // Check if GPS is enabled
            val isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            
            if (!isGPSEnabled && !isNetworkEnabled) {
                _gpsStatus.value = "OFFLINE"
                Timber.w("Location providers are disabled")
                return
            }
            
            // Use GPS as primary provider, network as fallback
            if (isGPSEnabled) {
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    updateInterval,
                    0f,
                    this,
                    Looper.getMainLooper()
                )
            }
            
            if (isNetworkEnabled) {
                locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    updateInterval,
                    0f,
                    this,
                    Looper.getMainLooper()
                )
            }
            
            isTracking = true
            updateGPSStatus()
            Timber.d("Location updates started with interval: ${updateInterval}ms")
            
        } catch (e: SecurityException) {
            Timber.e(e, "Security exception while requesting location updates")
            _gpsStatus.value = "ERROR"
        } catch (e: Exception) {
            Timber.e(e, "Exception while starting location updates")
            _gpsStatus.value = "ERROR"
        }
    }
    
    /**
     * Stop location updates to save battery
     */
    fun stopLocationUpdates() {
        try {
            locationManager.removeUpdates(this)
            isTracking = false
            updateGPSStatus()
            Timber.d("Location updates stopped")
        } catch (e: Exception) {
            Timber.e(e, "Exception while stopping location updates")
        }
    }
    
    /**
     * Restart location updates with new settings
     */
    private fun restartLocationUpdates() {
        stopLocationUpdates()
        startLocationUpdates()
    }
    
    /**
     * Get current location with accuracy check
     */
    fun getCurrentLocation(): Location? {
        val location = _currentLocation.value
        val accuracy = _locationAccuracy.value ?: Float.MAX_VALUE
        
        return if (location != null && accuracy <= accuracyThreshold) {
            location
        } else {
            null
        }
    }
    
    /**
     * Get location quality assessment
     */
    fun getLocationQuality(): String {
        val accuracy = _locationAccuracy.value ?: Float.MAX_VALUE
        
        return when {
            accuracy <= 10f -> "EXCELLENT"
            accuracy <= 20f -> "GOOD"
            accuracy <= 50f -> "FAIR"
            else -> "POOR"
        }
    }
    
    /**
     * Check if location is fresh enough
     */
    fun isLocationFresh(): Boolean {
        val currentTime = System.currentTimeMillis()
        return lastLocation != null && (currentTime - lastUpdateTime) < MAX_AGE_OF_LOCATION
    }
    
    /**
     * Get distance between two locations in meters
     */
    fun calculateDistance(loc1: Location?, loc2: Location?): Float {
        if (loc1 == null || loc2 == null) return 0f
        return loc1.distanceTo(loc2)
    }
    
    /**
     * Check if location has moved significantly
     */
    fun hasSignificantMovement(newLocation: Location): Boolean {
        val oldLocation = lastLocation
        return oldLocation == null || calculateDistance(oldLocation, newLocation) > 10f
    }
    
    // LocationListener implementation
    override fun onLocationChanged(location: Location) {
        val currentTime = System.currentTimeMillis()
        
        // Filter out old locations
        if (currentTime - lastUpdateTime < MIN_TIME_BETWEEN_UPDATES) {
            return
        }
        
        // Filter out inaccurate locations
        if (location.accuracy > accuracyThreshold) {
            Timber.d("Location filtered out due to poor accuracy: ${location.accuracy}m")
            return
        }
        
        // Check if this is a better location than the last one
        if (isBetterLocation(location, lastLocation)) {
            lastLocation = location
            lastUpdateTime = currentTime
            
            _currentLocation.value = location
            _locationAccuracy.value = location.accuracy
            
            Timber.d("New location: ${location.latitude}, ${location.longitude} (Accuracy: ${location.accuracy}m)")
            updateGPSStatus()
        }
    }
    
    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        when (status) {
            android.location.LocationProvider.AVAILABLE -> {
                Timber.d("Location provider $provider is available")
                updateGPSStatus()
            }
            android.location.LocationProvider.OUT_OF_SERVICE -> {
                Timber.w("Location provider $provider is out of service")
                updateGPSStatus()
            }
            android.location.LocationProvider.TEMPORARILY_UNAVAILABLE -> {
                Timber.w("Location provider $provider is temporarily unavailable")
                updateGPSStatus()
            }
        }
    }
    
    override fun onProviderEnabled(provider: String) {
        Timber.d("Location provider $provider enabled")
        updateGPSStatus()
    }
    
    override fun onProviderDisabled(provider: String) {
        Timber.w("Location provider $provider disabled")
        updateGPSStatus()
    }
    
    /**
     * Determine if new location is better than the current one
     */
    private fun isBetterLocation(newLocation: Location, currentBestLocation: Location?): Boolean {
        if (currentBestLocation == null) {
            return true
        }
        
        // Check if new location is significantly newer
        val timeDelta = newLocation.time - currentBestLocation.time
        val isSignificantlyNewer = timeDelta > TWO_MINUTES
        val isSignificantlyOlder = timeDelta < -TWO_MINUTES
        val isNewer = timeDelta > 0
        
        if (isSignificantlyNewer) {
            return true
        } else if (isSignificantlyOlder) {
            return false
        }
        
        // Check accuracy
        val accuracyDelta = newLocation.accuracy - currentBestLocation.accuracy
        val isLessAccurate = accuracyDelta > 0f
        val isMoreAccurate = accuracyDelta < 0f
        val isSignificantlyLessAccurate = accuracyDelta > 200f
        
        if (isMoreAccurate) {
            return true
        } else if (isNewer && !isLessAccurate) {
            return true
        } else if (isNewer && !isSignificantlyLessAccurate) {
            return true
        }
        
        return false
    }
    
    /**
     * Update GPS status based on current conditions
     */
    private fun updateGPSStatus() {
        val status = when {
            !hasLocationPermission() -> "NO_PERMISSION"
            !isTracking -> "STOPPED"
            !isLocationFresh() -> "STALE"
            getLocationQuality() == "EXCELLENT" -> "EXCELLENT"
            getLocationQuality() == "GOOD" -> "GOOD"
            getLocationQuality() == "FAIR" -> "FAIR"
            else -> "POOR"
        }
        
        _gpsStatus.value = status
    }
    
    /**
     * Check if location permission is granted
     */
    private fun hasLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    /**
     * Get current optimization level
     */
    fun getOptimizationLevel(): OptimizationLevel = optimizationLevel
    
    /**
     * Get battery usage estimate
     */
    fun getBatteryUsageEstimate(): String {
        return when (optimizationLevel) {
            OptimizationLevel.HIGH_PERFORMANCE -> "High"
            OptimizationLevel.BALANCED -> "Medium"
            OptimizationLevel.POWER_SAVING -> "Low"
            OptimizationLevel.CRITICAL_POWER_SAVING -> "Very Low"
        }
    }
    
    /**
     * Cleanup resources
     */
    fun cleanup() {
        stopLocationUpdates()
        lastLocation = null
        lastUpdateTime = 0L
    }
}
