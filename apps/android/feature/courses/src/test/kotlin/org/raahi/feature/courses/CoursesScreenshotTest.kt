package org.raahi.feature.courses

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


/** Screenshot of every screen in this module, in each of the five languages (design reference state). */
@RunWith(ParameterizedRobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = PHONE_QUALIFIERS)
class CoursesScreenshotTest(private val language: AppLanguage) {
    @get:Rule val compose = createComposeRule()

    @Test fun s14_courses() = compose.captureScreen("s14_courses", language) {
        CoursesScreen(coursesDesignReference(), {}, {}, {}, {}, {})
    }

    @Test fun s15_examPlan() = compose.captureScreen("s15_exam_plan", language) {
        ExamPlanScreen(examPlanDesignReference(), {}, {}, {}, {})
    }

    @Test fun s16_payAfterEarn() = compose.captureScreen("s16_pay_after_earn", language) {
        PayAfterEarnScreen(sponsor = null, {}, {}, {})
    }

    companion object {
        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters(name = "{0}")
        fun params() = ScreenshotLanguages
    }
}
