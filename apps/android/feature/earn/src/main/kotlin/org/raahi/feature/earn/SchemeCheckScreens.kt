package org.raahi.feature.earn

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.raahi.core.designsystem.component.BackRow
import org.raahi.core.designsystem.component.ChoiceTile
import org.raahi.core.designsystem.component.DidiBubble
import org.raahi.core.designsystem.component.EnText
import org.raahi.core.designsystem.component.HearText
import org.raahi.core.designsystem.component.MicDock
import org.raahi.core.designsystem.component.ProgressDots
import org.raahi.core.designsystem.component.RaahiCard
import org.raahi.core.designsystem.component.RaahiIcons
import org.raahi.core.designsystem.component.RaahiScreen
import org.raahi.core.designsystem.component.SourceLine
import org.raahi.core.designsystem.theme.RaahiColors
import org.raahi.core.designsystem.theme.RaahiTheme
import org.raahi.core.designsystem.theme.Tone
import org.raahi.core.designsystem.R as DsR

data class SchemeQuestionUiState(
    val index: Int,
    val maxQuestions: Int,
    val question: String,
    /** Answer tiles derived from rule thresholds; "don't know" is always added last. */
    val options: List<String>,
    val picked: Int?,
    /** True for income/category: stored as a band on this phone only, never synced. */
    val onDeviceOnly: Boolean,
)

/** S19 Scheme check: Didi asks only the questions that decide eligibility (≤ 6). */
@Composable
fun SchemeCheckScreen(
    state: SchemeQuestionUiState,
    onPick: (Int) -> Unit,
    onBack: () -> Unit,
    onMic: () -> Unit,
    onType: () -> Unit,
    modifier: Modifier = Modifier,
) {
    RaahiScreen(modifier = modifier, bottom = { MicDock(stringResource(DsR.string.speak_or_tap), onMic, onType) }) {
        BackRow(stringResource(R.string.s19_title), onBack)
        ProgressDots(count = state.maxQuestions, current = state.index)
        EnText(stringResource(R.string.s19_progress, state.index + 1, state.maxQuestions))
        DidiBubble(text = state.question, en = stringResource(R.string.s19_why_asking), alignTop = true)
        Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
            val all = state.options + stringResource(R.string.s19_dont_know)
            all.forEachIndexed { i, option ->
                val dontKnow = i == all.lastIndex
                ChoiceTile(
                    label = option,
                    onClick = { onPick(i) },
                    tone = if (state.picked == i) Tone.Neel else if (dontKnow) Tone.Muted else Tone.Line,
                    selected = state.picked == i,
                    icon = if (dontKnow) RaahiIcons.Help else RaahiIcons.Coin,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
        if (state.onDeviceOnly) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(painterResource(RaahiIcons.Shield), null, tint = RaahiColors.Leaf, modifier = Modifier.size(18.dp))
                HearText(stringResource(R.string.s19_on_device), RaahiTheme.typography.caption.copy(color = RaahiColors.Leaf))
            }
        }
    }
}

enum class Verdict { Eligible, Maybe, NotEligible }

data class VerdictItem(val scheme: String, val verdict: Verdict, val reason: String, val source: String)

/** S20 Verdict: eligible / maybe / not, each with the reason from the deciding rule and its source. */
@Composable
fun VerdictScreen(
    items: List<VerdictItem>,
    onApply: (Int) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    RaahiScreen(modifier = modifier) {
        BackRow(stringResource(R.string.s20_title), onBack)
        Verdict.entries.forEach { verdict ->
            val group = items.withIndex().filter { it.value.verdict == verdict }
            if (group.isEmpty()) return@forEach
            val (label, tone, icon) = when (verdict) {
                Verdict.Eligible -> Triple(R.string.s20_eligible, Tone.Leaf, RaahiIcons.Yes)
                Verdict.Maybe -> Triple(R.string.s20_maybe, Tone.Gold, RaahiIcons.Maybe)
                Verdict.NotEligible -> Triple(R.string.s20_not, Tone.Clay, RaahiIcons.No)
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(painterResource(icon), null, tint = tone.content, modifier = Modifier.size(22.dp))
                HearText(stringResource(label), RaahiTheme.typography.tile.copy(color = tone.content))
            }
            group.forEach { (i, item) ->
                RaahiCard(borderColor = tone.content) {
                    HearText(item.scheme, RaahiTheme.typography.card)
                    HearText(item.reason, RaahiTheme.typography.body)
                    SourceLine(stringResource(DsR.string.source_label, item.source))
                    if (verdict != Verdict.NotEligible) {
                        ChoiceTile(
                            label = stringResource(R.string.s20_how_to_apply),
                            onClick = { onApply(i) },
                            tone = tone,
                            icon = RaahiIcons.Chevron,
                            iconSize = 20.dp,
                            labelStyle = RaahiTheme.typography.card,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
            Spacer(Modifier.height(2.dp))
        }
    }
}

@Composable
fun schemeQuestionDesignReference(picked: Int? = null) = SchemeQuestionUiState(
    index = 1,
    maxQuestions = 6,
    question = stringResource(R.string.designref_s19_q),
    options = listOf(
        stringResource(R.string.designref_band1),
        stringResource(R.string.designref_band2),
        stringResource(R.string.designref_band3),
    ),
    picked = picked,
    onDeviceOnly = true,
)

@Composable
fun verdictDesignReference() = listOf(
    VerdictItem(stringResource(R.string.designref_s6_naps), Verdict.Eligible, stringResource(R.string.designref_r_naps), stringResource(R.string.designref_src_naps)),
    VerdictItem(stringResource(R.string.designref_s6_sch), Verdict.Maybe, stringResource(R.string.designref_r_pms), stringResource(R.string.designref_src_pms)),
    VerdictItem(stringResource(R.string.designref_nmms), Verdict.NotEligible, stringResource(R.string.designref_r_nmms), stringResource(R.string.designref_src_nmms)),
)
