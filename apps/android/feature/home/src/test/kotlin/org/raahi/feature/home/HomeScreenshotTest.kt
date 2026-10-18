package org.raahi.feature.home

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
class HomeScreenshotTest(private val language: AppLanguage) {
    @get:Rule val compose = createComposeRule()

    @Test fun s03_home() = compose.captureScreen("s03_home", language) {
        HomeScreen(homeDesignReference(), {}, {}, {}, {}, {}, {}, {})
    }

    companion object {
        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters(name = "{0}")
        fun params() = ScreenshotLanguages
    }
}
