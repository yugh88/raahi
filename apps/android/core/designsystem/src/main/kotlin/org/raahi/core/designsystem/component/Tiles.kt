package org.raahi.core.designsystem.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.raahi.core.designsystem.speech.LocalSpeaker
import org.raahi.core.designsystem.theme.LocalReadingLevel
import org.raahi.core.designsystem.theme.RaahiColors
import org.raahi.core.designsystem.theme.RaahiTheme
import org.raahi.core.designsystem.theme.Tone
import org.raahi.core.model.ReadingLevel

val TileShape = RoundedCornerShape(16.dp)

/**
 * A big tappable choice (`.tile`). Speaks its own label when tapped, then calls [onClick].
 * At least 56dp tall; the label wraps (never ellipsized) so long Tamil/Malayalam words fit.
 * [vertical] = icon above centred text, as in the 2-column grids.
 */
@Composable
fun ChoiceTile(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    en: String? = null,
    @DrawableRes icon: Int? = null,
    tone: Tone = Tone.Line,
    selected: Boolean = false,
    vertical: Boolean = false,
    labelStyle: TextStyle = RaahiTheme.typography.tile,
    iconSize: Dp = 28.dp,
    minHeight: Dp = 56.dp,
    contentPadding: Dp = 12.dp,
    trailing: (@Composable () -> Unit)? = null,
) {
    val speaker = LocalSpeaker.current
    val lowReading = LocalReadingLevel.current == ReadingLevel.Low
    val border = when {
        selected -> Modifier.border(2.dp, if (tone == Tone.Line) RaahiColors.Neel else tone.content, TileShape)
        tone == Tone.Line -> Modifier.border(1.5.dp, RaahiColors.Line, TileShape)
        else -> Modifier
    }
    val clickable = Modifier
        .heightIn(min = minHeight)
        .clip(TileShape)
        .background(tone.container)
        .then(border)
        .clickable(role = Role.Button) {
            speaker.speak(label)
            onClick()
        }
        .padding(horizontal = contentPadding, vertical = 10.dp)
    val iconTint = if (tone == Tone.Line) RaahiColors.Ink else tone.content
    if (vertical) {
        Column(
            modifier = modifier.then(clickable),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
        ) {
            if (icon != null) Icon(painterResource(icon), null, tint = iconTint, modifier = Modifier.size(if (lowReading) iconSize * 1.4f else iconSize))
            Text(label, style = labelStyle, color = tone.content, textAlign = TextAlign.Center)
            if (en != null) EnText(en, textAlign = TextAlign.Center, color = if (tone == Tone.Solid) tone.content else RaahiColors.Muted)
        }
    } else {
        Row(
            modifier = modifier.then(clickable),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            if (icon != null) Icon(painterResource(icon), null, tint = iconTint, modifier = Modifier.size(iconSize))
            Column(Modifier.weight(1f)) {
                Text(label, style = labelStyle, color = tone.content)
                if (en != null) EnText(en, color = if (tone == Tone.Solid) tone.content.copy(alpha = 0.85f) else RaahiColors.Muted)
            }
            trailing?.invoke()
        }
    }
}

/** Two-column grid of equal-height tiles (`.g2`). Odd last item spans the full width. */
@Composable
fun <T> TileGrid(
    items: List<T>,
    modifier: Modifier = Modifier,
    gap: Dp = 7.dp,
    tile: @Composable (item: T, modifier: Modifier) -> Unit,
) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(gap)) {
        items.chunked(2).forEach { pair ->
            Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min), horizontalArrangement = Arrangement.spacedBy(gap)) {
                pair.forEach { tile(it, Modifier.weight(1f).fillMaxHeight()) }
            }
        }
    }
}

/** Progress dots for multi-step flows (`.dots`). */
@Composable
fun ProgressDots(count: Int, current: Int, modifier: Modifier = Modifier) {
    Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterHorizontally)) {
        repeat(count) { i ->
            val on = i == current
            androidx.compose.foundation.layout.Box(
                Modifier
                    .size(width = if (on) 18.dp else 7.dp, height = 7.dp)
                    .clip(RoundedCornerShape(if (on) 4.dp else 50.dp))
                    .background(if (on) RaahiColors.Neel else if (i < current) RaahiColors.Neel.copy(alpha = 0.4f) else RaahiColors.Line),
            )
        }
    }
}
