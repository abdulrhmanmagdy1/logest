package com.edham.logistics.utils

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import timber.log.Timber
import java.util.*

/**
 * Voice Alert Manager for Driver Experience
 * Provides voice guidance and alerts for navigation and safety
 */
class VoiceAlertManager(private val context: Context) : TextToSpeech.OnInitListener {
    
    private var textToSpeech: TextToSpeech? = null
    private var isInitialized = false
    private var isVoiceEnabled = true
    private var currentLanguage = Locale.US
    
    // Voice alert queue for sequential playback
    private val alertQueue = mutableListOf<String>()
    private var isSpeaking = false
    
    init {
        initializeTTS()
    }
    
    private fun initializeTTS() {
        try {
            textToSpeech = TextToSpeech(context, this)
        } catch (e: Exception) {
            Timber.e(e, "Failed to initialize TextToSpeech")
        }
    }
    
    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            textToSpeech?.let { tts ->
                val result = tts.setLanguage(currentLanguage)
                
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Timber.w("Language not supported: $currentLanguage")
                    // Fallback to default language
                    tts.setLanguage(Locale.getDefault())
                }
                
                // Set speech rate for clarity
                tts.setSpeechRate(0.9f)
                
                // Set utterance progress listener
                tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        isSpeaking = true
                    }
                    
                    override fun onDone(utteranceId: String?) {
                        isSpeaking = false
                        processNextAlert()
                    }
                    
                    override fun onError(utteranceId: String?) {
                        isSpeaking = false
                        processNextAlert()
                    }
                })
                
                isInitialized = true
                Timber.d("VoiceAlertManager initialized successfully")
            }
        } else {
            Timber.e("TextToSpeech initialization failed with status: $status")
        }
    }
    
    /**
     * Speak text immediately or add to queue
     */
    fun speak(text: String, priority: AlertPriority = AlertPriority.NORMAL) {
        if (!isVoiceEnabled || !isInitialized) {
            return
        }
        
        when (priority) {
            AlertPriority.HIGH -> {
                // High priority alerts interrupt current speech
                stopSpeaking()
                speakNow(text)
            }
            AlertPriority.NORMAL -> {
                // Normal priority adds to queue
                addToQueue(text)
            }
            AlertPriority.LOW -> {
                // Low priority only added if queue is empty
                if (alertQueue.isEmpty() && !isSpeaking) {
                    addToQueue(text)
                }
            }
        }
    }
    
    /**
     * Navigation voice guidance
     */
    fun provideNavigationGuidance(instruction: String) {
        val guidance = when {
            instruction.contains("turn left", ignoreCase = true) -> "Turn left ahead"
            instruction.contains("turn right", ignoreCase = true) -> "Turn right ahead"
            instruction.contains("straight", ignoreCase = true) -> "Continue straight"
            instruction.contains("destination", ignoreCase = true) -> "You have arrived at your destination"
            else -> instruction
        }
        
        speak(guidance, AlertPriority.NORMAL)
    }
    
    /**
     * Safety alerts
     */
    fun alertSpeedLimit(speedLimit: Int) {
        speak("Speed limit $speedLimit kilometers per hour", AlertPriority.HIGH)
    }
    
    fun alertTrafficAhead() {
        speak("Traffic ahead. Please slow down.", AlertPriority.HIGH)
    }
    
    fun alertRoadHazard() {
        speak("Road hazard detected. Please proceed with caution.", AlertPriority.HIGH)
    }
    
    fun alertWeatherCondition(condition: String) {
        val alert = when (condition.lowercase()) {
            "rain" -> "Rain detected. Please reduce speed."
            "fog" -> "Fog detected. Please use fog lights and reduce speed."
            "snow" -> "Snow detected. Please drive carefully."
            "ice" -> "Ice on road detected. Please drive with extreme caution."
            else -> "Weather alert: $condition"
        }
        
        speak(alert, AlertPriority.HIGH)
    }
    
    /**
     * Trip management alerts
     */
    fun alertTripStarted() {
        speak("Trip started successfully. Drive safely.", AlertPriority.NORMAL)
    }
    
    fun alertTripCompleted() {
        speak("Trip completed. Good job!", AlertPriority.NORMAL)
    }
    
    fun alertBreakTimeRemaining(minutes: Int) {
        speak("Break time remaining: $minutes minutes.", AlertPriority.NORMAL)
    }
    
    fun alertBreakTimeOver() {
        speak("Break time is over. Please resume your trip.", AlertPriority.HIGH)
    }
    
    /**
     * Emergency alerts
     */
    fun alertEmergencySent() {
        speak("Emergency alert sent. Help is on the way.", AlertPriority.HIGH)
    }
    
    fun alertLowFuel(level: Int) {
        speak("Warning: Fuel level is $level percent. Please refuel soon.", AlertPriority.HIGH)
    }
    
    fun alertLowBattery(level: Int) {
        speak("Warning: Phone battery is $level percent. Please charge your device.", AlertPriority.HIGH)
    }
    
    /**
     * Delivery alerts
     */
    fun alertNearDelivery(distance: Int) {
        speak("Delivery location is $distance meters ahead.", AlertPriority.NORMAL)
    }
    
    fun alertDeliveryArrived() {
        speak("You have arrived at the delivery location.", AlertPriority.NORMAL)
    }
    
    fun alertDeliveryConfirmation() {
        speak("Delivery confirmed. Proceed to next destination.", AlertPriority.NORMAL)
    }
    
    /**
     * System alerts
     */
    fun alertGPSLost() {
        speak("GPS signal lost. Please check your connection.", AlertPriority.HIGH)
    }
    
    fun alertGPSRestored() {
        speak("GPS signal restored.", AlertPriority.NORMAL)
    }
    
    fun alertNetworkLost() {
        speak("Network connection lost. Working in offline mode.", AlertPriority.NORMAL)
    }
    
    fun alertNetworkRestored() {
        speak("Network connection restored.", AlertPriority.NORMAL)
    }
    
    private fun speakNow(text: String) {
        textToSpeech?.let { tts ->
            val params = Bundle().apply {
                putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "voice_alert")
            }
            
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, params, "voice_alert")
        }
    }
    
    private fun addToQueue(text: String) {
        alertQueue.add(text)
        if (!isSpeaking) {
            processNextAlert()
        }
    }
    
    private fun processNextAlert() {
        if (alertQueue.isNotEmpty() && !isSpeaking) {
            val nextAlert = alertQueue.removeAt(0)
            speakNow(nextAlert)
        }
    }
    
    private fun stopSpeaking() {
        textToSpeech?.stop()
        isSpeaking = false
        alertQueue.clear()
    }
    
    /**
     * Enable/disable voice alerts
     */
    fun setVoiceEnabled(enabled: Boolean) {
        isVoiceEnabled = enabled
        if (!enabled) {
            stopSpeaking()
        }
    }
    
    /**
     * Set voice language
     */
    fun setLanguage(locale: Locale) {
        currentLanguage = locale
        textToSpeech?.language = locale
    }
    
    /**
     * Set speech rate
     */
    fun setSpeechRate(rate: Float) {
        textToSpeech?.setSpeechRate(rate.coerceIn(0.5f, 2.0f))
    }
    
    /**
     * Check if voice is enabled
     */
    fun isVoiceEnabled(): Boolean = isVoiceEnabled
    
    /**
     * Check if currently speaking
     */
    fun isSpeaking(): Boolean = isSpeaking
    
    /**
     * Get queue size
     */
    fun getQueueSize(): Int = alertQueue.size
    
    /**
     * Clear alert queue
     */
    fun clearQueue() {
        alertQueue.clear()
        stopSpeaking()
    }
    
    /**
     * Cleanup resources
     */
    fun cleanup() {
        stopSpeaking()
        textToSpeech?.shutdown()
        textToSpeech = null
        isInitialized = false
    }
}

/**
 * Alert priority levels
 */
enum class AlertPriority {
    HIGH,    // Emergency and safety alerts
    NORMAL,  // Navigation and trip management
    LOW      // Informational alerts
}
