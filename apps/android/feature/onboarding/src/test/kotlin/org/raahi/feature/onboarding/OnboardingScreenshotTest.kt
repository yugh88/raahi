package org.raahi.feature.onboarding

import androidx.compose.ui.test.junit4.v2.createComposeRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.raahi.core.model.AppLanguage
import org.raahi.core.testing.PHONE_QUALIFIERS
import org.raahi.core.testing.ScreenshotLanguages
import org.raahi.core.testing.captureScreen
import org.robolectric.ParameterizedRobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import androidx.compose.ui.res.stringResource
import org.raahi.core.model.Persona

/** Screenshot of every screen in this module, in each of the five languages (design reference state). */
@RunWith(ParameterizedRobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = PHONE_QUALIFIERS)
class OnboardingScreenshotTest(private val language: AppLanguage) {
    @get:Rule val compose = createComposeRule()

    @Test fun s01_languagePicker() = compose.captureScreen("s01_language", language) {
        LanguagePickerScreen(selected = language, onSelect = {}, onMic = {}, onType = {})
    }

    @Test fun s02_name() = step(OnboardingStep.Name, "")
    @Test fun s02_status() = step(OnboardingStep.Status, persona = Persona.Class11To12)
    @Test fun s02_language() = step(OnboardingStep.Language)
    @Test fun s02_interests() = step(OnboardingStep.Interests, interests = setOf(Interest.Making))
    @Test fun s02_goal() = step(OnboardingStep.Goal, goal = Goal.Earn)

    private fun step(
        step: OnboardingStep,
        name: String? = null,
        persona: Persona? = null,
        interests: Set<Interest> = emptySet(),
        goal: Goal? = null,
    ) = compose.captureScreen("s02_${step.name.lowercase()}", language) {
        val state = OnboardingUiState(
            step = step,
            name = name ?: stringResource(R.string.designref_name),
            persona = persona,
            language = language,
            interests = interests,
            goal = goal,
        )
        OnboardingScreen(state, {}, {}, {}, {}, {}, {}, {}, {})
    }

    companion object {
        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters(name = "{0}")
        fun params() = ScreenshotLanguages
    }
}
