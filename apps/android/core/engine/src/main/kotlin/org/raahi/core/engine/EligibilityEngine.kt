package org.raahi.core.engine

import org.raahi.core.model.Condition
import org.raahi.core.model.FactDef
import org.raahi.core.model.FactOption
import org.raahi.core.model.Op
import org.raahi.core.model.Rule
import org.raahi.core.model.Scheme

/** Three-valued result: a fact the student hasn't given (or said "don't know") is [Unknown]. */
enum class Tri { Pass, Fail, Unknown }

enum class Verdict { Eligible, Maybe, NotEligible }

data class SchemeResult(
    val scheme: Scheme,
    val verdict: Verdict,
    /** Rules the answers break; the first one is the reason shown. */
    val failed: List<Rule>,
    /** Rules that need a fact the student hasn't given. */
    val missing: List<Rule>,
    /** "check" rules: things only the student or school can confirm. */
    val toConfirm: List<Rule>,
)

/**
 * Decides eligibility from answer-option ids (e.g. income = "2_5l_3_5l").
 * Number facts are bands; a band decides a threshold only when the whole band is on one side.
 */
class EligibilityEngine(facts: List<FactDef>) {
    private val facts = facts.associateBy { it.id }

    fun evaluate(scheme: Scheme, answers: Map<String, String>): SchemeResult {
        val failed = mutableListOf<Rule>()
        val missing = mutableListOf<Rule>()
        val toConfirm = mutableListOf<Rule>()
        for (rule in scheme.rules) {
            if (rule.op == Op.Check) {
                toConfirm += rule
                continue
            }
            when (evaluate(rule, answers)) {
                Tri.Fail -> failed += rule
                Tri.Unknown -> missing += rule
                Tri.Pass -> Unit
            }
        }
        val verdict = when {
            failed.isNotEmpty() -> Verdict.NotEligible
            missing.isNotEmpty() -> Verdict.Maybe
            else -> Verdict.Eligible
        }
        return SchemeResult(scheme, verdict, failed, missing, toConfirm)
    }

    fun evaluate(rule: Rule, answers: Map<String, String>): Tri {
        val fact = rule.fact ?: return Tri.Unknown
        val main = test(Condition(fact, rule.op, rule.values, rule.value), answers)
        val guard = rule.onlyIf ?: return main
        return when (test(guard, answers)) {
            Tri.Fail -> Tri.Pass // rule does not apply to this student
            Tri.Pass -> main
            // Not sure the rule applies: it can still pass for certain, otherwise we can't tell.
            Tri.Unknown -> if (main == Tri.Pass) Tri.Pass else Tri.Unknown
        }
    }

    fun test(c: Condition, answers: Map<String, String>): Tri {
        val answer = answers[c.fact] ?: return Tri.Unknown
        return when (c.op) {
            Op.In -> if (answer in c.values) Tri.Pass else Tri.Fail
            Op.NotIn -> if (answer in c.values) Tri.Fail else Tri.Pass
            Op.Lte, Op.Lt, Op.Gte -> {
                val band = option(c.fact, answer) ?: return Tri.Unknown
                compare(band, c.op, requireNotNull(c.value) { "${c.fact} ${c.op} needs a value" })
            }
            Op.Check -> Tri.Unknown
        }
    }

    private fun option(fact: String, id: String): FactOption? = facts[fact]?.options?.firstOrNull { it.id == id }

    private fun compare(band: FactOption, op: Op, t: Long): Tri {
        val min = band.min ?: Long.MIN_VALUE
        val max = band.max // null = no upper end
        return when (op) {
            Op.Lte -> when {
                max != null && max <= t -> Tri.Pass
                min > t || min == t && !band.minInclusive -> Tri.Fail
                else -> Tri.Unknown
            }
            Op.Lt -> when {
                max != null && (max < t || max == t && !band.maxInclusive) -> Tri.Pass
                min >= t -> Tri.Fail
                else -> Tri.Unknown
            }
            Op.Gte -> when {
                min >= t -> Tri.Pass
                max != null && (max < t || max == t && !band.maxInclusive) -> Tri.Fail
                else -> Tri.Unknown
            }
            else -> Tri.Unknown
        }
    }
}
