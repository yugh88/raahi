package org.raahi.feature.learn

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.raahi.core.designsystem.component.BackRow
import org.raahi.core.designsystem.component.ChoiceTile
import org.raahi.core.designsystem.component.ClimbStrip
import org.raahi.core.designsystem.component.DidiBubble
import org.raahi.core.designsystem.component.HearText
import org.raahi.core.designsystem.component.MicDock
import org.raahi.core.designsystem.component.ProgressDots
import org.raahi.core.designsystem.component.RaahiCard
import org.raahi.core.designsystem.component.RaahiIcons
import org.raahi.core.designsystem.component.RaahiScreen
import org.raahi.core.designsystem.component.StatusPill
import org.raahi.core.designsystem.component.SourceLine
import org.raahi.core.designsystem.component.TrapChip
import org.raahi.core.designsystem.component.TranscriptLine
import org.raahi.core.designsystem.component.VideoSegmentPlayer
import org.raahi.core.designsystem.theme.RaahiTheme
import org.raahi.core.designsystem.theme.Tone

data class TrapTestUiState(
    val topic: String,
    val index: Int,
    val total: Int,
    @DrawableRes val picture: Int,
    val question: String,
    val options: List<String>,
    val picked: Int?,
)

/** S10 Trap test: 5–8 topic-tagged questions, one at a time, no score shown, no right/wrong. */
@Composable
fun TrapTestScreen(
    state: TrapTestUiState,
    onPick: (Int) -> Unit,
    onBack: () -> Unit,
    onMic: () -> Unit,
    onType: () -> Unit,
    modifier: Modifier = Modifier,
) {
    RaahiScreen(
        modifier = modifier,
        bottom = { MicDock(stringResource(org.raahi.core.designsystem.R.string.speak_or_tap), onMic, onType) },
    ) {
        BackRow(stringResource(R.string.s10_title, state.topic, state.index + 1), onBack)
        ProgressDots(count = state.total, current = state.index)
        Picture(state.picture)
        HearText(state.question, RaahiTheme.typography.tile)
        OptionList(state.options, state.picked, correct = null, onPick = onPick)
        HearText(stringResource(R.string.s10_no_score), RaahiTheme.typography.caption)
    }
}

/** S11 Trap summary: where the student got stuck, each with its video segment and a retry. */
@Composable
fun TrapSummaryScreen(
    traps: List<String>,
    onWatch: (Int) -> Unit,
    onRetry: (Int) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    /** True when the pack has no video: the button opens Didi's spoken explanation. */
    explainOnly: Boolean = false,
) {
    RaahiScreen(modifier = modifier) {
        BackRow(stringResource(R.string.s11_title, traps.size), onBack)
        DidiBubble(
            text = stringResource(R.string.s11_title, traps.size),
            en = stringResource(R.string.s11_encourage),
            alignTop = true,
        )
        if (traps.isEmpty()) {
            DidiBubble(text = stringResource(R.string.s11_none), wave = true)
        }
        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            traps.forEach { TrapChip(it) }
        }
        traps.forEachIndexed { i, trap ->
            RaahiCard {
                TrapChip(trap)
                // Stacked, full width: "वीडियो का वो हिस्सा देखो" is long in ta/ml and must not break mid-word.
                ChoiceTile(
                    label = stringResource(if (explainOnly) R.string.s11_explain else R.string.s11_watch_segment),
                    onClick = { onWatch(i) },
                    tone = Tone.Neel,
                    icon = if (explainOnly) RaahiIcons.Speaker else RaahiIcons.Video,
                    iconSize = 22.dp,
                    labelStyle = RaahiTheme.typography.card,
                    modifier = Modifier.fillMaxWidth(),
                )
                ChoiceTile(
                    label = stringResource(R.string.s11_try_again),
                    onClick = { onRetry(i) },
                    tone = Tone.Leaf,
                    icon = RaahiIcons.Retry,
                    iconSize = 22.dp,
                    labelStyle = RaahiTheme.typography.card,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

data class VideoSegmentUiState(
    val trap: String,
    val segmentLabel: String,
    val segmentStart: Float,
    val segmentEnd: Float,
    val played: Float,
    val speed: Float,
    val lines: List<TranscriptLine>,
    val currentLine: Int,
    /** Set when there is no video: Didi reads [lines] aloud instead. */
    val noVideoNote: String? = null,
    val source: String? = null,
)

/** S12 Video segment: only the linked part of the lesson, transcript below, works offline. */
@Composable
fun VideoSegmentScreen(
    state: VideoSegmentUiState,
    onSpeed: (Float) -> Unit,
    onLine: (Int) -> Unit,
    onPlay: () -> Unit,
    onRetry: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    RaahiScreen(
        modifier = modifier,
        bottom = {
            ChoiceTile(
                label = stringResource(R.string.s11_try_again),
                onClick = onRetry,
                tone = Tone.Solid,
                icon = RaahiIcons.Retry,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(8.dp))
        },
    ) {
        BackRow(state.trap, onBack)
        VideoSegmentPlayer(
            segmentLabel = state.segmentLabel,
            segmentStart = state.segmentStart,
            segmentEnd = state.segmentEnd,
            played = state.played,
            speeds = listOf(0.75f, 1f, 1.25f),
            speed = state.speed,
            onSpeed = onSpeed,
            lines = state.lines,
            currentLine = state.currentLine,
            onLine = onLine,
            onPlay = onPlay,
            noVideoNote = state.noVideoNote,
            listenLabel = stringResource(R.string.s12_listen),
        )
        if (state.source != null) SourceLine(state.source)
    }
}

data class TrapClearedUiState(val trap: String, val badge: String, val fromNode: String, val toNode: String, val recheckDays: Int)

/** S13 Trap cleared: celebration, the climber moves up, badge, next spaced re-check date. */
@Composable
fun TrapClearedScreen(state: TrapClearedUiState, onContinue: () -> Unit, modifier: Modifier = Modifier) {
    RaahiScreen(
        modifier = modifier,
        bottom = {
            ChoiceTile(
                label = stringResource(R.string.s13_continue),
                onClick = onContinue,
                tone = Tone.Solid,
                icon = RaahiIcons.Mountain,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(8.dp))
        },
    ) {
        DidiBubble(
            text = stringResource(R.string.s13_title, state.trap),
            en = stringResource(R.string.s13_two_in_row),
            didiWidth = 72.dp,
            wave = true,
        )
        ClimbStrip(fromLabel = state.fromNode, toLabel = state.toNode)
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            StatusPill(stringResource(R.string.s13_badge, state.badge), Tone.Gold, icon = RaahiIcons.Trophy)
            TrapChip(state.trap, cleared = true)
        }
        ChoiceTile(
            label = stringResource(R.string.s13_recheck, state.recheckDays),
            onClick = {},
            tone = Tone.Neel,
            icon = RaahiIcons.Calendar,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
