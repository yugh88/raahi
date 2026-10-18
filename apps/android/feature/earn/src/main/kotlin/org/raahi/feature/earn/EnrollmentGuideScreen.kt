package org.raahi.feature.earn

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import org.raahi.core.designsystem.component.BackRow
import org.raahi.core.designsystem.component.ChoiceTile
import org.raahi.core.designsystem.component.DidiBubble
import org.raahi.core.designsystem.component.EnText
import org.raahi.core.designsystem.component.HearText
import org.raahi.core.designsystem.component.RaahiCard
import org.raahi.core.designsystem.component.RaahiIcons
import org.raahi.core.designsystem.component.RaahiScreen
import org.raahi.core.designsystem.component.TileGrid
import org.raahi.core.designsystem.theme.RaahiColors
import org.raahi.core.designsystem.theme.RaahiTheme
import org.raahi.core.designsystem.theme.Tone
import org.raahi.core.designsystem.R as DsR

/** A non-sensitive draft field. Aadhaar, OTPs, bank details and passwords never appear here. */
data class DraftField(val label: String, val value: String)

data class EnrollmentUiState(
    val scheme: String,
    val steps: List<String>,
    val current: Int,
    val currentDetail: String,
    val portal: String,
    val draft: List<DraftField>,
)

/**
 * S21 Enrollment guide: one official step at a time, read aloud; "हो गया" or tap advances.
 * Assisted apply only: copy buttons for safe fields and a link that opens the official form.
 * Nothing is submitted, scripted or scraped.
 */
@Composable
fun EnrollmentGuideScreen(
    state: EnrollmentUiState,
    onStep: (Int) -> Unit,
    onDone: () -> Unit,
    onCopy: (DraftField) -> Unit,
    onOpenForm: () -> Unit,
    onWithTeacher: () -> Unit,
    onApplied: () -> Unit,
    onListenAgain: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    RaahiScreen(
        modifier = modifier,
        bottom = {
            TileGrid(listOf(0, 1), Modifier.padding(bottom = 8.dp)) { i, m ->
                if (i == 0) {
                    ChoiceTile(stringResource(DsR.string.listen_again), onListenAgain, m, icon = RaahiIcons.Speaker, tone = Tone.Neel, iconSize = 22.dp, labelStyle = RaahiTheme.typography.card)
                } else {
                    ChoiceTile(stringResource(DsR.string.done), onDone, m, icon = RaahiIcons.Check, tone = Tone.Solid, iconSize = 22.dp, labelStyle = RaahiTheme.typography.card)
                }
            }
        },
    ) {
        BackRow(stringResource(R.string.s21_title, state.scheme), onBack)
        EnText(stringResource(DsR.string.step_of, state.current + 1, state.steps.size))
        state.steps.forEachIndexed { i, step ->
            if (i == state.current) {
                RaahiCard(borderColor = RaahiColors.Neel) {
                    StepRow(i, step, done = false, current = true, onClick = { onStep(i) })
                    DidiBubble(text = state.currentDetail, didiWidth = 36.dp, alignTop = true)
                    HearText(stringResource(R.string.s21_draft), RaahiTheme.typography.caption)
                    state.draft.forEach { field -> DraftRow(field, onCopy) }
                    ChoiceTile(
                        label = stringResource(R.string.s21_open_form),
                        en = state.portal,
                        onClick = onOpenForm,
                        tone = Tone.Neel,
                        icon = RaahiIcons.External,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(painterResource(RaahiIcons.Shield), null, tint = RaahiColors.Clay, modifier = Modifier.size(18.dp))
                        HearText(stringResource(R.string.s21_never_store), RaahiTheme.typography.body.copy(color = RaahiColors.Clay, fontSize = RaahiTheme.typography.caption.fontSize))
                    }
                }
            } else {
                StepRow(i, step, done = i < state.current, current = false, onClick = { onStep(i) })
            }
        }
        ChoiceTile(stringResource(R.string.s21_with_teacher), onWithTeacher, Modifier.fillMaxWidth(), icon = RaahiIcons.Users, tone = Tone.Line)
        HearText(stringResource(R.string.s21_csc_unknown), RaahiTheme.typography.caption)
        ChoiceTile(stringResource(R.string.s21_mark_applied), onApplied, Modifier.fillMaxWidth(), icon = RaahiIcons.Yes, tone = Tone.Leaf)
    }
}

@Composable
private fun StepRow(index: Int, text: String, done: Boolean, current: Boolean, onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().heightIn(min = 48.dp).clickable(role = Role.Button, onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        val (bg, fg) = when {
            done -> RaahiColors.Leaf to RaahiColors.OnNeel
            current -> RaahiColors.Neel to RaahiColors.OnNeel
            else -> RaahiColors.Surface2 to RaahiColors.Muted
        }
        Box(Modifier.size(30.dp).clip(CircleShape).background(bg), contentAlignment = Alignment.Center) {
            if (done) {
                Icon(painterResource(RaahiIcons.Check), null, tint = fg, modifier = Modifier.size(18.dp))
            } else {
                Text("${index + 1}", style = RaahiTheme.typography.card, color = fg)
            }
        }
        HearText(text, if (current) RaahiTheme.typography.tile else RaahiTheme.typography.card.copy(color = if (done) RaahiColors.Muted else RaahiColors.Ink), Modifier.weight(1f))
    }
}

@Composable
private fun DraftRow(field: DraftField, onCopy: (DraftField) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Column(Modifier.weight(1f)) {
            EnText(field.label)
            HearText(field.value, RaahiTheme.typography.card)
        }
        ChoiceTile(
            label = stringResource(R.string.s21_copy),
            onClick = { onCopy(field) },
            tone = Tone.Line,
            icon = RaahiIcons.Copy,
            iconSize = 18.dp,
            minHeight = 48.dp,
            contentPadding = 10.dp,
            labelStyle = RaahiTheme.typography.pill,
        )
    }
}

@Composable
fun enrollmentDesignReference() = EnrollmentUiState(
    scheme = stringResource(R.string.designref_s6_sch),
    steps = listOf(
        stringResource(R.string.designref_step1),
        stringResource(R.string.designref_step2),
        stringResource(R.string.designref_step3),
        stringResource(R.string.designref_step4),
        stringResource(R.string.designref_step5),
        stringResource(R.string.designref_step6),
    ),
    current = 2,
    currentDetail = stringResource(R.string.designref_step3_detail),
    portal = stringResource(R.string.designref_portal),
    draft = listOf(
        DraftField(stringResource(R.string.field_name), stringResource(R.string.designref_name)),
        DraftField(stringResource(R.string.field_class), "12"),
        DraftField(stringResource(R.string.field_district), stringResource(R.string.designref_district)),
    ),
)
