package org.raahi.core.engine

import org.raahi.core.model.AppLanguage
import org.raahi.core.model.Localized

/** Plain-language reasons for S20, taken from the deciding rule's own text. Nothing is invented. */
object VerdictExplainer {
    data class Explanation(val reason: String, val toConfirm: List<String>, val spoken: String)

    fun explain(result: SchemeResult, language: AppLanguage): Explanation {
        val reason = when (result.verdict) {
            Verdict.NotEligible -> result.failed.first().reason.get(language)
            Verdict.Maybe -> result.missing.joinToString(" ") { it.reason.get(language) }
            Verdict.Eligible -> result.scheme.benefit.get(language)
        }
        val confirm = result.toConfirm.map { it.reason.get(language) }
        val word = verdictWord(result.verdict).get(language)
        val spoken = "${result.scheme.name.get(language)}: $word. $reason"
        return Explanation(reason, confirm, spoken)
    }

    fun verdictWord(v: Verdict): Localized = when (v) {
        Verdict.Eligible -> Localized(en = "you look eligible", hi = "आप योग्य लगते हो")
        Verdict.Maybe -> Localized(en = "maybe, one thing is still needed", hi = "शायद, एक बात और चाहिए")
        Verdict.NotEligible -> Localized(en = "not eligible", hi = "योग्य नहीं")
    }
}
