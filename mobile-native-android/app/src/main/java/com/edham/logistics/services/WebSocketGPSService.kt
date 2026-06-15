package com.edham.logistics.services

import android.location.Location
import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.json.JSONObject
import java.net.URI
import java.util.concurrent.TimeUnit

/**
 * WebSocket GPS Service - manages real-time GPS updates via WebSocket connection
 * Provides live location updates for fleet tracking
 */
class WebSocketGPSService {
    
    private val _connectionStatus = MutableStateFlow<ConnectionStatus>(ConnectionStatus.DISCONNECTED)
    val connectionStatus: StateFlow<ConnectionStatus> = _connectionStatus
    
    private val _locationUpdates = MutableStateFlow<LocationUpdate?>(null)
    val locationUpdates: StateFlow<LocationUpdate?> = _locationUpdates
    
    private var webSocket: okhttp3.WebSocket? = null
    private var client: okhttp3.OkHttpClient? = null
    private var reconnectJob: Job? = null
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    private val reconnectDelay = 5L // seconds
    private val pingInterval = 30L // seconds
    
    companion object {
        private const val TAG = "WebSocketGPSService"
        private const val DEFAULT_WS_URL = "ws://10.0.2.2:5000/gps"
    }
    
    /**
     * Connect to WebSocket GPS service
     */
    fun connect(serverUrl: String = DEFAULT_WS_URL) {
        if (_connectionStatus.value == ConnectionStatus.CONNECTED) {
            Log.d(TAG, "Already connected")
            return
        }
        
        _connectionStatus.value = ConnectionStatus.CONNECTING
        
        client = okhttp3.OkHttpClient.Builder()
            .readTimeout(0, TimeUnit.MILLISECONDS)
            .pingInterval(pingInterval, TimeUnit.SECONDS)
            .build()
        
        val request = okhttp3.Request.Builder()
            .url(serverUrl)
            .build()
        
        val listener = object : okhttp3.WebSocketListener() {
            override fun onOpen(webSocket: okhttp3.WebSocket, response: okhttp3.Response) {
                super.onOpen(webSocket, response)
                Log.d(TAG, "WebSocket connected")
                _connectionStatus.value = ConnectionStatus.CONNECTED
            }
            
            override fun onMessage(webSocket: okhttp3.WebSocket, text: String) {
                super.onMessage(webSocket, text)
                handleLocationMessage(text)
            }
            
            override fun onClosing(webSocket: okhttp3.WebSocket, code: Int, reason: String) {
                super.onClosing(webSocket, code, reason)
                Log.d(TAG, "WebSocket closing: $code - $reason")
                _connectionStatus.value = ConnectionStatus.DISCONNECTED
            }
            
            override fun onFailure(webSocket: okhttp3.WebSocket, t: Throwable, response: okhttp3.Response?) {
                super.onFailure(webSocket, t, response)
                Log.e(TAG, "WebSocket error", t)
                _connectionStatus.value = ConnectionStatus.ERROR
                scheduleReconnect(serverUrl)
            }
        }
        
        webSocket = client?.newWebSocket(request, listener)
    }
    
    /**
     * Disconnect from WebSocket GPS service
     */
    fun disconnect() {
        reconnectJob?.cancel()
        webSocket?.close(1000, "User disconnected")
        webSocket = null
        client = null
        _connectionStatus.value = ConnectionStatus.DISCONNECTED
        Log.d(TAG, "Disconnected")
    }
    
    /**
     * Send GPS location update to server
     */
    fun sendLocationUpdate(location: Location, driverId: String) {
        val json = JSONObject().apply {
            put("driverId", driverId)
            put("latitude", location.latitude)
            put("longitude", location.longitude)
            put("accuracy", location.accuracy)
            put("speed", location.speed)
            put("timestamp", System.currentTimeMillis())
        }
        
        webSocket?.send(json.toString())
        Log.d(TAG, "Sent location update: ${location.latitude}, ${location.longitude}")
    }
    
    /**
     * Handle incoming location message from server
     */
    private fun handleLocationMessage(message: String) {
        try {
            val json = JSONObject(message)
            val driverId = json.optString("driverId")
            val latitude = json.getDouble("latitude")
            val longitude = json.getDouble("longitude")
            val accuracy = json.optDouble("accuracy", 0.0)
            val speed = json.optDouble("speed", 0.0)
            val timestamp = json.optLong("timestamp", System.currentTimeMillis())
            
            val location = Location("WebSocketGPS").apply {
                this.latitude = latitude
                this.longitude = longitude
                this.accuracy = accuracy.toFloat()
                this.speed = speed.toFloat()
                this.time = timestamp
            }
            
            val update = LocationUpdate(
                driverId = driverId,
                location = location,
                timestamp = timestamp
            )
            
            _locationUpdates.value = update
            Log.d(TAG, "Received location update from $driverId")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing location message", e)
        }
    }
    
    /**
     * Schedule automatic reconnection
     */
    private fun scheduleReconnect(serverUrl: String) {
        reconnectJob?.cancel()
        reconnectJob = serviceScope.launch {
            delay(reconnectDelay * 1000)
            if (_connectionStatus.value != ConnectionStatus.CONNECTED) {
                Log.d(TAG, "Attempting to reconnect...")
                connect(serverUrl)
            }
        }
    }
    
    /**
     * Get current connection status
     */
    fun getConnectionStatus(): ConnectionStatus = _connectionStatus.value
    
    /**
     * Check if connected
     */
    fun isConnected(): Boolean = _connectionStatus.value == ConnectionStatus.CONNECTED
    
    /**
     * Clean up resources
     */
    fun cleanup() {
        disconnect()
        serviceScope.cancel()
    }
}

data class LocationUpdate(
    val driverId: String,
    val location: Location,
    val timestamp: Long
)

enum class ConnectionStatus {
    DISCONNECTED,
    CONNECTING,
    CONNECTED,
    ERROR
}
