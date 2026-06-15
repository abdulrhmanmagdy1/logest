package com.edham.logistics.feature.notifications.data.service

import android.util.Log
import com.edham.logistics.feature.notifications.domain.model.NotificationWebSocketMessage
import com.edham.logistics.feature.notifications.domain.model.NotificationWebSocketType
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import okhttp3.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationWebSocketService @Inject constructor(
    private val okHttpClient: OkHttpClient,
    private val gson: Gson
) {
    
    private var webSocket: WebSocket? = null
    private val _messageFlow = MutableSharedFlow<NotificationWebSocketMessage>()
    val messageFlow: SharedFlow<NotificationWebSocketMessage> = _messageFlow.asSharedFlow()
    
    private var isConnected = false
    
    companion object {
        private const val TAG = "NotificationWebSocket"
        private const val WEBSOCKET_URL = "wss://api.edham-logistics.com/notifications/ws"
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
                Log.d(TAG, "Notification WebSocket connected")
                
                // Send initial connection message
                sendConnectionMessage()
            }
            
            override fun onMessage(webSocket: WebSocket, text: String) {
                super.onMessage(webSocket, text)
                handleMessage(text)
            }
            
            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                super.onClosing(webSocket, code, reason)
                Log.d(TAG, "Notification WebSocket closing: $code - $reason")
            }
            
            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                super.onClosed(webSocket, code, reason)
                isConnected = false
                Log.d(TAG, "Notification WebSocket closed: $code - $reason")
            }
            
            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                super.onFailure(webSocket, t, response)
                isConnected = false
                Log.e(TAG, "Notification WebSocket error", t)
                
                // Try to reconnect after delay
                attemptReconnect()
            }
        })
    }
    
    fun disconnect() {
        webSocket?.close(1000, "Normal closure")
        webSocket = null
        isConnected = false
        Log.d(TAG, "Notification WebSocket disconnected")
    }
    
    fun sendMessage(message: NotificationWebSocketMessage) {
        if (isConnected) {
            val json = gson.toJson(message)
            webSocket?.send(json)
            Log.d(TAG, "Notification message sent: $json")
        } else {
            Log.w(TAG, "Notification WebSocket not connected, cannot send message")
        }
    }
    
    private fun handleMessage(text: String) {
        try {
            val message = gson.fromJson(text, NotificationWebSocketMessage::class.java)
            _messageFlow.tryEmit(message)
            Log.d(TAG, "Notification message received: ${message.type}")
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing notification message: $text", e)
        }
    }
    
    private fun sendConnectionMessage() {
        val message = NotificationWebSocketMessage(
            type = NotificationWebSocketType.DEVICE_CONNECTED,
            data = mapOf("action" to "connect"),
            timestamp = System.currentTimeMillis(),
            userId = null,
            userRole = null
        )
        sendMessage(message)
    }
    
    private fun attemptReconnect() {
        // Implement exponential backoff reconnection logic
        // This would typically use a coroutine with delay
        Log.d(TAG, "Attempting to reconnect Notification WebSocket...")
    }
    
    fun isConnected(): Boolean = isConnected
}
