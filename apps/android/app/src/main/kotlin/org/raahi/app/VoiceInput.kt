package org.raahi.app

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.raahi.core.voice.VoskListener

/**
 * Rule-based voice intents (no ML intent model): each screen offers its tile labels, and the
 * recognised words are matched to a tile by its words or by an ordinal ("दूसरा" = 2nd tile).
 */
object VoiceIntents {
    const val DONT_KNOW = -1

    private val ordinals = listOf(
        listOf("पहला", "पहली", "पहले", "एक"),
        listOf("दूसरा", "दूसरी", "दूसरे", "दो"),
        listOf("तीसरा", "तीसरी", "तीसरे", "तीन"),
        listOf("चौथा", "चौथी", "चौथे", "चार"),
        listOf("पाँचवाँ", "पांचवां", "पाँचवी", "पांच", "पाँच"),
        listOf("छठा", "छठी", "छह"),
        listOf("सातवाँ", "सातवीं", "सात"),
    )
    private val dontKnow = listOf("पता नहीं", "नहीं पता", "मालूम नहीं")

    private fun words(s: String) = s.lowercase().replace(Regex("[^\\p{L}\\p{M}\\s]"), " ").split(Regex("\\s+")).filter { it.length > 1 }

    /** Phrases Vosk should listen for on this screen. */
    fun grammar(options: List<String>, extra: List<String> = emptyList()): List<String> =
        (options.map { words(it).joinToString(" ") } + ordinals.take(options.size).flatten() + dontKnow + extra)
            .filter { it.isNotBlank() }.distinct()

    /** Index of the tile the student said, [DONT_KNOW], or null if nothing matched. */
    fun match(text: String, options: List<String>): Int? {
        val said = words(text)
        if (dontKnow.any { text.contains(it) }) return DONT_KNOW
        ordinals.forEachIndexed { i, forms -> if (i < options.size && said.any { it in forms }) return i }
        val scores = options.map { o -> words(o).count { it in said } }
        val best = scores.maxOrNull() ?: 0
        return if (best > 0 && scores.count { it == best } == 1) scores.indexOf(best) else null
    }
}

/** Mic button behaviour shared by every screen: load the model once, listen, report text. */
class VoiceInput(private val context: Context, private val scope: CoroutineScope, private val debug: Boolean) {
    val listener = VoskListener(context)
    private var loaded = false

    /** Context in the app's chosen language (set from Compose), for messages. */
    var uiContext: Context = context

    fun listen(phrases: List<String>, askPermission: () -> Unit, onText: (String?) -> Unit) {
        val fromFile = debug && listener.debugUtterance.exists()
        if (!fromFile && ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            askPermission()
            return
        }
        scope.launch {
            if (!loaded) loaded = withContext(Dispatchers.IO) { listener.load() }
            if (!loaded) {
                toast(R.string.voice_no_model)
                onText(null)
                return@launch
            }
            toast(R.string.voice_listening)
            listener.listen(phrases, allowDebugFile = debug) { text ->
                scope.launch(Dispatchers.Main) {
                    if (text == null) toast(R.string.voice_not_understood)
                    onText(text)
                }
            }
        }
    }

    private fun toast(res: Int) = Toast.makeText(context, uiContext.getString(res), Toast.LENGTH_SHORT).show()
}
