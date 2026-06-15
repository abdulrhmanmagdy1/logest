package com.edham.logistics.utils

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Battery Optimizer - Advanced battery management for tracking applications
 * 
 * Features:
 * - Battery level monitoring
 * - Power saving mode detection
 * - Background task optimization
 * - Location update frequency adjustment
 * - Network usage optimization
 * - Device performance tuning
 */
@Singleton
class BatteryOptimizer @Inject constructor(
    private val context: Context
) {
    
    private val _batteryState = MutableStateFlow<BatteryState>(BatteryState.Unknown)
    val batteryState: StateFlow<BatteryState> = _batteryState.asStateFlow()
    
    private val _powerSavingMode = MutableStateFlow(false)
    val powerSavingMode: StateFlow<Boolean> = _powerSavingMode.asStateFlow()
    
    private val _batteryLevel = MutableStateFlow(100)
    val batteryLevel: StateFlow<Int> = _batteryLevel.asStateFlow()
    
    private val _isCharging = MutableStateFlow(false)
    val isCharging: StateFlow<Boolean> = _isCharging.asStateFlow()
    
    private val _optimizationLevel = MutableStateFlow(OptimizationLevel.NORMAL)
    val optimizationLevel: StateFlow<OptimizationLevel> = _optimizationLevel.asStateFlow()
    
    private val batteryManager: BatteryManager by lazy {
        context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
    }
    
    private val powerManager: PowerManager by lazy {
        context.getSystemService(Context.POWER_SERVICE) as PowerManager
    }
    
    private var isMonitoring = false
    private var lastOptimizationCheck = 0L
    private var currentLocationUpdateInterval = 5000L // Default 5 seconds
    private var currentNetworkUpdateInterval = 30000L // Default 30 seconds
    
    companion object {
        private const val TAG = "BatteryOptimizer"
        private const val OPTIMIZATION_CHECK_INTERVAL = 60000L // 1 minute
        private const val CRITICAL_BATTERY_LEVEL = 15
        private const val LOW_BATTERY_LEVEL = 30
        private const val HIGH_BATTERY_LEVEL = 80
    }
    
    /**
     * Start battery monitoring
     */
    fun startMonitoring() {
        if (isMonitoring) return
        
        isMonitoring = true
        updateBatteryInfo()
        
        // Start periodic optimization checks
        startOptimizationChecks()
    }
    
    /**
     * Stop battery monitoring
     */
    fun stopMonitoring() {
        isMonitoring = false
    }
    
    /**
     * Get current battery level
     */
    fun getBatteryLevel(): Int {
        return batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
    }
    
    /**
     * Check if device is charging
     */
    fun isDeviceCharging(): Boolean {
        return batteryManager.isCharging
    }
    
    /**
     * Check if power saving mode is enabled
     */
    fun isPowerSavingModeEnabled(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            powerManager.isPowerSaveMode
        } else {
            // For older versions, check system settings
            false
        }
    }
    
    /**
     * Get optimized location update interval
     */
    fun getLocationUpdateInterval(): Long {
        return currentLocationUpdateInterval
    }
    
    /**
     * Get optimized network update interval
     */
    fun getNetworkUpdateInterval(): Long {
        return currentNetworkUpdateInterval
    }
    
    /**
     * Check if background location is allowed
     */
    fun isBackgroundLocationAllowed(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Check for background location permission
            true // Placeholder - would check actual permissions
        } else {
            true
        }
    }
    
    /**
     * Request battery optimization whitelist
     */
    fun requestBatteryOptimizationWhitelist(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                val intent = Intent().apply {
                    action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                    data = android.net.Uri.parse("package:${context.packageName}")
                }
                context.startActivity(intent)
                true
            } catch (e: Exception) {
                false
            }
        } else {
            true
        }
    }
    
    /**
     * Check if app is whitelisted for battery optimization
     */
    fun isWhitelistedForBatteryOptimization(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            powerManager.isIgnoringBatteryOptimizations(context.packageName)
        } else {
            true
        }
    }
    
    /**
     * Update battery information
     */
    private fun updateBatteryInfo() {
        val level = getBatteryLevel()
        val charging = isDeviceCharging()
        val powerSaving = isPowerSavingModeEnabled()
        
        _batteryLevel.value = level
        _isCharging.value = charging
        _powerSavingMode.value = powerSaving
        
        // Update battery state
        val batteryState = when {
            charging -> BatteryState.Charging
            level > HIGH_BATTERY_LEVEL -> BatteryState.High
            level > LOW_BATTERY_LEVEL -> BatteryState.Medium
            level > CRITICAL_BATTERY_LEVEL -> BatteryState.Low
            else -> BatteryState.Critical
        }
        
        _batteryState.value = batteryState
        
        // Update optimization level
        updateOptimizationLevel()
    }
    
    /**
     * Update optimization level based on battery state
     */
    private fun updateOptimizationLevel() {
        val level = _batteryLevel.value
        val charging = _isCharging.value
        val powerSaving = _powerSavingMode.value
        
        val optimizationLevel = when {
            charging -> OptimizationLevel.NORMAL
            powerSaving -> OptimizationLevel.AGGRESSIVE
            level > HIGH_BATTERY_LEVEL -> OptimizationLevel.NORMAL
            level > LOW_BATTERY_LEVEL -> OptimizationLevel.MODERATE
            level > CRITICAL_BATTERY_LEVEL -> OptimizationLevel.HIGH
            else -> OptimizationLevel.AGGRESSIVE
        }
        
        _optimizationLevel.value = optimizationLevel
        
        // Adjust update intervals based on optimization level
        adjustUpdateIntervals(optimizationLevel)
    }
    
    /**
     * Adjust update intervals based on optimization level
     */
    private fun adjustUpdateIntervals(level: OptimizationLevel) {
        when (level) {
            OptimizationLevel.NORMAL -> {
                currentLocationUpdateInterval = 5000L // 5 seconds
                currentNetworkUpdateInterval = 30000L // 30 seconds
            }
            OptimizationLevel.MODERATE -> {
                currentLocationUpdateInterval = 10000L // 10 seconds
                currentNetworkUpdateInterval = 60000L // 1 minute
            }
            OptimizationLevel.HIGH -> {
                currentLocationUpdateInterval = 20000L // 20 seconds
                currentNetworkUpdateInterval = 120000L // 2 minutes
            }
            OptimizationLevel.AGGRESSIVE -> {
                currentLocationUpdateInterval = 60000L // 1 minute
                currentNetworkUpdateInterval = 300000L // 5 minutes
            }
        }
    }
    
    /**
     * Start periodic optimization checks
     */
    private fun startOptimizationChecks() {
        // This would be implemented with a timer or coroutine
        // For now, optimization is checked in updateBatteryInfo
    }
    
    /**
     * Get battery usage statistics
     */
    fun getBatteryUsageStats(): BatteryUsageStats {
        return BatteryUsageStats(
            currentLevel = _batteryLevel.value,
            isCharging = _isCharging.value,
            powerSavingMode = _powerSavingMode.value,
            optimizationLevel = _optimizationLevel.value,
            estimatedTimeRemaining = estimateTimeRemaining(),
            batteryHealth = getBatteryHealth(),
            temperature = getBatteryTemperature()
        )
    }
    
    /**
     * Estimate remaining battery time
     */
    private fun estimateTimeRemaining(): Long {
        val level = _batteryLevel.value
        val charging = _isCharging.value
        
        return if (charging) {
            // Estimate time to full charge
            (100 - level) * 60000L // Rough estimate: 1 minute per percent
        } else {
            // Estimate time to empty
            level * 180000L // Rough estimate: 3 minutes per percent
        }
    }
    
    /**
     * Get battery health
     */
    private fun getBatteryHealth(): BatteryHealth {
        return try {
            val intent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
            val health = intent?.getIntExtra(BatteryManager.EXTRA_HEALTH, BatteryManager.BATTERY_HEALTH_UNKNOWN)
                ?: BatteryManager.BATTERY_HEALTH_UNKNOWN
            when (health) {
                BatteryManager.BATTERY_HEALTH_GOOD -> BatteryHealth.Good
                BatteryManager.BATTERY_HEALTH_OVERHEAT -> BatteryHealth.Overheat
                BatteryManager.BATTERY_HEALTH_DEAD -> BatteryHealth.Dead
                BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> BatteryHealth.Overvoltage
                BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE -> BatteryHealth.UnspecifiedFailure
                BatteryManager.BATTERY_HEALTH_COLD -> BatteryHealth.Cold
                else -> BatteryHealth.Unknown
            }
        } catch (e: Exception) {
            BatteryHealth.Unknown
        }
    }

    /**
     * Get battery temperature
     */
    private fun getBatteryTemperature(): Float {
        return try {
            val intent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
            val temp = intent?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) ?: 0
            temp / 10f
        } catch (e: Exception) {
            0f
        }
    }
    
    /**
     * Check if device performance should be throttled
     */
    fun shouldThrottlePerformance(): Boolean {
        return when (_optimizationLevel.value) {
            OptimizationLevel.HIGH, OptimizationLevel.AGGRESSIVE -> true
            else -> false
        }
    }
    
    /**
     * Check if location updates should be paused
     */
    fun shouldPauseLocationUpdates(): Boolean {
        return _optimizationLevel.value == OptimizationLevel.AGGRESSIVE && 
               !_isCharging.value && 
               _batteryLevel.value < CRITICAL_BATTERY_LEVEL
    }
    
    /**
     * Get recommended actions
     */
    fun getRecommendedActions(): List<BatteryAction> {
        val actions = mutableListOf<BatteryAction>()
        
        when (_batteryState.value) {
            BatteryState.Critical -> {
                actions.add(BatteryAction.CHARGE_IMMEDIATELY)
                actions.add(BatteryAction.PAUSE_TRACKING)
                actions.add(BatteryAction.ENABLE_AGGRESSIVE_OPTIMIZATION)
            }
            BatteryState.Low -> {
                actions.add(BatteryAction.CHARGE_SOON)
                actions.add(BatteryAction.REDUCE_UPDATE_FREQUENCY)
                actions.add(BatteryAction.ENABLE_POWER_SAVING)
            }
            BatteryState.Medium -> {
                actions.add(BatteryAction.MONITOR_BATTERY)
                actions.add(BatteryAction.MODERATE_OPTIMIZATION)
            }
            BatteryState.High -> {
                actions.add(BatteryAction.NORMAL_OPERATION)
            }
            BatteryState.Charging -> {
                actions.add(BatteryAction.NORMAL_OPERATION)
                actions.add(BatteryAction.ENABLE_FAST_CHARGING)
            }
            else -> {
                // Unknown state
            }
        }
        
        return actions
    }
    
    /**
     * Apply optimization settings
     */
    fun applyOptimizationSettings(settings: OptimizationSettings) {
        when (settings.level) {
            OptimizationLevel.NORMAL -> {
                currentLocationUpdateInterval = 5000L
                currentNetworkUpdateInterval = 30000L
            }
            OptimizationLevel.MODERATE -> {
                currentLocationUpdateInterval = settings.locationUpdateInterval
                currentNetworkUpdateInterval = settings.networkUpdateInterval
            }
            OptimizationLevel.HIGH -> {
                currentLocationUpdateInterval = settings.locationUpdateInterval
                currentNetworkUpdateInterval = settings.networkUpdateInterval
            }
            OptimizationLevel.AGGRESSIVE -> {
                currentLocationUpdateInterval = settings.locationUpdateInterval
                currentNetworkUpdateInterval = settings.networkUpdateInterval
            }
        }
        
        _optimizationLevel.value = settings.level
    }
    
    /**
     * Reset to default settings
     */
    fun resetToDefaults() {
        currentLocationUpdateInterval = 5000L
        currentNetworkUpdateInterval = 30000L
        _optimizationLevel.value = OptimizationLevel.NORMAL
    }
}

