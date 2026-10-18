package org.raahi.feature.learn

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
class LearnScreenshotTest(private val language: AppLanguage) {
    @get:Rule val compose = createComposeRule()

    @Test fun s04_lesson() = compose.captureScreen("s04_lesson", language) {
        LessonQuestionScreen(lessonDesignReference(picked = 1), {}, {}, {})
    }

    @Test fun s10_trapTest() = compose.captureScreen("s10_trap_test", language) {
        TrapTestScreen(trapTestDesignReference(), {}, {}, {}, {})
    }

    @Test fun s11_trapSummary() = compose.captureScreen("s11_trap_summary", language) {
        TrapSummaryScreen(trapsDesignReference(), {}, {}, {})
    }

    @Test fun s12_videoSegment() = compose.captureScreen("s12_video_segment", language) {
        VideoSegmentScreen(videoDesignReference(), {}, {}, {}, {}, {})
    }

    @Test fun s13_trapCleared() = compose.captureScreen("s13_trap_cleared", language) {
        TrapClearedScreen(trapClearedDesignReference(), {})
    }

    companion object {
        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters(name = "{0}")
        fun params() = ScreenshotLanguages
    }
}
