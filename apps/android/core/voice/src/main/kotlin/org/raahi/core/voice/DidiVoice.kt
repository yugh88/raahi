package org.raahi.core.voice

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import org.raahi.core.model.AppLanguage
import java.util.Locale

/**
 * Didi's voice: the phone's own text-to-speech in the student's language (hi-IN, ta-IN, …).
 * Works offline when the language's voice data is installed; [available] tells the UI whether
 * to show the "download voice data" prompt.
 */
class DidiVoice(context: Context) {
    private var ready = false
    private var pending: (() -> Unit)? = null
    private var language: AppLanguage = AppLanguage.Default
    private var onLine: ((Int) -> Unit)? = null

    var available: Boolean = false
        private set

    private val tts: TextToSpeech = TextToSpeech(context.applicationContext) { status ->
        ready = status == TextToSpeech.SUCCESS
        if (ready) {
            applyLanguage()
            pending?.invoke()
            pending = null
        }
    }.apply {
        setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(id: String) {
                id.removePrefix(LINE).toIntOrNull()?.let { onLine?.invoke(it) }
            }
            override fun onDone(id: String) = Unit
            @Deprecated("Deprecated in Java")
            override fun onError(id: String) = Unit
        })
    }

    fun setLanguage(l: AppLanguage) {
        language = l
        if (ready) applyLanguage()
    }

    private fun applyLanguage() {
        val r = tts.setLanguage(Locale.forLanguageTag("${language.tag}-IN"))
        available = r != TextToSpeech.LANG_MISSING_DATA && r != TextToSpeech.LANG_NOT_SUPPORTED
    }

    fun speak(text: String, rate: Float = 1f) = whenReady {
        tts.setSpeechRate(rate * BASE_RATE)
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "say")
    }

    /** Reads [lines] one after another, reporting which line is being read. */
    fun speakLines(lines: List<String>, from: Int, rate: Float, onLine: (Int) -> Unit) = whenReady {
        this.onLine = onLine
        tts.setSpeechRate(rate * BASE_RATE)
        lines.forEachIndexed { i, line ->
            if (i >= from) tts.speak(line, if (i == from) TextToSpeech.QUEUE_FLUSH else TextToSpeech.QUEUE_ADD, null, "$LINE$i")
        }
    }

    fun stop() {
        if (ready) tts.stop()
    }

    fun shutdown() = tts.shutdown()

    private fun whenReady(block: () -> Unit) {
        if (ready) block() else pending = block
    }

    private companion object {
        const val LINE = "line-"
        /** A little slower than default: easier for first-time listeners. */
        const val BASE_RATE = 0.9f
    }
}
