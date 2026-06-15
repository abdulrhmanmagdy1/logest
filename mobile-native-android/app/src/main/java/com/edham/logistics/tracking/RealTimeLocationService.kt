package com.edham.logistics.tracking

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.edham.logistics.R
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

/**
 * Real-time GPS Tracking Service for Edham Logistics
 * خدمة تتبع GPS في الوقت الفعلي لتطبيق إدهام اللوجستي
 */
class RealTimeLocationService : LifecycleService(), LocationListener {

    companion object {
        private const val TAG = "RealTimeLocationService"
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "location_tracking_channel"
        private const val CHANNEL_NAME = "تتبع الموقع"
        private const val UPDATE_INTERVAL_MS = 5000L // 5 seconds
        private const val FASTEST_UPDATE_INTERVAL_MS = 2000L // 2 seconds
        private const val MIN_ACCURACY_METERS = 20f
        private const val MAX_AGE_MS = 30000L // 30 seconds
        
        // Location update intervals
        const val HIGH_FREQUENCY_INTERVAL = 2000L // 2 seconds (active tracking)
        const val NORMAL_FREQUENCY_INTERVAL = 10000L // 10 seconds (normal tracking)
        const val LOW_FREQUENCY_INTERVAL = 30000L // 30 seconds (background tracking)
        const val BATTERY_SAVER_INTERVAL = 60000L // 1 minute (battery saver)
    }

    // Location components
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationManager: LocationManager
    private var locationCallback: LocationCallback? = null
    
    // Current state
    private val _isTracking = MutableStateFlow(false)
    val isTracking: StateFlow<Boolean> = _isTracking.asStateFlow()
    
    private val _currentLocation = MutableStateFlow<Location?>(null)
    val currentLocation: StateFlow<Location?> = _currentLocation.asStateFlow()
    
    private val _locationUpdates = MutableStateFlow<List<Location>>(emptyList())
    val locationUpdates: StateFlow<List<Location>> = _locationUpdates.asStateFlow()
    
    private val _trackingStatus = MutableStateFlow(TrackingStatus.STOPPED)
    val trackingStatus: StateFlow<TrackingStatus> = _trackingStatus.asStateFlow()
    
    private val _trackingStats = MutableStateFlow(TrackingStats())
    val trackingStats: StateFlow<TrackingStats> = _trackingStats.asStateFlow()
    
    // Configuration
    private var updateInterval = NORMAL_FREQUENCY_INTERVAL
    private var isHighAccuracyMode = false
    private var isBatterySaverMode = false
    private var trackingJob: Job? = null
    private var notificationJob: Job? = null
    
    // Location history
    private val locationHistory = mutableListOf<Location>()
    private var lastKnownLocation: Location? = null
    private var totalDistance = 0.0
    private var startTime = 0L
    
