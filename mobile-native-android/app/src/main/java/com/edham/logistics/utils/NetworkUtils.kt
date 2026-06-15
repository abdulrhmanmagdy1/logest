package com.edham.logistics.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.telephony.SignalStrength
import android.telephony.TelephonyManager

object NetworkUtils {
    
    /**
     * Check if network is available
     */
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        } else {
            @Suppress("DEPRECATION")
            val networkInfo = connectivityManager.activeNetworkInfo
            networkInfo?.isConnected ?: false
        }
    }
    
    /**
     * Check if internet is available (has connectivity to internet)
     */
    fun isInternetAvailable(): Boolean {
        return try {
            val command = "ping -c 1 8.8.8.8"
            Runtime.getRuntime().exec(command).waitFor() == 0
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Get the type of network connection (WIFI, MOBILE, etc.)
     */
    fun getNetworkType(context: Context): String {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return "UNKNOWN"
            val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return "UNKNOWN"
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "WIFI"
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "MOBILE"
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> "ETHERNET"
                else -> "UNKNOWN"
            }
        } else {
            @Suppress("DEPRECATION")
            when (connectivityManager.activeNetworkInfo?.type) {
                ConnectivityManager.TYPE_WIFI -> "WIFI"
                ConnectivityManager.TYPE_MOBILE -> "MOBILE"
                ConnectivityManager.TYPE_ETHERNET -> "ETHERNET"
                else -> "UNKNOWN"
            }
        }
    }
    
    /**
     * Get signal strength as a percentage (0-100)
     */
    fun getSignalStrength(context: Context): Int {
        return try {
            val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            // Note: This is a simplified implementation
            // In reality, you'd need to use SignalStrength callbacks for accurate values
            val signalLevel = when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                    // For Android 12+, use the newer API
                    val signalStrength = telephonyManager.signalStrength
                    if (signalStrength != null) {
                        (signalStrength.level / 4 * 25)  // Convert 0-4 to 0-100
                    } else {
                        50
                    }
                }
                else -> 50  // Simplified default
            }
            signalLevel
        } catch (e: Exception) {
            50
        }
    }
    
    /**
     * Check if WiFi is connected
     */
    fun isWifiConnected(context: Context): Boolean {
        return getNetworkType(context) == "WIFI"
    }
    
    /**
     * Check if mobile data is connected
     */
    fun isMobileDataConnected(context: Context): Boolean {
        return getNetworkType(context) == "MOBILE"
    }
}

