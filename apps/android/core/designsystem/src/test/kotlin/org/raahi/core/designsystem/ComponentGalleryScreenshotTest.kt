package org.raahi.core.designsystem

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.dp
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.raahi.core.designsystem.component.BigMicButton
import org.raahi.core.designsystem.component.BottomTabs
import org.raahi.core.designsystem.component.ChoiceTile
import org.raahi.core.designsystem.component.DidiAvatar
import org.raahi.core.designsystem.component.DidiState
import org.raahi.core.designsystem.component.MicState
import org.raahi.core.designsystem.component.OfflineBanner
import org.raahi.core.designsystem.component.PricePill
import org.raahi.core.designsystem.component.PriceType
import org.raahi.core.designsystem.component.RaahiIcons
import org.raahi.core.designsystem.component.RaahiTab
import org.raahi.core.designsystem.component.SourceLine
import org.raahi.core.designsystem.component.StatusPill
import org.raahi.core.designsystem.component.TrapChip
import org.raahi.core.designsystem.theme.Tone
import org.raahi.core.model.AppLanguage
import org.raahi.core.testing.PHONE_QUALIFIERS
import org.raahi.core.testing.ScreenshotLanguages
import org.raahi.core.testing.captureScreen
import org.robolectric.ParameterizedRobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/** Every component in every state, per language, so token or font changes show up in review. */
@RunWith(ParameterizedRobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = PHONE_QUALIFIERS)
class ComponentGalleryScreenshotTest(private val language: AppLanguage) {
    @get:Rule val compose = createComposeRule()

    @Test
    fun gallery() = compose.captureScreen("components", language) { Gallery() }

    companion object {
        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters(name = "{0}")
        fun params() = ScreenshotLanguages
    }
}

@Composable
private fun Gallery() {
    Column(
        Modifier.verticalScroll(rememberScrollState()).padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(9.dp),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            DidiState.entries.forEach { DidiAvatar(it, size = 56.dp) }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            MicState.entries.forEach { BigMicButton(onClick = {}, state = it, size = 44.dp) }
        }
        ChoiceTile(stringResource(R.string.speak_or_tap), {}, en = "Speak or tap a picture", icon = RaahiIcons.Speaker)
        ChoiceTile(stringResource(R.string.saved_will_send), {}, tone = Tone.Leaf, selected = true, icon = RaahiIcons.Check)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            PriceType.entries.forEach { PricePill(it) }
            StatusPill(stringResource(R.string.verified), Tone.Leaf, icon = RaahiIcons.Shield)
            StatusPill(stringResource(R.string.offline), Tone.Muted)
        }
        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            TrapChip(stringResource(R.string.not_available))
            TrapChip(stringResource(R.string.done), cleared = true)
        }
        OfflineBanner(stringResource(R.string.saved_will_send))
        SourceLine(stringResource(R.string.source_label, "scholarships.gov.in"))
        BottomTabs(selected = RaahiTab.Home, onSelect = {}, modifier = Modifier.fillMaxWidth())
    }
}
