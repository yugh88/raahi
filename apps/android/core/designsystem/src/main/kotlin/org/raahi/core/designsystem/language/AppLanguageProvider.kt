package org.raahi.core.designsystem.language

import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.content.res.Resources
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import org.raahi.core.model.AppLanguage
import java.util.Locale

val LocalAppLanguage = staticCompositionLocalOf { AppLanguage.Default }

/** English resources, for the English sublabel under every label (one key, two languages). */
val LocalEnglishResources = staticCompositionLocalOf<Resources?> { null }

/**
 * Applies the student's language inside the app without restarting the Activity and without
 * AppCompat: everything below reads strings from a Context localized to [language].
 * Phase 2: strings from an installed language pack override these resources.
 */
@Composable
fun ProvideAppLanguage(language: AppLanguage, content: @Composable () -> Unit) {
    val base = LocalContext.current
    val localized = remember(base, language) { base.withLocale(Locale.forLanguageTag(language.tag)) }
    val english = remember(base) { base.withLocale(Locale.ENGLISH).resources }
    CompositionLocalProvider(
        LocalContext provides localized,
        LocalConfiguration provides localized.resources.configuration,
        LocalAppLanguage provides language,
        LocalEnglishResources provides english,
        content = content,
    )
}

/** The English version of a string resource, used for `.en` sublabels. */
@Composable
@ReadOnlyComposable
fun enStringResource(@StringRes id: Int, vararg args: Any): String {
    val res = LocalEnglishResources.current ?: LocalContext.current.resources
    return if (args.isEmpty()) res.getString(id) else res.getString(id, *args)
}

private class LocalizedContext(base: Context, private val res: Resources) : ContextWrapper(base) {
    override fun getResources(): Resources = res
}

private fun Context.withLocale(locale: Locale): Context {
    val config = Configuration(resources.configuration).apply { setLocale(locale) }
    return LocalizedContext(this, createConfigurationContext(config).resources)
}
