package org.raahi.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.raahi.core.designsystem.component.BigMicButton
import org.raahi.core.designsystem.component.BottomTabs
import org.raahi.core.designsystem.component.ChoiceTile
import org.raahi.core.designsystem.component.DidiBubble
import org.raahi.core.designsystem.component.HearText
import org.raahi.core.designsystem.component.OfflineBanner
import org.raahi.core.designsystem.component.OpportunityCard
import org.raahi.core.designsystem.component.ProgressAvatarButton
import org.raahi.core.designsystem.component.RaahiIcons
import org.raahi.core.designsystem.component.RaahiScreen
import org.raahi.core.designsystem.component.RaahiTab
import org.raahi.core.designsystem.component.StatusPill
import org.raahi.core.designsystem.component.initialsOf
import org.raahi.core.designsystem.theme.RaahiTheme
import org.raahi.core.designsystem.theme.Tone

data class HomeEvent(val title: String, val meta: String)

data class HomeUiState(
    val name: String,
    val streakDays: Int,
    /** null when online; otherwise how long ago the last sync happened. */
    val lastSyncedAgo: String?,
    val lessonMeta: String,
    /** null until the student has done the scheme check. */
    val scholarshipCount: Int?,
    val moneyMeta: String,
    val pathMeta: String,
    val nudgeMinutes: Int,
    /** Upcoming NGO session, shown only when one exists. */
    val event: HomeEvent?,
)

/** S3 Home: exactly three big cards — today's lesson, money you may get, your mountain. */
@Composable
fun HomeScreen(
    state: HomeUiState,
    onLesson: () -> Unit,
    onMoney: () -> Unit,
    onPath: () -> Unit,
    onEvent: () -> Unit,
    onProgress: () -> Unit,
    onMic: () -> Unit,
    onTab: (RaahiTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    RaahiScreen(
        modifier = modifier,
        bottom = {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                DidiBubble(
                    text = stringResource(R.string.s3_nudge, state.nudgeMinutes),
                    didiWidth = 44.dp,
                    modifier = Modifier.weight(1f),
                )
                BigMicButton(onClick = onMic, size = 50.dp, modifier = Modifier.padding(bottom = 4.dp))
            }
            BottomTabs(selected = RaahiTab.Home, onSelect = onTab)
        },
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            HearText(stringResource(R.string.s3_greeting, state.name), RaahiTheme.typography.title, Modifier.weight(1f))
            StatusPill(stringResource(R.string.s3_streak_days, state.streakDays), Tone.Gold, icon = RaahiIcons.Flame)
            ProgressAvatarButton(initialsOf(state.name), onProgress)
        }
        if (state.lastSyncedAgo != null) {
            OfflineBanner(stringResource(R.string.s3_offline_synced, state.lastSyncedAgo))
        }
        BigCard(stringResource(R.string.s3_today_lesson), state.lessonMeta, RaahiIcons.Play, Tone.Neel, onLesson)
        BigCard(
            state.scholarshipCount?.let { stringResource(R.string.s3_money_title, it) } ?: stringResource(R.string.s3_money_check),
            state.moneyMeta, RaahiIcons.Coin, Tone.Leaf, onMoney)
        BigCard(stringResource(R.string.s3_your_path), state.pathMeta, RaahiIcons.Compass, Tone.Gold, onPath)
        if (state.event != null) {
            OpportunityCard(
                title = state.event.title,
                subtitle = state.event.meta,
                pill = { StatusPill(stringResource(R.string.s3_upcoming_session), Tone.Neel, icon = RaahiIcons.Calendar) },
                onClick = onEvent,
            )
        }
    }
}

@Composable
private fun BigCard(title: String, meta: String, icon: Int, tone: Tone, onClick: () -> Unit) {
    ChoiceTile(
        label = title,
        en = meta,
        icon = icon,
        tone = tone,
        onClick = onClick,
        minHeight = 72.dp,
        modifier = Modifier.fillMaxWidth(),
    )
}

/** Phase 1 design-reference state (docs/ui-reference.html S3). Replaced by HomeViewModel in Phase 2. */
@Composable
fun homeDesignReference() = HomeUiState(
    name = stringResource(R.string.designref_name),
    streakDays = 6,
    lastSyncedAgo = stringResource(R.string.designref_synced_ago),
    lessonMeta = stringResource(R.string.designref_lesson_meta),
    scholarshipCount = 3,
    moneyMeta = stringResource(R.string.designref_money_meta),
    pathMeta = stringResource(R.string.designref_path_meta),
    nudgeMinutes = 8,
    event = HomeEvent(stringResource(R.string.designref_event_title), stringResource(R.string.designref_event_meta)),
)
