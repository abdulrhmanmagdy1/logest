package com.edham.logistics.utils

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager

/**
 * Battery utilities for monitoring device battery status
 */
object BatteryUtils {
    
    /**
     * Get the current battery level as a percentage (0-100)
     */
    fun getBatteryLevel(context: Context): Int {
        val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        return batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER)
    }
    
    /**
     * Check if the device is currently charging
     */
    fun isCharging(context: Context): Boolean {
        val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        val status = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_STATUS)
        return status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL
    }
    
    /**
     * Get battery status string (charging, discharging, etc.)
     */
    fun getBatteryStatus(context: Context): String {
        val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        val status = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_STATUS)
        return when (status) {
            BatteryManager.BATTERY_STATUS_CHARGING -> "Charging"
            BatteryManager.BATTERY_STATUS_DISCHARGING -> "Discharging"
            BatteryManager.BATTERY_STATUS_FULL -> "Full"
            BatteryManager.BATTERY_STATUS_NOT_CHARGING -> "Not Charging"
            else -> "Unknown"
        }
    }
}
