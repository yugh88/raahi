package org.raahi.core.engine

import org.raahi.core.model.AppLanguage
import org.raahi.core.model.FactDef
import org.raahi.core.model.Localized

/** Plain-language reasons for S20, taken from the deciding rule's own text. Nothing is invented. */
object VerdictExplainer {
    data class Explanation(val reason: String, val toConfirm: List<String>, val spoken: String)

    /**
     * @param facts the scheme-check questions; when given, a "maybe" names the exact questions
     *   whose answers are missing, instead of listing every rule that is still open
     */
    fun explain(result: SchemeResult, language: AppLanguage, facts: List<FactDef> = emptyList()): Explanation {
        val reason = when (result.verdict) {
            Verdict.NotEligible -> result.failed.first().reason.get(language)
            Verdict.Maybe -> {
                val byId = facts.associateBy { it.id }
                val asks = result.missingFacts.mapNotNull { byId[it] }
                if (asks.isEmpty()) {
                    result.missing.joinToString(" ") { it.reason.get(language) }
                } else {
                    needToKnow.get(language) + " " + asks.joinToString(" · ") { it.question.get(language) }
                }
            }
            Verdict.Eligible -> result.scheme.benefit.get(language)
        }
        val confirm = result.toConfirm.map { it.reason.get(language) }
        val word = verdictWord(result.verdict).get(language)
        val spoken = "${result.scheme.name.get(language)}: $word. $reason"
        return Explanation(reason, confirm, spoken)
    }

    private val needToKnow = Localized(en = "Didi still needs to know:", hi = "दीदी को अभी यह जानना है:")

    fun verdictWord(v: Verdict): Localized = when (v) {
        Verdict.Eligible -> Localized(en = "you look eligible", hi = "आप योग्य लगते हो")
        Verdict.Maybe -> Localized(en = "maybe, one thing is still needed", hi = "शायद, एक बात और चाहिए")
        Verdict.NotEligible -> Localized(en = "not eligible", hi = "योग्य नहीं")
    }
}
