package com.edham.logistics.core.voice

import android.content.Context
import android.speech.tts.TextToSpeech
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Tactical Voice Assistant for Drivers: Announces critical alerts (Temp, Route, SOS).
 */
@Singleton
class TacticalVoiceAssistant @Inject constructor(
    private val context: Context
) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isReady = false

    init {
        tts = TextToSpeech(context, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale("ar"))
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Timber.e("Arabic TTS not supported on this device")
            } else {
                isReady = true
                Timber.i("Tactical Voice Assistant Ready")
            }
        }
    }

    fun speak(text: String) {
        if (isReady) {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
    }
}
