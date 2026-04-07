package com.google.ai.edge.gallery.common

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TtsManager @Inject constructor(@ApplicationContext private val context: Context) : TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = null
    private var isInitialized = false
    var isMuted = false

    init {
        tts = TextToSpeech(context, this)
    }

    override fun onInit(status: Int) {
        Log.d("TtsManager", "onInit status: $status")
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.CHINESE) ?: TextToSpeech.LANG_NOT_SUPPORTED
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TtsManager", "The Language not supported! Falling back to default")
                tts?.setLanguage(Locale.getDefault())
            }
            isInitialized = true
            Log.d("TtsManager", "TTS initialized successfully.")
        } else {
            Log.e("TtsManager", "Initialization Failed!")
        }
    }

    fun speak(text: String) {
        Log.d("TtsManager", "Attempting to speak. isInit: $isInitialized, muted: $isMuted, text: $text")
        if (!isInitialized || isMuted) return
        val cleanText = text.replace(Regex("[*#>`~]"), "").trim()
        if (cleanText.isEmpty()) return
        Log.d("TtsManager", "Queueing TTS: $cleanText")
        tts?.speak(cleanText, TextToSpeech.QUEUE_ADD, null, "ag_tts_play")
    }

    fun stop() {
        if (!isInitialized) return
        tts?.stop()
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
    }
}
