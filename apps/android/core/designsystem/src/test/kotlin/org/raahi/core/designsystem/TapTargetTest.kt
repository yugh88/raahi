package org.raahi.core.designsystem

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.raahi.core.designsystem.component.ChoiceTile
import org.raahi.core.designsystem.component.EnText
import org.raahi.core.designsystem.component.OpportunityCard
import org.raahi.core.designsystem.speech.LocalSpeaker
import org.raahi.core.designsystem.speech.Speaker
import org.raahi.core.model.AppLanguage
import org.raahi.core.testing.RaahiTestFrame
import org.robolectric.RobolectricTestRunner
import androidx.compose.runtime.CompositionLocalProvider

/**
 * Regression: text inside a tappable tile or card must not take the tap for itself
 * (it did, because Compose widens small tap targets to 48dp). One tap = speak + act.
 */
@RunWith(RobolectricTestRunner::class)
class TapTargetTest {
    @get:Rule val compose = createComposeRule()

    private val spoken = mutableListOf<String>()
    private var clicks = 0

    private fun setUp(content: @androidx.compose.runtime.Composable () -> Unit) {
        compose.setContent {
            RaahiTestFrame(AppLanguage.Tamil) {
                CompositionLocalProvider(LocalSpeaker provides Speaker { spoken += it }, content = content)
            }
        }
    }

    @Test
    fun tappingTileSublabelSelectsTheTile() {
        setUp { ChoiceTile("சீக்கிரம் சம்பாதிக்க", onClick = { clicks++ }, en = "Start earning soon") }
        compose.onNodeWithText("Start earning soon").performClick()
        assertEquals(1, clicks)
        assertEquals(listOf("சீக்கிரம் சம்பாதிக்க"), spoken)
    }

    @Test
    fun tappingTileLabelSelectsTheTileAndSpeaksOnce() {
        setUp { ChoiceTile("சீக்கிரம் சம்பாதிக்க", onClick = { clicks++ }, en = "Start earning soon") }
        compose.onNodeWithText("சீக்கிரம் சம்பாதிக்க").performClick()
        assertEquals(1, clicks)
        assertEquals(1, spoken.size)
    }

    @Test
    fun tappingCardTitleOpensTheCard() {
        setUp { OpportunityCard("NAPS apprenticeship", "Electrician", onClick = { clicks++ }) }
        compose.onNodeWithText("NAPS apprenticeship").performClick()
        assertEquals(1, clicks)
        assertEquals(listOf("NAPS apprenticeship"), spoken)
    }

    @Test
    fun plainTextOutsideTilesStillSpeaksWhenTapped() {
        setUp { EnText("Matched to your profile") }
        compose.onNodeWithText("Matched to your profile").performClick()
        assertEquals(listOf("Matched to your profile"), spoken)
    }
}
