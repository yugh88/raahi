package org.raahi.feature.teacher

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.raahi.core.designsystem.component.BackRow
import org.raahi.core.designsystem.component.ChoiceTile
import org.raahi.core.designsystem.component.DidiBubble
import org.raahi.core.designsystem.component.EnText
import org.raahi.core.designsystem.component.HearText
import org.raahi.core.designsystem.component.ProfileAvatar
import org.raahi.core.designsystem.component.RaahiIcons
import org.raahi.core.designsystem.component.RaahiScreen
import org.raahi.core.designsystem.component.StatusPill
import org.raahi.core.designsystem.component.initialsOf
import org.raahi.core.designsystem.speech.LocalSpeaker
import org.raahi.core.designsystem.speech.LocalTapToHearEnabled
import org.raahi.core.designsystem.theme.RaahiColors
import org.raahi.core.designsystem.theme.RaahiTheme
import org.raahi.core.designsystem.theme.Tone

data class Learner(val name: String, val tone: Tone)

data class ClassUiState(
    val className: String,
    val learners: List<Learner>,
    val others: Int,
    val stuckCount: Int,
    val stuckTopic: String,
    val phones: Int,
    val recommendName: String,
    val recommendFor: String,
)

/**
 * S8 Teacher class view: pick who is learning on the shared phone, Didi's stuck alert (→ T1),
 * share the pack, recommend a student (→ T7), earnings (→ T5), studio (→ T3), planner (→ T6).
 */
@Composable
fun ClassScreen(
    state: ClassUiState,
    onLearner: (Int) -> Unit,
    onAlert: () -> Unit,
    onSharePack: () -> Unit,
    onRecommend: () -> Unit,
    onEarnings: () -> Unit,
    onStudio: () -> Unit,
    onPlanner: () -> Unit,
    modifier: Modifier = Modifier,
) {
    RaahiScreen(
        modifier = modifier,
        bottom = {
            ChoiceTile(stringResource(R.string.s8_earnings), onEarnings, Modifier.fillMaxWidth(), icon = RaahiIcons.Coin, tone = Tone.Leaf)
            Box(Modifier.height(8.dp))
        },
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            HearText(stringResource(R.string.s8_class, state.className), RaahiTheme.typography.title, Modifier.weight(1f))
            StatusPill(stringResource(R.string.s8_teacher), Tone.Neel)
        }
        EnText(stringResource(R.string.s8_who_now))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
            state.learners.forEachIndexed { i, l ->
                Column(
                    Modifier.clip(RoundedCornerShape(12.dp)).clickable(role = Role.Button) { onLearner(i) }.padding(4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    ProfileAvatar(initialsOf(l.name), l.tone)
                    HearText(l.name, RaahiTheme.typography.caption.copy(color = RaahiColors.Ink), textAlign = TextAlign.Center)
                }
            }
            Column(Modifier.padding(4.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                ProfileAvatar("+${state.others}", Tone.Muted)
                HearText(stringResource(R.string.s8_all), RaahiTheme.typography.caption.copy(color = RaahiColors.Ink))
            }
        }
        val alert = stringResource(R.string.s8_alert, state.stuckCount, state.stuckTopic)
        val speaker = LocalSpeaker.current
        // The whole alert is one tap target: Didi reads it and the heatmap (T1) opens.
        Box(Modifier.clip(RoundedCornerShape(16.dp)).clickable(role = Role.Button) { speaker.speak(alert); onAlert() }) {
            CompositionLocalProvider(LocalTapToHearEnabled provides false) {
                DidiBubble(alert, tone = Tone.Clay, didiWidth = 44.dp)
            }
        }
        ChoiceTile(stringResource(R.string.s8_share_pack, state.phones), onSharePack, Modifier.fillMaxWidth(), icon = RaahiIcons.Share, tone = Tone.Line, labelStyle = RaahiTheme.typography.card)
        ChoiceTile(stringResource(R.string.s8_recommend, state.recommendName, state.recommendFor), onRecommend, Modifier.fillMaxWidth(), icon = RaahiIcons.Star, tone = Tone.Line, labelStyle = RaahiTheme.typography.card)
        Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
            ChoiceTile(stringResource(R.string.s8_studio), onStudio, Modifier.weight(1f), icon = RaahiIcons.Video, tone = Tone.Neel, vertical = true, labelStyle = RaahiTheme.typography.card)
            ChoiceTile(stringResource(R.string.s8_planner), onPlanner, Modifier.weight(1f), icon = RaahiIcons.Bulb, tone = Tone.Gold, vertical = true, labelStyle = RaahiTheme.typography.card)
        }
    }
}

