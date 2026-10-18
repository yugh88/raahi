package org.raahi.feature.learn

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.raahi.core.designsystem.component.BackRow
import org.raahi.core.designsystem.component.ChoiceTile
import org.raahi.core.designsystem.component.DidiFigure
import org.raahi.core.designsystem.component.HearText
import org.raahi.core.designsystem.component.RaahiIcons
import org.raahi.core.designsystem.component.RaahiScreen
import org.raahi.core.designsystem.component.SourceLine
import org.raahi.core.designsystem.component.StatusPill
import org.raahi.core.designsystem.component.TileShape
import org.raahi.core.designsystem.theme.RaahiColors
import org.raahi.core.designsystem.theme.RaahiTheme
import org.raahi.core.designsystem.theme.Tone

data class LessonQuestionUiState(
    val header: String,
    @DrawableRes val picture: Int,
    val question: String,
    val options: List<String>,
    /** Index the student picked, or null before answering. */
    val picked: Int?,
    val correct: Int,
    val reward: Int,
    val queuedDoubts: Int,
    /** Who wrote the question and from which source. */
    val source: String? = null,
)

/** S4 Lesson question: picture, question, three spoken options, instant feedback. */
@Composable
fun LessonQuestionScreen(
    state: LessonQuestionUiState,
    onPick: (Int) -> Unit,
    onAskDidi: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    RaahiScreen(
        modifier = modifier,
        bottom = {
            ChoiceTile(
                label = stringResource(R.string.s4_ask_didi),
                onClick = onAskDidi,
                tone = Tone.Gold,
                leading = { DidiFigure(width = 36.dp) },
                labelStyle = RaahiTheme.typography.tile.copy(fontSize = RaahiTheme.typography.card.fontSize, color = RaahiColors.Ink),
                modifier = Modifier.fillMaxWidth(),
                trailing = if (state.queuedDoubts > 0) {
                    { StatusPill(stringResource(R.string.s4_queued, state.queuedDoubts), Tone.Line) }
                } else {
                    null
                },
            )
            androidx.compose.foundation.layout.Spacer(Modifier.height(8.dp))
        },
    ) {
        BackRow(state.header, onBack)
        Picture(state.picture)
        HearText(state.question, RaahiTheme.typography.tile)
        OptionList(state.options, state.picked, state.correct, onPick)
        if (state.picked == state.correct) {
            HearText(
                stringResource(R.string.s4_correct_reward, state.reward),
                RaahiTheme.typography.card.copy(color = RaahiColors.Leaf),
            )
        }
        if (state.source != null) SourceLine(state.source)
    }
}

@Composable
internal fun Picture(@DrawableRes icon: Int) {
    Box(
        Modifier.fillMaxWidth().height(92.dp).clip(TileShape).background(RaahiColors.NeelTint),
        contentAlignment = Alignment.Center,
    ) {
        Icon(painterResource(icon), null, tint = RaahiColors.Neel, modifier = Modifier.size(46.dp))
    }
}

/** Three spoken options. After a pick: the right one turns leaf; nothing is ever marked "wrong". */
@Composable
internal fun OptionList(options: List<String>, picked: Int?, correct: Int?, onPick: (Int) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
        options.forEachIndexed { i, option ->
            val showRight = picked != null && correct != null && i == correct && picked == correct
            ChoiceTile(
                label = option,
                onClick = { onPick(i) },
                tone = if (showRight) Tone.Leaf else Tone.Line,
                selected = showRight || (picked == i && correct == null),
                icon = if (showRight) RaahiIcons.Check else RaahiIcons.Speaker,
                iconSize = 20.dp,
                labelStyle = RaahiTheme.typography.tile.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Medium),
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
