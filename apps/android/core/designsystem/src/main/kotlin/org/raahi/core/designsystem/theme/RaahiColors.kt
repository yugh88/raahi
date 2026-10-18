package org.raahi.core.designsystem.theme

import androidx.compose.ui.graphics.Color

/** Colour tokens from docs/ui-reference.html. Light theme only; there is no dark theme. */
object RaahiColors {
    val Bg = Color(0xFFF4F6F9)
    val Surface = Color(0xFFFFFFFF)
    val Surface2 = Color(0xFFEEF1F6)
    val Line = Color(0xFFD9DEE7)
    val Ink = Color(0xFF121826)
    val Muted = Color(0xFF586174)
    val Neel = Color(0xFF2440A8)
    val NeelTint = Color(0xFFE4E9FB)
    val OnNeel = Color(0xFFFFFFFF)
    val Gold = Color(0xFFA86F00)
    val GoldTint = Color(0xFFFBEFD2)
    val DidiOrange = Color(0xFFF5A524)
    val Leaf = Color(0xFF1F6F43)
    val LeafTint = Color(0xFFDFF1E6)
    val Clay = Color(0xFFB0402A)
    val ClayTint = Color(0xFFFBE3DD)
}

/** Colour pairs used by tiles, pills and cards (`.tl-*` in the reference). */
enum class Tone(val container: Color, val content: Color) {
    Neel(RaahiColors.NeelTint, RaahiColors.Neel),
    Leaf(RaahiColors.LeafTint, RaahiColors.Leaf),
    Gold(RaahiColors.GoldTint, RaahiColors.Gold),
    Clay(RaahiColors.ClayTint, RaahiColors.Clay),
    Muted(RaahiColors.Surface2, RaahiColors.Muted),

    /** Outlined tile: white with a 1.5dp line border (`.tl-line`). */
    Line(RaahiColors.Surface, RaahiColors.Ink),

    /** Solid primary action (`background: var(--neel)`). */
    Solid(RaahiColors.Neel, RaahiColors.OnNeel),
}
