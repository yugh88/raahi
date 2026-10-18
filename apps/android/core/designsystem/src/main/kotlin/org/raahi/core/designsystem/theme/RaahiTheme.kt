package org.raahi.core.designsystem.theme

import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import org.raahi.core.designsystem.language.LocalAppLanguage
import org.raahi.core.model.ReadingLevel

val LocalRaahiTypography = staticCompositionLocalOf { raahiTypography(org.raahi.core.model.Script.Devanagari) }

/** Low reading level: screens auto-speak, show pictures first and hide English sublabels. */
val LocalReadingLevel = staticCompositionLocalOf { ReadingLevel.Standard }

private val LightScheme = lightColorScheme(
    primary = RaahiColors.Neel,
    onPrimary = RaahiColors.OnNeel,
    primaryContainer = RaahiColors.NeelTint,
    onPrimaryContainer = RaahiColors.Neel,
    secondary = RaahiColors.Gold,
    secondaryContainer = RaahiColors.GoldTint,
    tertiary = RaahiColors.Leaf,
    tertiaryContainer = RaahiColors.LeafTint,
    error = RaahiColors.Clay,
    errorContainer = RaahiColors.ClayTint,
    background = RaahiColors.Bg,
    onBackground = RaahiColors.Ink,
    surface = RaahiColors.Surface,
    onSurface = RaahiColors.Ink,
    surfaceVariant = RaahiColors.Surface2,
    onSurfaceVariant = RaahiColors.Muted,
    outline = RaahiColors.Line,
)

/**
 * Raahi's only theme (light). Typography follows the script of the current [LocalAppLanguage],
 * so wrap content in [org.raahi.core.designsystem.language.ProvideAppLanguage] first.
 */
@Composable
fun RaahiTheme(
    readingLevel: ReadingLevel = ReadingLevel.Standard,
    content: @Composable () -> Unit,
) {
    val script = LocalAppLanguage.current.script
    val typography = remember(script) { raahiTypography(script) }
    MaterialTheme(colorScheme = LightScheme) {
        CompositionLocalProvider(
            LocalRaahiTypography provides typography,
            LocalReadingLevel provides readingLevel,
            LocalContentColor provides RaahiColors.Ink,
            LocalTextSelectionColors provides TextSelectionColors(RaahiColors.Neel, RaahiColors.NeelTint),
            content = content,
        )
    }
}

object RaahiTheme {
    val typography: RaahiTypography
        @Composable @ReadOnlyComposable get() = LocalRaahiTypography.current
}