enum class CellState { Stuck, Cleared, NotTried }

data class HeatmapUiState(
    val className: String,
    val topics: List<String>,
    val students: List<String>,
    /** cells[student][topic] */
    val cells: List<List<CellState>>,
    val topicStuckCounts: List<Int>,
)

/** T1 Class traps heatmap: which topics block most students. */
@Composable
fun HeatmapScreen(
    state: HeatmapUiState,
    onSendEasier: () -> Unit,
    onPlanner: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    RaahiScreen(modifier = modifier) {
        BackRow(stringResource(R.string.t1_title), onBack)
        EnText(stringResource(R.string.s8_class, state.className))
        Row(verticalAlignment = Alignment.Bottom) {
            Box(Modifier.width(76.dp))
            state.topics.forEach { t ->
                HearText(t, RaahiTheme.typography.pill.copy(color = RaahiColors.Muted), Modifier.weight(1f).padding(horizontal = 2.dp), textAlign = TextAlign.Center)
            }
        }
        state.students.forEachIndexed { s, name ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                HearText(name, RaahiTheme.typography.card, Modifier.width(76.dp))
                state.cells[s].forEach { c ->
                    val color = when (c) {
                        CellState.Stuck -> RaahiColors.Clay
                        CellState.Cleared -> RaahiColors.Leaf
                        CellState.NotTried -> RaahiColors.Surface2
                    }
                    val icon = when (c) {
                        CellState.Stuck -> RaahiIcons.Alert
                        CellState.Cleared -> RaahiIcons.Check
                        CellState.NotTried -> null
                    }
                    // Icon as well as colour, so the grid reads without colour vision.
                    Box(
                        Modifier.weight(1f).padding(3.dp).heightIn(min = 40.dp).clip(RoundedCornerShape(8.dp)).background(color),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (icon != null) Icon(painterResource(icon), null, tint = RaahiColors.OnNeel, modifier = Modifier.size(20.dp))
                    }
                }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            StatusPill(stringResource(R.string.t1_stuck), Tone.Clay)
            StatusPill(stringResource(R.string.t1_cleared), Tone.Leaf)
            StatusPill(stringResource(R.string.t1_not_tried), Tone.Muted)
        }
        state.topics.forEachIndexed { i, t ->
            if (state.topicStuckCounts[i] > 0) {
                HearText(stringResource(R.string.t1_topic_count, t, state.topicStuckCounts[i]), RaahiTheme.typography.body)
            }
        }
        ChoiceTile(stringResource(R.string.t1_send_easier), onSendEasier, Modifier.fillMaxWidth(), icon = RaahiIcons.Share, tone = Tone.Solid)
        ChoiceTile(stringResource(R.string.t6_title), onPlanner, Modifier.fillMaxWidth(), icon = RaahiIcons.Bulb, tone = Tone.Gold)
    }
}

@Composable
fun classDesignReference() = ClassUiState(
    className = stringResource(R.string.designref_class),
    learners = listOf(
        Learner(stringResource(R.string.designref_student_rahul), Tone.Gold),
        Learner(stringResource(R.string.designref_name), Tone.Leaf),
        Learner(stringResource(R.string.designref_student_aman), Tone.Neel),
    ),
    others = 15,
    stuckCount = 6,
    stuckTopic = stringResource(R.string.designref_trap_fractions),
    phones = 18,
    recommendName = stringResource(R.string.designref_name),
    recommendFor = stringResource(R.string.designref_naps),
)

@Composable
fun heatmapDesignReference() = HeatmapUiState(
    className = stringResource(R.string.designref_class),
    topics = listOf(
        stringResource(R.string.designref_trap_fractions),
        stringResource(R.string.designref_trap_units),
        stringResource(R.string.designref_trap_percent),
    ),
    students = listOf(
        stringResource(R.string.designref_student_rahul),
        stringResource(R.string.designref_name),
        stringResource(R.string.designref_student_aman),
    ),
    cells = listOf(
        listOf(CellState.Stuck, CellState.Cleared, CellState.NotTried),
        listOf(CellState.Stuck, CellState.Cleared, CellState.Stuck),
        listOf(CellState.Cleared, CellState.NotTried, CellState.Stuck),
    ),
    // From the reference alert ("6 बच्चे भिन्न में अटके"); the other topics have no reference count.
    topicStuckCounts = listOf(6, 0, 0),
)