    override fun onCreate() {
        super.onCreate()
        initializeLocationServices()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        
        when (intent?.action) {
            Actions.ACTION_START_TRACKING -> startTracking()
            Actions.ACTION_STOP_TRACKING -> stopTracking()
            Actions.ACTION_PAUSE_TRACKING -> pauseTracking()
            Actions.ACTION_RESUME_TRACKING -> resumeTracking()
            Actions.ACTION_UPDATE_SETTINGS -> updateTrackingSettings(intent)
        }
        
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? = null

    /**
     * Initialize location services
     * تهيئة خدمات الموقع
     */
    private fun initializeLocationServices() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    handleLocationUpdate(location)
                }
            }
            
            override fun onLocationAvailability(availability: LocationAvailability) {
                _trackingStatus.value = if (availability.isLocationAvailable) {
                    TrackingStatus.ACTIVE
                } else {
                    TrackingStatus.NO_SIGNAL
                }
            }
        }
    }

    /**
     * Start location tracking
     * بدء تتبع الموقع
     */
    private fun startTracking() {
        if (!hasLocationPermissions()) {
            _trackingStatus.value = TrackingStatus.PERMISSION_DENIED
            return
        }
        
        if (!isLocationEnabled()) {
            _trackingStatus.value = TrackingStatus.GPS_DISABLED
            return
        }
        
        try {
            startTime = System.currentTimeMillis()
            locationHistory.clear()
            totalDistance = 0.0
            
            // Start foreground service
            startForeground(NOTIFICATION_ID, createTrackingNotification())
            
            // Start location updates
            startLocationUpdates()
            
            _isTracking.value = true
            _trackingStatus.value = TrackingStatus.ACTIVE
            
            // Start tracking job
            trackingJob = lifecycleScope.launch {
                while (_isTracking.value) {
                    updateTrackingStats()
                    delay(1000) // Update stats every second
                }
            }
            
            // Start notification updates
            notificationJob = lifecycleScope.launch {
                while (_isTracking.value) {
                    updateNotification()
                    delay(5000) // Update notification every 5 seconds
                }
            }
            
        } catch (e: Exception) {
            e.printStackTrace()
            _trackingStatus.value = TrackingStatus.ERROR
        }
    }

    /**
     * Stop location tracking
     * إيقاف تتبع الموقع
     */
    private fun stopTracking() {
        try {
            // Stop location updates
            locationCallback?.let { fusedLocationClient.removeLocationUpdates(it) }
            locationManager.removeUpdates(this)
            
            // Cancel jobs
            trackingJob?.cancel()
            notificationJob?.cancel()
            
            // Update state
            _isTracking.value = false
            _trackingStatus.value = TrackingStatus.STOPPED
            
            // Stop foreground service
            stopForeground(true)
            stopSelf()
            
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Pause tracking
     * إيقاف التتبع مؤقتاً
     */
    private fun pauseTracking() {
        try {
            locationCallback?.let { fusedLocationClient.removeLocationUpdates(it) }
            locationManager.removeUpdates(this)
            
            _trackingStatus.value = TrackingStatus.PAUSED
            updateInterval = LOW_FREQUENCY_INTERVAL
            
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Resume tracking
     * استئناف التتبع
     */
    private fun resumeTracking() {
        if (_isTracking.value) {
            startLocationUpdates()
            _trackingStatus.value = TrackingStatus.ACTIVE
        }
    }

    /**
     * Start location updates
     * بدء تحديثات الموقع
     */
    private fun startLocationUpdates() {
        try {
            val locationRequest = LocationRequest.Builder(updateInterval)
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .setMinUpdateIntervalMillis(FASTEST_UPDATE_INTERVAL_MS)
                .setMaxUpdateDelayMillis(updateInterval * 2)
                .build()
            
            locationCallback?.let { callback ->
                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    callback,
                    Looper.getMainLooper()
                )
            }
            
            // Fallback to legacy LocationManager
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) 
                == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    updateInterval,
                    MIN_ACCURACY_METERS.toFloat(),
                    this
                )
            }
            
        } catch (e: Exception) {
            e.printStackTrace()
            _trackingStatus.value = TrackingStatus.ERROR
        }
    }

    /**
     * Handle location update
     * معالجة تحديث الموقع
     */
    private fun handleLocationUpdate(location: Location) {
        // Validate location
        if (!isValidLocation(location)) return
        
        // Calculate distance
        lastKnownLocation?.let { last ->
            totalDistance += last.distanceTo(location).toDouble()
        }
        
        // Update state
        lastKnownLocation = location
        _currentLocation.value = location
        
        // Add to history
        locationHistory.add(location)
        if (locationHistory.size > 1000) { // Keep only last 1000 locations
            locationHistory.removeAt(0)
        }
        
        // Update location updates flow
        _locationUpdates.value = locationHistory.toList()
        
        // Broadcast location update
        broadcastLocationUpdate(location)
        
        _trackingStatus.value = TrackingStatus.ACTIVE
    }

    /**
     * Validate location
     * التحقق من صحة الموقع
     */
    private fun isValidLocation(location: Location): Boolean {
        val currentTime = System.currentTimeMillis()
        
        // Check if location is too old
        if (currentTime - location.time > MAX_AGE_MS) {
            return false
        }
        
        // Check accuracy
        if (location.accuracy > MIN_ACCURACY_METERS) {
            return false
        }
        
        // Check if location is from mock provider (if needed)
        // if (location.isFromMockProvider) return false
        
        return true
    }

    /**
     * Update tracking statistics
     * تحديث إحصائيات التتبع
     */
    private fun updateTrackingStats() {
        val currentTime = System.currentTimeMillis()
        val duration = if (startTime > 0) currentTime - startTime else 0L
        
        val stats = TrackingStats(
            duration = duration,
            distance = totalDistance,
            averageSpeed = if (duration > 0) (totalDistance * 1000) / duration else 0.0,
            locationCount = locationHistory.size,
            accuracy = _currentLocation.value?.accuracy ?: 0f,
            batteryLevel = getBatteryLevel(),
            isHighAccuracy = isHighAccuracyMode
        )
        
        _trackingStats.value = stats
    }

    /**
     * Broadcast location update
        بث تحديث الموقع
     */
    private fun broadcastLocationUpdate(location: Location) {
        val intent = Intent(Actions.ACTION_LOCATION_UPDATE).apply {
            putExtra("location", location)
            putExtra("latitude", location.latitude)
            putExtra("longitude", location.longitude)
            putExtra("accuracy", location.accuracy)
            putExtra("timestamp", location.time)
        }
        sendBroadcast(intent)
    }

    /**
     * Create tracking notification
     * إنشاء إشعار التتبع
     */
    private fun createTrackingNotification(): Notification {
        val intent = Intent(this, RealTimeLocationService::class.java).apply {
            action = Actions.ACTION_STOP_TRACKING
        }
        val pendingIntent = PendingIntent.getService(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("تتبع الموقع نشط")
            .setContentText("جاري تتبع موقعك في الوقت الفعلي")
            // .setSmallIcon(R.drawable.ic_notification)
            // .addAction(R.drawable.ic_close, "إيقاف", pendingIntent)
            .setOngoing(true)
            .setSilent(true)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
    }

    /**
     * Update notification
     * تحديث الإشعار
     */
    private fun updateNotification() {
        val stats = _trackingStats.value
        val accuracy = if (stats.accuracy < 1000) "${stats.accuracy.toInt()}م" else "غير دقيق"
        val duration = formatDuration(stats.duration)
        
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("تتبع الموقع نشط")
            .setContentText("الدقة: $accuracy | المدة: $duration")
            .setSmallIcon(R.drawable.ic_notification)
            .setOngoing(true)
            .setSilent(true)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
        
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    /**
     * Check if location permissions are granted
     * التحقق من منح أذونات الموقع
     */
    private fun hasLocationPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Check if location is enabled
     * التحقق من تفعيل الموقع
     */
    private fun isLocationEnabled(): Boolean {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
            locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    /**
     * Get battery level
     * الحصول على مستوى البطارية
     */
    private fun getBatteryLevel(): Int {
        return try {
            val batteryManager = applicationContext.getSystemService(Context.BATTERY_SERVICE) as android.os.BatteryManager
            batteryManager.getIntProperty(android.os.BatteryManager.BATTERY_PROPERTY_CAPACITY)
        } catch (e: Exception) {
            -1
        }
    }

    /**
     * Format duration
     * تنسيق المدة
     */
    private fun formatDuration(durationMs: Long): String {
        val seconds = durationMs / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        
        return when {
            hours > 0 -> "${hours}س ${minutes % 60}د"
            minutes > 0 -> "${minutes}د ${seconds % 60}ث"
            else -> "${seconds}ث"
        }
    }

    /**
     * Update tracking settings
     * تحديث إعدادات التتبع
     */
    private fun updateTrackingSettings(intent: Intent) {
        updateInterval = intent.getLongExtra("update_interval", NORMAL_FREQUENCY_INTERVAL)
        isHighAccuracyMode = intent.getBooleanExtra("high_accuracy", false)
        isBatterySaverMode = intent.getBooleanExtra("battery_saver", false)
        
        if (isBatterySaverMode) {
            updateInterval = BATTERY_SAVER_INTERVAL
        } else if (isHighAccuracyMode) {
            updateInterval = HIGH_FREQUENCY_INTERVAL
        }
        
        // Restart location updates with new settings
        if (_isTracking.value) {
            startLocationUpdates()
        }
    }

    // LocationListener interface
    override fun onLocationChanged(location: Location) {
        handleLocationUpdate(location)
    }

    override fun onProviderEnabled(provider: String) {
        _trackingStatus.value = TrackingStatus.ACTIVE
    }

    override fun onProviderDisabled(provider: String) {
        _trackingStatus.value = TrackingStatus.GPS_DISABLED
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        // Handle provider status changes
    }

    /**
     * Create notification channel
     * إنشاء قناة الإشعارات
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "إشعارات تتبع الموقع في الوقت الفعلي"
                setShowBadge(false)
                enableVibration(false)
                setSound(null, null)
            }
            
            val notificationManager = applicationContext.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    // Actions
    object Actions {
        const val ACTION_START_TRACKING = "com.edham.logistics.START_TRACKING"
        const val ACTION_STOP_TRACKING = "com.edham.logistics.STOP_TRACKING"
        const val ACTION_PAUSE_TRACKING = "com.edham.logistics.PAUSE_TRACKING"
        const val ACTION_RESUME_TRACKING = "com.edham.logistics.RESUME_TRACKING"
        const val ACTION_UPDATE_SETTINGS = "com.edham.logistics.UPDATE_SETTINGS"
        const val ACTION_LOCATION_UPDATE = "com.edham.logistics.LOCATION_UPDATE"
    }

    // Tracking status enum
    enum class TrackingStatus {
        STOPPED,
        ACTIVE,
        PAUSED,
        NO_SIGNAL,
        GPS_DISABLED,
        PERMISSION_DENIED,
        ERROR
    }

    // Tracking stats data class
    data class TrackingStats(
        val duration: Long = 0L,
        val distance: Double = 0.0,
        val averageSpeed: Double = 0.0,
        val locationCount: Int = 0,
        val accuracy: Float = 0f,
        val batteryLevel: Int = -1,
        val isHighAccuracy: Boolean = false
    )
}
