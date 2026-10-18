package org.raahi.core.testing

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.onRoot
import com.github.takahirom.roborazzi.captureRoboImage
import org.raahi.core.designsystem.language.ProvideAppLanguage
import org.raahi.core.designsystem.theme.RaahiColors
import org.raahi.core.designsystem.theme.RaahiTheme
import org.raahi.core.model.AppLanguage

/**
 * Small low-end phone used for every screenshot: 360 × 720 dp at 2× (720 × 1440 px), which is
 * the common size of ₹6,000–8,000 Android phones. Long Tamil/Malayalam labels must fit here.
 */
const val PHONE_QUALIFIERS = "w360dp-h720dp-xhdpi"

/** Languages every screen is captured in (Add-on A). */
val ScreenshotLanguages: List<Array<Any>> = AppLanguage.entries.map { arrayOf(it) }

/** Renders [content] the way the app does: chosen language, Raahi theme, white surface. */
@Composable
fun RaahiTestFrame(language: AppLanguage, content: @Composable () -> Unit) {
    ProvideAppLanguage(language) {
        RaahiTheme {
            Box(Modifier.fillMaxSize().background(RaahiColors.Surface)) { content() }
        }
    }
}

/**
 * Sets [content] and writes `src/test/screenshots/<name>_<lang>.png` (recorded with
 * `-Proborazzi.test.record=true`, compared with `-Proborazzi.test.verify=true`).
 */
fun ComposeContentTestRule.captureScreen(name: String, language: AppLanguage, content: @Composable () -> Unit) {
    setContent { RaahiTestFrame(language, content) }
    onRoot().captureRoboImage("src/test/screenshots/${name}_${language.tag}.png")
}
