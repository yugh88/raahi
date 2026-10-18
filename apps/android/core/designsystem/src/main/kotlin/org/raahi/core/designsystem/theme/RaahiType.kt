package org.raahi.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import org.raahi.core.designsystem.R
import org.raahi.core.model.Script

private val Weights = listOf(400, 500, 600, 700)

private fun variableFamily(res: Int, weights: List<Int> = Weights) = FontFamily(
    weights.map { w ->
        Font(res, FontWeight(w), variationSettings = FontVariation.Settings(FontVariation.weight(w)))
    },
)

/** Baloo 2 family for each script: titles, tiles, bubbles (`--f-app`). */
private val Baloo2 = variableFamily(R.font.baloo2)
private val BalooThambi2 = variableFamily(R.font.baloo_thambi2)
private val BalooTammudu2 = variableFamily(R.font.baloo_tammudu2)
private val BalooTamma2 = variableFamily(R.font.baloo_tamma2)
private val BalooChettan2 = variableFamily(R.font.baloo_chettan2)

/** Mukta: Devanagari body text and every English sublabel (`--f-body`). */
val Mukta = FontFamily(
    Font(R.font.mukta_regular, FontWeight.Normal),
    Font(R.font.mukta_medium, FontWeight.Medium),
    Font(R.font.mukta_semibold, FontWeight.SemiBold),
)
private val NotoSansTamil = variableFamily(R.font.noto_sans_tamil, listOf(400, 500, 600))
private val NotoSansTelugu = variableFamily(R.font.noto_sans_telugu, listOf(400, 500, 600))
private val NotoSansKannada = variableFamily(R.font.noto_sans_kannada, listOf(400, 500, 600))
private val NotoSansMalayalam = variableFamily(R.font.noto_sans_malayalam, listOf(400, 500, 600))

fun uiFontFor(script: Script): FontFamily = when (script) {
    Script.Devanagari, Script.Latin -> Baloo2
    Script.Tamil -> BalooThambi2
    Script.Telugu -> BalooTammudu2
    Script.Kannada -> BalooTamma2
    Script.Malayalam -> BalooChettan2
}

fun bodyFontFor(script: Script): FontFamily = when (script) {
    Script.Devanagari, Script.Latin -> Mukta
    Script.Tamil -> NotoSansTamil
    Script.Telugu -> NotoSansTelugu
    Script.Kannada -> NotoSansKannada
    Script.Malayalam -> NotoSansMalayalam
}

/**
 * Text styles from the reference CSS (px → sp one to one). Every style wraps; key labels
 * are never ellipsized, so Tamil and Malayalam labels up to 40% longer than Hindi still fit.
 */
@Immutable
data class RaahiTypography(
    /** `.ttl` 20 / 700 */
    val title: TextStyle,
    /** `.tile` 15 / 600 */
    val tile: TextStyle,
    /** Language tile names, 20 / 600 */
    val tileLarge: TextStyle,
    /** `.card` 14 / 600 */
    val card: TextStyle,
    /** `.bubble` 14 / 500 */
    val bubble: TextStyle,
    /** Body copy in the script's reading font, 14 / 400 */
    val body: TextStyle,
    /** `.en` 11.5 sublabel, always Mukta */
    val en: TextStyle,
    /** `.pill` 11.5 / 600 */
    val pill: TextStyle,
    /** Small muted labels (tabs, captions) 12 / 500 */
    val caption: TextStyle,
)

fun raahiTypography(script: Script): RaahiTypography {
    val ui = uiFontFor(script)
    val body = bodyFontFor(script)
    // Baloo's tall Indic stacks need more line height than Latin; 1.25em keeps matras clear.
    val lh = 1.3.em
    return RaahiTypography(
        title = TextStyle(fontFamily = ui, fontWeight = FontWeight.Bold, fontSize = 20.sp, lineHeight = 1.25.em),
        tile = TextStyle(fontFamily = ui, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, lineHeight = lh),
        tileLarge = TextStyle(fontFamily = ui, fontWeight = FontWeight.SemiBold, fontSize = 20.sp, lineHeight = lh),
        card = TextStyle(fontFamily = ui, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, lineHeight = lh),
        bubble = TextStyle(fontFamily = ui, fontWeight = FontWeight.Medium, fontSize = 14.sp, lineHeight = 1.4.em),
        body = TextStyle(fontFamily = body, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 1.45.em),
        en = TextStyle(fontFamily = Mukta, fontWeight = FontWeight.Normal, fontSize = 11.5.sp, lineHeight = 1.35.em, color = RaahiColors.Muted),
        pill = TextStyle(fontFamily = ui, fontWeight = FontWeight.SemiBold, fontSize = 11.5.sp, lineHeight = 1.3.em),
        caption = TextStyle(fontFamily = ui, fontWeight = FontWeight.Medium, fontSize = 12.sp, lineHeight = lh, color = RaahiColors.Muted),
    )
}
