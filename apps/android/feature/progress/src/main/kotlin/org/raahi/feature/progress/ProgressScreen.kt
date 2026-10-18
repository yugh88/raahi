package org.raahi.feature.progress

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.raahi.core.designsystem.component.BackRow
import org.raahi.core.designsystem.component.ChoiceTile
import org.raahi.core.designsystem.component.DidiBubble
import org.raahi.core.designsystem.component.EnText
import org.raahi.core.designsystem.component.HearText
import org.raahi.core.designsystem.component.ProfileAvatar
import org.raahi.core.designsystem.component.RaahiCard
import org.raahi.core.designsystem.component.RaahiIcons
import org.raahi.core.designsystem.component.RaahiScreen
import org.raahi.core.designsystem.component.StatusPill
import org.raahi.core.designsystem.theme.RaahiColors
import org.raahi.core.designsystem.theme.RaahiTheme
import org.raahi.core.designsystem.theme.Tone
import org.raahi.core.designsystem.R as DsR

data class Badge(val label: String, @DrawableRes val icon: Int, val tone: Tone, val locked: Boolean)

data class ProgressUiState(
    val name: String,
    val streakDays: Int,
    val streakMeta: String,
    val badges: List<Badge>,
    /** null until the school joins and real weekly counts exist. Never an individual rank. */
    val schoolRank: String?,
    val verifiedSkills: Int,
)

/** S7 Progress: streak, badges, school-vs-school (never individual) rank, Skill Passport QR. */
@Composable
fun ProgressScreen(
    state: ProgressUiState,
    onPassport: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    /** Easy reading (low reading level) is on; null hides the switch. */
    easyReading: Boolean? = null,
    onEasyReading: (Boolean) -> Unit = {},
) {
    RaahiScreen(modifier = modifier) {
        BackRow(stringResource(DsR.string.my_progress), onBack)
        DidiBubble(stringResource(R.string.s7_cheer, state.name), didiWidth = 46.dp, wave = true)
        if (easyReading != null) {
            ChoiceTile(
                label = stringResource(if (easyReading) R.string.s7_easy_on else R.string.s7_easy_off),
                en = stringResource(R.string.s7_easy_meta),
                onClick = { onEasyReading(!easyReading) },
                tone = if (easyReading) Tone.Neel else Tone.Line,
                icon = RaahiIcons.Speaker,
                selected = easyReading,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        ChoiceTile(
            label = stringResource(R.string.s7_streak, state.streakDays),
            en = state.streakMeta,
            onClick = {},
            tone = Tone.Gold,
            icon = RaahiIcons.Flame,
            modifier = Modifier.fillMaxWidth(),
        )
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
            state.badges.forEach { b ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    ProfileAvatar("", if (b.locked) Tone.Muted else b.tone, size = 44.dp, icon = if (b.locked) RaahiIcons.Lock else b.icon)
                    HearText(if (b.locked) stringResource(R.string.s7_locked) else b.label, RaahiTheme.typography.caption, textAlign = TextAlign.Center)
                }
            }
        }
        RaahiCard {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                HearText(stringResource(R.string.s7_team), RaahiTheme.typography.card, Modifier.weight(1f))
                StatusPill(state.schoolRank ?: stringResource(R.string.s7_soon), if (state.schoolRank != null) Tone.Leaf else Tone.Muted)
            }
            if (state.schoolRank == null) HearText(stringResource(R.string.s7_team_pending), RaahiTheme.typography.body.copy(color = RaahiColors.Muted))
            EnText(stringResource(R.string.s7_team_meta))
        }
        RaahiCard(borderColor = RaahiColors.Neel, onClick = onPassport) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                HearText(stringResource(R.string.s7_passport), RaahiTheme.typography.card.copy(color = RaahiColors.Neel), Modifier.weight(1f))
                Icon(painterResource(RaahiIcons.Shield), null, tint = RaahiColors.Neel, modifier = Modifier.size(20.dp))
            }
            EnText(stringResource(R.string.s7_passport_meta, state.verifiedSkills))
            ChoiceTile(stringResource(R.string.s7_show_qr), onPassport, Modifier.fillMaxWidth(), icon = RaahiIcons.Qr, tone = Tone.Neel)
        }
    }
}

/** Phase 1 design reference: docs/ui-reference.html S7 without the school rank number. */
@Composable
fun progressDesignReference() = ProgressUiState(
    name = stringResource(R.string.designref_name),
    streakDays = 6,
    streakMeta = stringResource(R.string.designref_s7_streak_meta),
    badges = listOf(
        Badge(stringResource(R.string.designref_badge_maths), RaahiIcons.Star, Tone.Neel, locked = false),
        Badge(stringResource(R.string.designref_badge_electricity), RaahiIcons.Bulb, Tone.Leaf, locked = false),
        Badge("", RaahiIcons.Tool, Tone.Muted, locked = true),
    ),
    schoolRank = null,
    verifiedSkills = 3,
)
