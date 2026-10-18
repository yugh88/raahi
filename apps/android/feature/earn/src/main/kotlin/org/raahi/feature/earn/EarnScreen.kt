package org.raahi.feature.earn

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
import org.raahi.core.designsystem.component.DidiFigure
import org.raahi.core.designsystem.component.OpportunityCard
import org.raahi.core.designsystem.component.ProgressAvatarButton
import org.raahi.core.designsystem.component.RaahiIcons
import org.raahi.core.designsystem.component.RaahiScreen
import org.raahi.core.designsystem.component.RaahiTab
import org.raahi.core.designsystem.component.StatusPill
import org.raahi.core.designsystem.component.TitleBlock
import org.raahi.core.designsystem.component.initialsOf
import org.raahi.core.designsystem.theme.Tone

/** A matched opportunity card. [pillTone] = clay for deadlines, leaf for eligible, neel for free. */
data class Opportunity(val title: String, val meta: String, val pill: String, val pillTone: Tone)

data class EarnUiState(val name: String, val matchMeta: String, val opportunities: List<Opportunity>)

/**
 * S6 Earn: matched cards (eligibility runs on the phone, so this works offline), deadlines in
 * clay, an entry to the voice scheme check (S19), documents (S18) and "fill the form with Didi".
 * Mentor cards appear only for real onboarded mentors, so none are shown in the demo.
 */
@Composable
fun EarnScreen(
    state: EarnUiState,
    onOpportunity: (Int) -> Unit,
    onSchemeCheck: () -> Unit,
    onDocuments: () -> Unit,
    onFillForm: () -> Unit,
    onProgress: () -> Unit,
    onTab: (RaahiTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    RaahiScreen(
        modifier = modifier,
        bottom = {
            ChoiceTile(
                label = stringResource(R.string.s6_fill_with_didi),
                onClick = onFillForm,
                tone = Tone.Solid,
                leading = { DidiFigure(width = 38.dp) },
                modifier = Modifier.fillMaxWidth(),
            )
            BottomTabs(RaahiTab.Earn, onTab)
        },
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            TitleBlock(stringResource(R.string.s6_title), state.matchMeta, Modifier.weight(1f))
            ProgressAvatarButton(initialsOf(state.name), onProgress)
        }
        state.opportunities.forEachIndexed { i, o ->
            OpportunityCard(
                title = o.title,
                subtitle = o.meta,
                pill = { StatusPill(o.pill, o.pillTone) },
                onClick = { onOpportunity(i) },
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
            ChoiceTile(
                label = stringResource(R.string.s6_check_more),
                onClick = onSchemeCheck,
                tone = Tone.Gold,
                icon = RaahiIcons.Help,
                vertical = true,
                labelStyle = org.raahi.core.designsystem.theme.RaahiTheme.typography.card,
                modifier = Modifier.weight(1f),
            )
            ChoiceTile(
                label = stringResource(R.string.s6_documents),
                onClick = onDocuments,
                tone = Tone.Leaf,
                icon = RaahiIcons.DocCheck,
                vertical = true,
                labelStyle = org.raahi.core.designsystem.theme.RaahiTheme.typography.card,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

/** Phase 1 design reference: docs/ui-reference.html S6 without the mentor card. */
@Composable
fun earnDesignReference() = EarnUiState(
    name = stringResource(R.string.designref_name),
    matchMeta = stringResource(R.string.designref_s6_meta),
    opportunities = listOf(
        Opportunity(stringResource(R.string.designref_s6_sch), stringResource(R.string.designref_s6_sch_meta), stringResource(R.string.s6_days_left, 5), Tone.Clay),
        Opportunity(stringResource(R.string.designref_s6_naps), stringResource(R.string.designref_s6_naps_meta), stringResource(R.string.s6_eligible), Tone.Leaf),
        Opportunity(stringResource(R.string.designref_s6_pmkvy), stringResource(R.string.designref_s6_pmkvy_meta), stringResource(org.raahi.core.designsystem.R.string.price_free), Tone.Neel),
    ),
)
