package org.raahi.feature.earn

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
class EarnScreenshotTest(private val language: AppLanguage) {
    @get:Rule val compose = createComposeRule()

    @Test fun s06_earn() = compose.captureScreen("s06_earn", language) {
        EarnScreen(earnDesignReference(), {}, {}, {}, {}, {}, {})
    }

    @Test fun s18_documents() = compose.captureScreen("s18_documents", language) {
        DocumentsScreen(documentsDesignReference(), {}, {}, {})
    }

    @Test fun s19_schemeCheck() = compose.captureScreen("s19_scheme_check", language) {
        SchemeCheckScreen(schemeQuestionDesignReference(), {}, {}, {}, {})
    }

    @Test fun s20_verdict() = compose.captureScreen("s20_verdict", language) {
        VerdictScreen(verdictDesignReference(), {}, {})
    }

    @Test fun s21_enrollment() = compose.captureScreen("s21_enrollment", language) {
        EnrollmentGuideScreen(enrollmentDesignReference(), {}, {}, {}, {}, {}, {}, {}, {})
    }

    companion object {
        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters(name = "{0}")
        fun params() = ScreenshotLanguages
    }
}
