package org.raahi.core.designsystem.speech

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role

/**
 * "Every word can be heard": anything the student can see can be spoken. The app provides the
 * real implementation (Android TextToSpeech in the chosen locale, Phase 3); the default is
 * silent so previews and screenshot tests need no TTS engine.
 */
fun interface Speaker {
    fun speak(text: String)
}

val LocalSpeaker = staticCompositionLocalOf { Speaker { } }

/**
 * False inside a tappable tile or card: the container speaks its label and acts on the same
 * tap, so text inside it must not grab taps (Compose widens small tap targets to 48dp, which
 * would let a sublabel steal the tile's tap).
 */
val LocalTapToHearEnabled = staticCompositionLocalOf { true }

/** Tap this element to hear [text]. No ripple: plain text should not look like a button. */
@Composable
fun Modifier.tapToHear(text: String): Modifier {
    if (!LocalTapToHearEnabled.current) return this
    val speaker = LocalSpeaker.current
    val interaction = remember { MutableInteractionSource() }
    return clickable(interactionSource = interaction, indication = null, role = Role.Button) {
        speaker.speak(text)
    }
}
