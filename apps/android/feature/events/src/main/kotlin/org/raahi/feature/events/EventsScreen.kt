package org.raahi.feature.events

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import org.raahi.core.designsystem.component.EnText
import org.raahi.core.designsystem.component.HearText
import org.raahi.core.designsystem.component.RaahiCard
import org.raahi.core.designsystem.component.RaahiIcons
import org.raahi.core.designsystem.component.RaahiScreen
import org.raahi.core.designsystem.component.StatusPill
import org.raahi.core.designsystem.theme.RaahiColors
import org.raahi.core.designsystem.theme.RaahiTheme
import org.raahi.core.designsystem.theme.Tone
import org.raahi.core.designsystem.R as DsR

data class EventItem(
    val title: String,
    val kind: String,
    val organiser: String,
    /** null = not set yet → "not available". */
    val date: String?,
    val place: String,
    val distance: String?,
    val isTestEvent: Boolean,
    val rsvp: Boolean,
    val reminderSet: Boolean,
)

/** S17 NGO events: date, place, distance; one-tap RSVP; reminder on the phone; QR check-in. */
@Composable
fun EventsScreen(
    events: List<EventItem>,
    onRsvp: (Int) -> Unit,
    onRemind: (Int) -> Unit,
    onCheckIn: (Int) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val na = stringResource(DsR.string.not_available)
    RaahiScreen(modifier = modifier) {
        BackRow(stringResource(R.string.s17_title), onBack)
        events.forEachIndexed { i, e ->
            RaahiCard {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    StatusPill(e.kind, Tone.Neel, icon = RaahiIcons.Users)
                    if (e.isTestEvent) StatusPill(stringResource(R.string.s17_test_event), Tone.Clay)
                }
                HearText(e.title, RaahiTheme.typography.tile)
                EnText(stringResource(R.string.s17_by, e.organiser))
                InfoRow(RaahiIcons.Calendar, stringResource(R.string.s17_date, e.date ?: na))
                InfoRow(RaahiIcons.Pin, stringResource(R.string.s17_place, e.place))
                InfoRow(RaahiIcons.Compass, stringResource(R.string.s17_distance, e.distance ?: na))
                ChoiceTile(stringResource(R.string.s17_rsvp), { onRsvp(i) }, Modifier.fillMaxWidth(), icon = RaahiIcons.Yes, tone = if (e.rsvp) Tone.Leaf else Tone.Solid, selected = e.rsvp)
                ChoiceTile(stringResource(R.string.s17_remind), { onRemind(i) }, Modifier.fillMaxWidth(), icon = RaahiIcons.Bell, tone = Tone.Gold, selected = e.reminderSet, iconSize = 22.dp, labelStyle = RaahiTheme.typography.card)
                ChoiceTile(stringResource(R.string.s17_checkin), { onCheckIn(i) }, Modifier.fillMaxWidth(), icon = RaahiIcons.Qr, tone = Tone.Line, iconSize = 22.dp, labelStyle = RaahiTheme.typography.card)
            }
        }
    }
}

@Composable
private fun InfoRow(icon: Int, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Icon(painterResource(icon), null, tint = RaahiColors.Muted, modifier = Modifier.size(18.dp))
        HearText(text, RaahiTheme.typography.body, Modifier.weight(1f))
    }
}

/** Phase 1 design reference: the team's own test event, clearly labelled; date and distance not set. */
@Composable
fun eventsDesignReference() = listOf(
    EventItem(
        title = stringResource(R.string.designref_event_title),
        kind = stringResource(R.string.s17_kind_camp),
        organiser = stringResource(R.string.designref_event_org),
        date = null,
        place = stringResource(R.string.designref_event_place),
        distance = null,
        isTestEvent = true,
        rsvp = false,
        reminderSet = false,
    ),
)
