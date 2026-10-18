package org.raahi.core.designsystem.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.raahi.core.designsystem.R
import org.raahi.core.designsystem.speech.tapToHear
import org.raahi.core.designsystem.theme.RaahiColors
import org.raahi.core.designsystem.theme.RaahiTheme
import org.raahi.core.designsystem.theme.Tone

/** What Didi (and the mic) is doing. Drives the ring colour around her avatar. */
enum class DidiState(val ring: Color) {
    Idle(RaahiColors.Line),
    Listening(RaahiColors.Neel),
    Thinking(RaahiColors.DidiOrange),
    Speaking(RaahiColors.Leaf),
    Offline(RaahiColors.Line),
}

/** Didi's full figure (120 × 140 artwork), decorative: the bubble next to her carries meaning. */
@Composable
fun DidiFigure(modifier: Modifier = Modifier, width: Dp = 50.dp, wave: Boolean = false) {
    Image(
        painter = painterResource(if (wave) R.drawable.didi_wave else R.drawable.didi),
        contentDescription = null,
        contentScale = ContentScale.Fit,
        modifier = modifier.size(width, width * 140f / 120f),
    )
}

/** Didi inside a ring: listening = neel, thinking = didiOrange, speaking = leaf, offline = dashed. */
@Composable
fun DidiAvatar(state: DidiState, modifier: Modifier = Modifier, size: Dp = 66.dp, wave: Boolean = false) {
    val ringWidth = 3.dp
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(RaahiColors.Surface)
            .drawBehind {
                val stroke = ringWidth.toPx()
                drawCircle(
                    color = state.ring,
                    radius = this.size.minDimension / 2 - stroke / 2,
                    center = Offset(this.size.width / 2, this.size.height / 2),
                    style = Stroke(
                        width = stroke,
                        pathEffect = if (state == DidiState.Offline) {
                            PathEffect.dashPathEffect(floatArrayOf(4.dp.toPx(), 3.dp.toPx()))
                        } else {
                            null
                        },
                    ),
                )
            },
        contentAlignment = Alignment.BottomCenter,
    ) {
        DidiFigure(width = size * 0.85f, wave = wave, modifier = Modifier.padding(top = 2.dp))
    }
}

/** A speech bubble (`.bubble`): 16dp corners, gold tint by default. Tap to hear. */
@Composable
fun Bubble(
    text: String,
    modifier: Modifier = Modifier,
    en: String? = null,
    tone: Tone = Tone.Gold,
    footer: (@Composable () -> Unit)? = null,
) {
    val container = if (tone == Tone.Solid) RaahiColors.Neel else tone.container
    val content = if (tone == Tone.Solid) RaahiColors.OnNeel else RaahiColors.Ink
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(container)
            .tapToHear(text)
            .padding(horizontal = 11.dp, vertical = 9.dp),
    ) {
        androidx.compose.material3.Text(text, style = RaahiTheme.typography.bubble, color = content)
        if (en != null) EnText(en, color = if (tone == Tone.Solid) content.copy(alpha = 0.85f) else Color.Unspecified)
        footer?.invoke()
    }
}

/** Didi next to a bubble, bottom-aligned like the reference screens. */
@Composable
fun DidiBubble(
    text: String,
    modifier: Modifier = Modifier,
    en: String? = null,
    tone: Tone = Tone.Gold,
    didiWidth: Dp = 50.dp,
    wave: Boolean = false,
    alignTop: Boolean = false,
    trailing: (@Composable RowScope.() -> Unit)? = null,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = if (alignTop) Alignment.Top else Alignment.Bottom,
    ) {
        DidiFigure(width = didiWidth, wave = wave)
        Bubble(text = text, en = en, tone = tone, modifier = Modifier.weight(1f, fill = false))
        trailing?.invoke(this)
    }
}
