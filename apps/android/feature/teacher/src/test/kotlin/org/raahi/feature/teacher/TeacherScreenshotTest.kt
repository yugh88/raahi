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

/** Screenshots of S8 and T1–T3, in each of the five languages (design reference state). */
@RunWith(ParameterizedRobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = PHONE_QUALIFIERS)
class TeacherScreenshotTest(private val language: AppLanguage) {
    @get:Rule val compose = createComposeRule()

    @Test fun s08_classView() = compose.captureScreen("s08_class_view", language) {
        ClassScreen(classDesignReference(), {}, {}, {}, {}, {}, {}, {})
    }

    @Test fun t1_heatmap() = compose.captureScreen("t1_heatmap", language) {
        HeatmapScreen(heatmapDesignReference(), {}, {}, {})
    }

    @Test fun t2_assign() = compose.captureScreen("t2_assign", language) {
        AssignScreen(stringResource(R.string.designref_trap_fractions), stringResource(R.string.designref_segment), 5, 6, saved = true, {}, {})
    }

    @Test fun t3_studio() = compose.captureScreen("t3_studio", language) {
        CourseStudioScreen(listOf(TaggedQuestion(stringResource(R.string.designref_trap_units), stringResource(R.string.designref_segment))), {}, {}, {}, {}, {})
    }

    companion object {
        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters(name = "{0}")
        fun params() = ScreenshotLanguages
    }
}
