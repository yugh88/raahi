package org.raahi.feature.onboarding

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
import org.raahi.core.designsystem.component.ChoiceTile
import org.raahi.core.designsystem.component.DidiBubble
import org.raahi.core.designsystem.component.HearText
import org.raahi.core.designsystem.component.MicDock
import org.raahi.core.designsystem.component.RaahiIcons
import org.raahi.core.designsystem.component.RaahiScreen
import org.raahi.core.designsystem.component.TileGrid
import org.raahi.core.designsystem.language.enStringResource
import org.raahi.core.designsystem.theme.RaahiColors
import org.raahi.core.designsystem.theme.RaahiTheme
import org.raahi.core.designsystem.theme.Tone
import org.raahi.core.designsystem.theme.raahiTypography
import org.raahi.core.model.AppLanguage

/**
 * S1 Language picker. Comes before anything else; each tile is written in its own script and
 * speaks its own name, so no reading is needed to choose. Tapping a tile picks it.
 */
@Composable
fun LanguagePickerScreen(
    selected: AppLanguage,
    onSelect: (AppLanguage) -> Unit,
    onMic: () -> Unit,
    onType: () -> Unit,
    modifier: Modifier = Modifier,
) {
    RaahiScreen(
        modifier = modifier,
        bottom = {
            MicDock(
                hint = stringResource(R.string.s1_or_say, selected.nativeName),
                onMic = onMic,
                onType = onType,
            )
        },
    ) {
        DidiBubble(
            text = stringResource(R.string.s1_greeting),
            en = enStringResource(R.string.s1_greeting),
            didiWidth = 58.dp,
            wave = true,
        )
        TileGrid(AppLanguage.entries) { language, tileModifier ->
            ChoiceTile(
                label = language.nativeName,
                en = language.englishName,
                onClick = { onSelect(language) },
                tone = if (language == selected) Tone.Neel else Tone.Line,
                selected = language == selected,
                vertical = true,
                // Each tile uses the font of its own script, whatever the app language is.
                labelStyle = raahiTypography(language.script).tileLarge,
                modifier = tileModifier,
            )
        }
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(painterResource(RaahiIcons.Speaker), null, tint = RaahiColors.Muted, modifier = Modifier.size(16.dp))
            HearText(stringResource(R.string.s1_touch_to_hear), RaahiTheme.typography.caption.copy(fontSize = RaahiTheme.typography.en.fontSize))
        }
    }
}
