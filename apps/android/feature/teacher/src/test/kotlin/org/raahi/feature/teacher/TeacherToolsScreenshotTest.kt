package org.raahi.feature.teacher

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
import org.raahi.core.designsystem.component.PriceType

/** Screenshots of T4–T7 (a separate class so each runs in a fresh test JVM), in each of the five languages (design reference state). */
@RunWith(ParameterizedRobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = PHONE_QUALIFIERS)
class TeacherToolsScreenshotTest(private val language: AppLanguage) {
    @get:Rule val compose = createComposeRule()

    @Test fun t4_pricing() = compose.captureScreen("t4_pricing", language) {
        PricingScreen(PriceType.PayAfterEarn, "", {}, {}, {}, {})
    }

    @Test fun t5_earnings() = compose.captureScreen("t5_earnings", language) {
        EarningsScreen({}, {})
    }

    @Test fun t6_planner() = compose.captureScreen("t6_planner", language) {
        LessonPlannerScreen(
            listOf(stringResource(R.string.s8_class, "9"), stringResource(R.string.s8_class, "10")),
            setOf(1), stringResource(R.string.designref_topic_electricity), 40, online = false, {}, {}, {},
        )
    }

    @Test fun t7_recommend() = compose.captureScreen("t7_recommend", language) {
        RecommendScreen(
            stringResource(R.string.designref_name), stringResource(R.string.designref_naps), "apprenticeshipindia.gov.in",
            listOf(stringResource(R.string.designref_strength), stringResource(R.string.designref_interest)), {}, {},
        )
    }

    companion object {
        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters(name = "{0}")
        fun params() = ScreenshotLanguages
    }
}
