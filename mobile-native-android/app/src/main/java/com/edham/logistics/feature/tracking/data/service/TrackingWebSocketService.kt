package com.edham.logistics.feature.tracking.data.service

import android.util.Log
import com.edham.logistics.feature.tracking.domain.model.TrackingWebSocketMessage
import com.edham.logistics.feature.tracking.domain.model.TrackingMessageType
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import okhttp3.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrackingWebSocketService @Inject constructor(
    private val okHttpClient: OkHttpClient,
    private val gson: Gson
) {
    
    private var webSocket: WebSocket? = null
    private val _messageFlow = MutableSharedFlow<TrackingWebSocketMessage>()
    val messageFlow: Flow<TrackingWebSocketMessage> = _messageFlow.asSharedFlow()
    
    private var isConnected = false
    
    companion object {
        private const val TAG = "TrackingWebSocket"
        private const val WEBSOCKET_URL = "wss://api.edham-logistics.com/tracking/ws"
    }
    
    fun connect(token: String) {
        if (isConnected) {
            Log.d(TAG, "WebSocket already connected")
            return
        }
        
        val request = Request.Builder()
            .url(WEBSOCKET_URL)
            .addHeader("Authorization", "Bearer $token")
            .build()
        
        webSocket = okHttpClient.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                super.onOpen(webSocket, response)
                isConnected = true
                Log.d(TAG, "WebSocket connected")
                
                // Send initial connection message
                sendConnectionMessage()
            }
            
            override fun onMessage(webSocket: WebSocket, text: String) {
                super.onMessage(webSocket, text)
                handleMessage(text)
            }
            
            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                super.onClosing(webSocket, code, reason)
                Log.d(TAG, "WebSocket closing: $code - $reason")
            }
            
            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                super.onClosed(webSocket, code, reason)
                isConnected = false
                Log.d(TAG, "WebSocket closed: $code - $reason")
            }
            
            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                super.onFailure(webSocket, t, response)
                isConnected = false
                Log.e(TAG, "WebSocket error", t)
                
                // Try to reconnect after delay
                attemptReconnect()
            }
        })
    }
    
    fun disconnect() {
        webSocket?.close(1000, "Normal closure")
        webSocket = null
        isConnected = false
        Log.d(TAG, "WebSocket disconnected")
    }
    
    fun sendMessage(message: TrackingWebSocketMessage) {
        if (isConnected) {
            val json = gson.toJson(message)
            webSocket?.send(json)
            Log.d(TAG, "Message sent: $json")
        } else {
            Log.w(TAG, "WebSocket not connected, cannot send message")
        }
    }
    
    private fun handleMessage(text: String) {
        try {
            val message = gson.fromJson(text, TrackingWebSocketMessage::class.java)
            _messageFlow.tryEmit(message)
            Log.d(TAG, "Message received: ${message.type}")
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing message: $text", e)
        }
    }
    
    private fun sendConnectionMessage() {
        val message = TrackingWebSocketMessage(
            type = TrackingMessageType.LOCATION_UPDATE,
            data = mapOf("action" to "connect"),
            timestamp = System.currentTimeMillis(),
            shipmentId = null,
            driverId = null,
            vehicleId = null
        )
        sendMessage(message)
    }
    
    private fun attemptReconnect() {
        // Implement exponential backoff reconnection logic
        // This would typically use a coroutine with delay
        Log.d(TAG, "Attempting to reconnect...")
    }
    
    fun isConnected(): Boolean = isConnected
}
