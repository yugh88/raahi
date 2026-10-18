package org.raahi.core.designsystem.component

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.raahi.core.designsystem.R
import org.raahi.core.designsystem.speech.LocalSpeaker
import org.raahi.core.designsystem.theme.RaahiColors
import org.raahi.core.designsystem.theme.RaahiTheme
import org.raahi.core.designsystem.theme.Tone

/** Exactly three tabs: घर · सीखो · कमाओ. Profile/progress is the avatar, not a tab. */
enum class RaahiTab(@StringRes val label: Int, @DrawableRes val icon: Int) {
    Home(R.string.tab_home, RaahiIcons.Home),
    Learn(R.string.tab_learn, RaahiIcons.Book),
    Earn(R.string.tab_earn, RaahiIcons.Brief),
}

@Composable
fun BottomTabs(selected: RaahiTab?, onSelect: (RaahiTab) -> Unit, modifier: Modifier = Modifier) {
    val speaker = LocalSpeaker.current
    Column(modifier.fillMaxWidth()) {
        HorizontalDivider(color = RaahiColors.Line, thickness = 1.dp)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
            RaahiTab.entries.forEach { tab ->
                val on = tab == selected
                val label = stringResource(tab.label)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 56.dp)
                        .clickable(role = Role.Tab) {
                            speaker.speak(label)
                            onSelect(tab)
                        }
                        .semantics { this.selected = on }
                        .padding(top = 7.dp, bottom = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(1.dp),
                ) {
                    val color = if (on) RaahiColors.Neel else RaahiColors.Muted
                    Icon(painterResource(tab.icon), null, tint = color, modifier = Modifier.size(22.dp))
                    Text(
                        label,
                        style = RaahiTheme.typography.caption.copy(fontWeight = if (on) FontWeight.Bold else FontWeight.Medium),
                        color = color,
                    )
                }
            }
        }
    }
}

/** Back arrow + title row used by pushed screens (`.row` with `i-back`). */
@Composable
fun BackRow(text: String, onBack: () -> Unit, modifier: Modifier = Modifier, trailing: (@Composable RowScope.() -> Unit)? = null) {
    val back = stringResource(R.string.back)
    Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier
                .size(48.dp)
                .clip(CircleShape)
                .clickable(role = Role.Button, onClick = onBack)
                .semantics { contentDescription = back },
            contentAlignment = Alignment.CenterStart,
        ) {
            Icon(painterResource(RaahiIcons.Back), null, tint = RaahiColors.Muted, modifier = Modifier.size(22.dp))
        }
        HearText(text, RaahiTheme.typography.card.copy(color = RaahiColors.Muted, fontWeight = FontWeight.Medium), Modifier.weight(1f))
        trailing?.invoke(this)
    }
}

/** The avatar at top right that opens S7 Progress. */
@Composable
fun ProgressAvatarButton(initials: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val label = stringResource(R.string.my_progress)
    Box(
        modifier
            .size(48.dp)
            .clip(CircleShape)
            .clickable(role = Role.Button, onClick = onClick)
            .semantics { contentDescription = label },
        contentAlignment = Alignment.Center,
    ) {
        ProfileAvatar(initials, Tone.Leaf, size = 36.dp)
    }
}

/**
 * Screen frame: white surface, scrollable body with the reference spacing (gap 9dp), and an
 * optional fixed bottom area (mic dock and/or tabs) like `.micwrap` / `.tabs` (margin-top:auto).
 */
@Composable
fun RaahiScreen(
    modifier: Modifier = Modifier,
    bottom: (@Composable ColumnScope.() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier
            .fillMaxSize()
            .background(RaahiColors.Surface)
            .statusBarsPadding(),
    ) {
        Column(
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .then(if (bottom == null) Modifier.navigationBarsPadding() else Modifier)
                .verticalScroll(rememberScrollState())
                .padding(start = 16.dp, end = 16.dp, top = 10.dp, bottom = 10.dp),
            verticalArrangement = Arrangement.spacedBy(9.dp),
            content = content,
        )
        if (bottom != null) {
            // The fixed bottom area (mic, tabs) stays clear of the gesture/navigation bar.
            Column(Modifier.fillMaxWidth().navigationBarsPadding().padding(horizontal = 12.dp), content = bottom)
        }
    }
}
