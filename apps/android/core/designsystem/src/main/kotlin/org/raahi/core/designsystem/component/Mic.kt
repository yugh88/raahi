package org.raahi.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.raahi.core.designsystem.R
import org.raahi.core.designsystem.theme.RaahiColors
import org.raahi.core.designsystem.theme.RaahiTheme

/** BigMicButton states. The halo colour matches Didi's ring for the same state. */
enum class MicState { Idle, Listening, Thinking, Speaking, Offline }

private fun MicState.halo(): Color = when (this) {
    MicState.Idle -> RaahiColors.NeelTint
    MicState.Listening -> RaahiColors.Neel.copy(alpha = 0.35f)
    MicState.Thinking -> RaahiColors.DidiOrange.copy(alpha = 0.45f)
    MicState.Speaking -> RaahiColors.Leaf.copy(alpha = 0.35f)
    MicState.Offline -> RaahiColors.Line
}

/** The one mic button that appears on every screen (`.mic`): 66dp, 7dp halo. */
@Composable
fun BigMicButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    state: MicState = MicState.Idle,
    size: Dp = 66.dp,
) {
    val label = stringResource(R.string.mic_speak)
    val halo = if (size >= 60.dp) 7.dp else 5.dp
    val offline = state == MicState.Offline
    Box(
        modifier = modifier
            .size(size + halo * 2)
            .drawBehind {
                val r = this.size.minDimension / 2
                if (offline) {
                    drawCircle(
                        color = RaahiColors.Line,
                        radius = r - 1.dp.toPx(),
                        style = Stroke(2.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(5.dp.toPx(), 4.dp.toPx()))),
                    )
                } else {
                    drawCircle(state.halo(), radius = r)
                }
            }
            .padding(halo)
            .clip(CircleShape)
            .background(if (offline) RaahiColors.Surface2 else RaahiColors.Neel)
            .clickable(role = Role.Button, onClick = onClick)
            .semantics { contentDescription = label },
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(RaahiIcons.Mic),
            contentDescription = null,
            tint = if (offline) RaahiColors.Muted else RaahiColors.OnNeel,
            modifier = Modifier.size(size * 0.45f),
        )
    }
}

/**
 * Bottom mic area (`.micwrap`): mic, a spoken hint underneath, and the typing fallback
 * button beside it (typing is always available).
 */
@Composable
fun MicDock(
    hint: String,
    onMic: () -> Unit,
    onType: () -> Unit,
    modifier: Modifier = Modifier,
    state: MicState = MicState.Idle,
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(bottom = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(56.dp))
            BigMicButton(onClick = onMic, state = state, modifier = Modifier.padding(horizontal = 12.dp))
            TypeButton(onType)
        }
        HearText(hint, RaahiTheme.typography.en.copy(fontFamily = RaahiTheme.typography.body.fontFamily), textAlign = TextAlign.Center)
    }
}

@Composable
fun TypeButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    val label = stringResource(R.string.type_instead)
    Box(
        modifier = modifier
            .size(56.dp)
            .clip(CircleShape)
            .border(1.5.dp, RaahiColors.Line, CircleShape)
            .clickable(role = Role.Button, onClick = onClick)
            .semantics { contentDescription = label },
        contentAlignment = Alignment.Center,
    ) {
        Icon(painterResource(RaahiIcons.Keyboard), contentDescription = null, tint = RaahiColors.Muted, modifier = Modifier.size(22.dp))
    }
}
