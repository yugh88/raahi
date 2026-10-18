package org.raahi.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import org.raahi.core.designsystem.R
import org.raahi.core.designsystem.theme.RaahiColors
import org.raahi.core.designsystem.theme.RaahiTheme
import org.raahi.core.designsystem.theme.Tone

data class TranscriptLine(val time: String, val text: String)

/**
 * S12 player: plays only the linked segment of a lesson video (start–end), shows where the
 * segment sits in the whole video, a live transcript (tap a line to jump) and speed choices.
 * Phase 1 draws the frame; Media3 playback from the offline pack arrives with :core:media.
 */
@Composable
fun VideoSegmentPlayer(
    segmentLabel: String,
    segmentStart: Float,
    segmentEnd: Float,
    played: Float,
    speeds: List<Float>,
    speed: Float,
    onSpeed: (Float) -> Unit,
    lines: List<TranscriptLine>,
    currentLine: Int,
    onLine: (Int) -> Unit,
    onPlay: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(9.dp)) {
        Box(
            Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .clip(TileShape)
                .background(RaahiColors.Ink)
                .clickable(role = Role.Button, onClick = onPlay),
            contentAlignment = Alignment.Center,
        ) {
            Box(Modifier.size(56.dp).clip(CircleShape).background(RaahiColors.Surface.copy(alpha = 0.92f)), contentAlignment = Alignment.Center) {
                Icon(painterResource(RaahiIcons.Play), stringResource(R.string.play), tint = RaahiColors.Neel, modifier = Modifier.size(28.dp))
            }
            Row(Modifier.align(Alignment.TopStart).padding(8.dp)) {
                StatusPill(stringResource(R.string.from_pack_offline), Tone.Leaf, icon = RaahiIcons.Download)
            }
            Column(Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(horizontal = 10.dp, vertical = 8.dp)) {
                Text(segmentLabel, style = RaahiTheme.typography.en, color = RaahiColors.OnNeel)
                SegmentBar(segmentStart, segmentEnd, played, Modifier.fillMaxWidth().height(8.dp))
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(7.dp), verticalAlignment = Alignment.CenterVertically) {
            speeds.forEach { s ->
                val on = s == speed
                val label = (if (s % 1f == 0f) s.toInt().toString() else s.toString()) + "×"
                Box(
                    Modifier
                        .heightIn(min = 48.dp)
                        .clip(CircleShape)
                        .background(if (on) RaahiColors.Neel else RaahiColors.Surface2)
                        .clickable(role = Role.Button) { onSpeed(s) }
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(label, style = RaahiTheme.typography.card, color = if (on) RaahiColors.OnNeel else RaahiColors.Ink)
                }
            }
        }
        HearText(stringResource(R.string.transcript), RaahiTheme.typography.caption)
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            lines.forEachIndexed { i, line ->
                val on = i == currentLine
                Row(
                    Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (on) RaahiColors.NeelTint else RaahiColors.Surface)
                        .clickable(role = Role.Button) { onLine(i) }
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(line.time, style = RaahiTheme.typography.en, color = if (on) RaahiColors.Neel else RaahiColors.Muted)
                    Text(line.text, style = RaahiTheme.typography.body, color = if (on) RaahiColors.Neel else RaahiColors.Ink, modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun SegmentBar(start: Float, end: Float, played: Float, modifier: Modifier) {
    Box(
        modifier.drawBehind {
            val h = size.height
            val r = CornerRadius(h / 2)
            drawRoundRect(RaahiColors.Surface.copy(alpha = 0.3f), cornerRadius = r)
            drawRoundRect(
                RaahiColors.DidiOrange,
                topLeft = Offset(size.width * start, 0f),
                size = Size(size.width * (end - start), h),
                cornerRadius = r,
            )
            drawRoundRect(
                RaahiColors.Surface,
                topLeft = Offset(size.width * start, 0f),
                size = Size(size.width * (played - start).coerceAtLeast(0f), h),
                cornerRadius = r,
            )
        },
    )
}
