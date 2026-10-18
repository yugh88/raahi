package org.raahi.feature.roadmap

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.raahi.core.designsystem.component.BottomTabs
import org.raahi.core.designsystem.component.ChoiceTile
import org.raahi.core.designsystem.component.EnText
import org.raahi.core.designsystem.component.HearText
import org.raahi.core.designsystem.component.MountainNode
import org.raahi.core.designsystem.component.MountainPath
import org.raahi.core.designsystem.component.NodeState
import org.raahi.core.designsystem.component.RaahiCard
import org.raahi.core.designsystem.component.RaahiIcons
import org.raahi.core.designsystem.component.RaahiScreen
import org.raahi.core.designsystem.component.RaahiTab
import org.raahi.core.designsystem.component.StatusPill
import org.raahi.core.designsystem.component.TitleBlock
import org.raahi.core.designsystem.theme.RaahiColors
import org.raahi.core.designsystem.theme.RaahiTheme
import org.raahi.core.designsystem.theme.Tone

data class NodeDetail(val what: String, val why: String, val time: String)

data class CareerMountainUiState(
    val career: String,
    val careerMeta: String,
    /** Summit first, base last. */
    val nodes: List<MountainNode>,
    val selected: Int,
    val detail: NodeDetail?,
)

/**
 * S9 Career mountain: base = foundation skills, middle = lessons/tests/exams/courses,
 * summit = real matched outcomes. Tap a node to see what it is, why it matters and the time.
 */
@Composable
fun CareerMountainScreen(
    state: CareerMountainUiState,
    onNode: (Int) -> Unit,
    onStart: () -> Unit,
    onCourses: () -> Unit,
    onTab: (RaahiTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    RaahiScreen(modifier = modifier, bottom = { BottomTabs(RaahiTab.Learn, onTab) }) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TitleBlock(stringResource(R.string.s9_title, state.career), state.careerMeta, Modifier.weight(1f))
            ChoiceTile(
                label = stringResource(R.string.s9_courses),
                onClick = onCourses,
                tone = Tone.Neel,
                icon = RaahiIcons.Book,
                iconSize = 20.dp,
                labelStyle = RaahiTheme.typography.card,
                contentPadding = 10.dp,
            )
        }
        MountainPath(nodes = state.nodes, onNodeClick = onNode, climberLabel = stringResource(R.string.s9_climber))
        val detail = state.detail
        if (detail != null) {
            val node = state.nodes[state.selected]
            RaahiCard(borderColor = RaahiColors.Neel) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    HearText(node.title, RaahiTheme.typography.tile, Modifier.weight(1f))
                    StatusPill(stringResource(R.string.s9_time, detail.time), Tone.Gold, icon = RaahiIcons.Clock)
                }
                Label(stringResource(R.string.s9_what))
                HearText(detail.what, RaahiTheme.typography.body)
                Label(stringResource(R.string.s9_why))
                HearText(detail.why, RaahiTheme.typography.body)
                ChoiceTile(
                    label = stringResource(R.string.s9_start),
                    onClick = onStart,
                    tone = Tone.Solid,
                    icon = RaahiIcons.Play,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun Label(text: String) = EnText(text, color = RaahiColors.Neel)

/** Phase 1 design reference: the ITI Electrician mountain from the master prompt's demo slice. */
@Composable
fun mountainDesignReference() = CareerMountainUiState(
    career = stringResource(R.string.designref_career),
    careerMeta = stringResource(R.string.designref_career_meta),
    nodes = listOf(
        MountainNode(stringResource(R.string.designref_n_job), stringResource(R.string.designref_sub_outcome_job), NodeState.Summit, RaahiIcons.Brief),
        MountainNode(stringResource(R.string.designref_n_naps), stringResource(R.string.designref_sub_outcome_appr), NodeState.Summit, RaahiIcons.Tool),
        MountainNode(stringResource(R.string.designref_n_scholarship), stringResource(R.string.designref_sub_outcome_sch), NodeState.Summit, RaahiIcons.Coin),
        MountainNode(stringResource(R.string.designref_n_iti), stringResource(R.string.designref_sub_course), NodeState.Locked, RaahiIcons.School),
        MountainNode(stringResource(R.string.designref_n_boards), stringResource(R.string.designref_sub_exam), NodeState.Locked, RaahiIcons.DocCheck),
        MountainNode(stringResource(R.string.designref_n_ohm), stringResource(R.string.designref_sub_lesson), NodeState.Locked, RaahiIcons.Bulb),
        MountainNode(stringResource(R.string.designref_n_test), stringResource(R.string.designref_sub_test), NodeState.Current, RaahiIcons.Flag),
        MountainNode(stringResource(R.string.designref_n_current), stringResource(R.string.designref_sub_lesson), NodeState.Done, RaahiIcons.Bulb),
        MountainNode(stringResource(R.string.designref_n_basics), stringResource(R.string.designref_sub_foundation), NodeState.Done, RaahiIcons.Calc),
    ),
    selected = 6,
    detail = NodeDetail(
        what = stringResource(R.string.designref_d_what),
        why = stringResource(R.string.designref_d_why),
        time = stringResource(R.string.designref_d_time),
    ),
)
