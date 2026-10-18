package org.raahi.core.voice

import android.content.Context
import android.util.Log
import org.json.JSONArray
import org.json.JSONObject
import org.vosk.Model
import org.vosk.Recognizer
import org.vosk.android.RecognitionListener
import org.vosk.android.SpeechService
import org.vosk.android.SpeechStreamService
import java.io.File
import java.io.FileInputStream

/**
 * Offline speech recognition with Vosk (vosk-model-small-hi-0.22). The model is a separate
 * ~42 MB pack, not part of the APK; it lives in the app's files folder once downloaded.
 *
 * Each screen passes the few phrases it expects (its grammar). Restricting Vosk to them makes
 * a small model accurate enough on a cheap phone, and anything else comes back as null so the
 * screen falls back to picture tiles.
 */
class VoskListener(private val context: Context) {
    private var model: Model? = null
    private var service: SpeechService? = null
    private var stream: SpeechStreamService? = null

    val modelDir: File get() = File(context.filesDir, "packs/$MODEL")

    /**
     * Debug builds only: if this file exists when the mic is pressed, it is recognised instead
     * of the microphone, then deleted. The emulator has no microphone of its own.
     */
    val debugUtterance: File get() = File(context.filesDir, "debug/utterance.wav")

    fun hasModel(): Boolean = File(modelDir, "am/final.mdl").exists()

    /** Loads the model; slow (~1–2 s), so call it off the main thread. */
    fun load(): Boolean {
        if (model != null) return true
        if (!hasModel()) return false
        model = Model(modelDir.absolutePath)
        return true
    }

    /**
     * Listens for one utterance and returns the recognised text, or null if nothing matched.
     * @param phrases the words this screen understands, in the student's language
     */
    fun listen(phrases: List<String>, allowDebugFile: Boolean, onResult: (String?) -> Unit): Boolean {
        val m = model ?: return false
        stop()
        val grammar = JSONArray(phrases.map { it.lowercase() } + "[unk]").toString()
        val recognizer = Recognizer(m, RATE, grammar)
        val listener = object : RecognitionListener {
            private var done = false
            private fun finish(json: String?) {
                Log.d(TAG, "final $json")
                if (done) return
                done = true
                val text = json?.let { JSONObject(it).optString("text") }?.replace("[unk]", "")?.trim()
                stop()
                onResult(text?.takeIf { it.isNotEmpty() })
            }
            override fun onPartialResult(hypothesis: String?) {
                Log.d(TAG, "partial $hypothesis")
            }
            override fun onResult(hypothesis: String?) {
                val text = hypothesis?.let { JSONObject(it).optString("text") }.orEmpty()
                if (text.isNotBlank() && text != "[unk]") finish(hypothesis)
            }
            override fun onFinalResult(hypothesis: String?) = finish(hypothesis)
            override fun onError(exception: Exception?) = finish(null)
            override fun onTimeout() = finish(null)
        }
        val file = debugUtterance
        if (allowDebugFile && file.exists()) {
            val input = FileInputStream(file).apply { skip(WAV_HEADER) }
            stream = SpeechStreamService(recognizer, input, RATE).also { it.start(listener) }
            file.delete()
        } else {
            service = SpeechService(recognizer, RATE).also { it.startListening(listener, TIMEOUT_MS) }
        }
        return true
    }

    fun stop() {
        service?.stop(); service?.shutdown(); service = null
        stream?.stop(); stream = null
    }

    companion object {
        const val MODEL = "vosk-model-small-hi-0.22"
        private const val RATE = 16000f
        private const val TIMEOUT_MS = 8000
        private const val TAG = "RaahiVoice"
        private const val WAV_HEADER = 44L
    }
}
