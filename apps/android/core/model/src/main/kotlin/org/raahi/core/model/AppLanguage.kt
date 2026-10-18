package org.raahi.core.model

/** Writing systems the app has fonts for. English sublabels always use [Latin]. */
enum class Script { Devanagari, Tamil, Telugu, Kannada, Malayalam, Latin }

/**
 * The five demo languages (Add-on A). The order here is the order of the S1 tiles.
 * Phase 2 adds languages from installed language packs; these five are bundled.
 */
enum class AppLanguage(
    val tag: String,
    val nativeName: String,
    val englishName: String,
    val script: Script,
) {
    Hindi("hi", "हिंदी", "Hindi", Script.Devanagari),
    Tamil("ta", "தமிழ்", "Tamil", Script.Tamil),
    Telugu("te", "తెలుగు", "Telugu", Script.Telugu),
    Kannada("kn", "ಕನ್ನಡ", "Kannada", Script.Kannada),
    Malayalam("ml", "മലയാളം", "Malayalam", Script.Malayalam),
    ;

    companion object {
        val Default = Hindi

        fun fromTag(tag: String?): AppLanguage = entries.firstOrNull { it.tag == tag } ?: Default
    }
}