/**
 * Data classes
 */
data class BatteryUsageStats(
    val currentLevel: Int,
    val isCharging: Boolean,
    val powerSavingMode: Boolean,
    val optimizationLevel: OptimizationLevel,
    val estimatedTimeRemaining: Long,
    val batteryHealth: BatteryHealth,
    val temperature: Float
)

data class OptimizationSettings(
    val level: OptimizationLevel,
    val locationUpdateInterval: Long,
    val networkUpdateInterval: Long,
    val enableBackgroundTasks: Boolean,
    val enableHighAccuracyLocation: Boolean
)

/**
 * Enums
 */
enum class BatteryState {
    Unknown,
    Charging,
    High,
    Medium,
    Low,
    Critical
}

enum class OptimizationLevel {
    NORMAL,
    MODERATE,
    HIGH,
    AGGRESSIVE
}

enum class BatteryHealth {
    Unknown,
    Good,
    Overheat,
    Dead,
    Overvoltage,
    UnspecifiedFailure,
    Cold
}

enum class BatteryAction {
    CHARGE_IMMEDIATELY,
    CHARGE_SOON,
    PAUSE_TRACKING,
    ENABLE_AGGRESSIVE_OPTIMIZATION,
    REDUCE_UPDATE_FREQUENCY,
    ENABLE_POWER_SAVING,
    MONITOR_BATTERY,
    MODERATE_OPTIMIZATION,
    NORMAL_OPERATION,
    ENABLE_FAST_CHARGING
}
