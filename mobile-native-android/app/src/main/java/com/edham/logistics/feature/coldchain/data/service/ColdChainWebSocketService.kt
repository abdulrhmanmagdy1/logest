package com.edham.logistics.feature.coldchain.data.service

import android.util.Log
import com.edham.logistics.feature.coldchain.domain.model.ColdChainWebSocketMessage
import com.edham.logistics.feature.coldchain.domain.model.ColdChainMessageType
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import okhttp3.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ColdChainWebSocketService @Inject constructor(
    private val okHttpClient: OkHttpClient,
    private val gson: Gson
) {
    
    private var webSocket: WebSocket? = null
    private val _messageFlow = MutableSharedFlow<ColdChainWebSocketMessage>()
    val messageFlow: Flow<ColdChainWebSocketMessage> = _messageFlow.asSharedFlow()
    
    private var isConnected = false
    
    companion object {
        private const val TAG = "ColdChainWebSocket"
        private const val WEBSOCKET_URL = "wss://api.edham-logistics.com/coldchain/ws"
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
                Log.d(TAG, "ColdChain WebSocket connected")
                
                // Send initial connection message
                sendConnectionMessage()
            }
            
            override fun onMessage(webSocket: WebSocket, text: String) {
                super.onMessage(webSocket, text)
                handleMessage(text)
            }
            
            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                super.onClosing(webSocket, code, reason)
                Log.d(TAG, "ColdChain WebSocket closing: $code - $reason")
            }
            
            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                super.onClosed(webSocket, code, reason)
                isConnected = false
                Log.d(TAG, "ColdChain WebSocket closed: $code - $reason")
            }
            
            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                super.onFailure(webSocket, t, response)
                isConnected = false
                Log.e(TAG, "ColdChain WebSocket error", t)
                
                // Try to reconnect after delay
                attemptReconnect()
            }
        })
    }
    
    fun disconnect() {
        webSocket?.close(1000, "Normal closure")
        webSocket = null
        isConnected = false
        Log.d(TAG, "ColdChain WebSocket disconnected")
    }
    
    fun sendMessage(message: ColdChainWebSocketMessage) {
        if (isConnected) {
            val json = gson.toJson(message)
            webSocket?.send(json)
            Log.d(TAG, "ColdChain message sent: $json")
        } else {
            Log.w(TAG, "ColdChain WebSocket not connected, cannot send message")
        }
    }
    
    private fun handleMessage(text: String) {
        try {
            val message = gson.fromJson(text, ColdChainWebSocketMessage::class.java)
            _messageFlow.tryEmit(message)
            Log.d(TAG, "ColdChain message received: ${message.type}")
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing ColdChain message: $text", e)
        }
    }
    
    private fun sendConnectionMessage() {
        val message = ColdChainWebSocketMessage(
            type = ColdChainMessageType.TEMPERATURE_UPDATE,
            data = mapOf("action" to "connect"),
            timestamp = System.currentTimeMillis(),
            shipmentId = null,
            sensorId = null
        )
        sendMessage(message)
    }
    
    private fun attemptReconnect() {
        // Implement exponential backoff reconnection logic
        // This would typically use a coroutine with delay
        Log.d(TAG, "Attempting to reconnect ColdChain WebSocket...")
    }
    
    fun isConnected(): Boolean = isConnected
}
