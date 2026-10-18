package org.raahi.core.engine

import org.raahi.core.model.FactDef
import org.raahi.core.model.Scheme
import kotlin.math.ln

/**
 * Picks Didi's next scheme-check question (S19, Add-on B). Each candidate fact is scored by
 * what its answer is expected to settle over the still-undecided schemes, with every answer tile
 * equally likely: first the expected number of schemes decided, then the information gain
 * (entropy of the outcomes) to break ties. Stops after [maxQuestions] or when nothing is left.
 */
class SchemeQuestionSelector(
    private val engine: EligibilityEngine,
    private val facts: List<FactDef>,
    private val schemes: List<Scheme>,
    val maxQuestions: Int = 6,
) {
    /**
     * @param answers facts answered so far (option ids)
     * @param asked every fact already asked, including ones answered "don't know"
     * @param limit questions allowed in total; the student can ask Didi to go beyond the first 6
     */
    fun next(answers: Map<String, String>, asked: Set<String>, limit: Int = maxQuestions): FactDef? {
        if (asked.size >= limit) return null
        val open = schemes.filter { engine.evaluate(it, answers).verdict == Verdict.Maybe }
        if (open.isEmpty()) return null
        val needed = open.flatMap { s -> engine.evaluate(s, answers).missing.flatMap { listOfNotNull(it.fact, it.onlyIf?.fact) } }.toSet()
        return facts
            .filter { it.id in needed && it.id !in asked }
            .map { it to score(it, open, answers) }
            .filter { it.second.gain > 0.0 }
            .maxWithOrNull(compareBy<Pair<FactDef, Score>> { it.second.decided }.thenBy { it.second.gain })
            ?.first
    }

    private data class Score(val gain: Double, val decided: Double)

    private fun score(fact: FactDef, open: List<Scheme>, answers: Map<String, String>): Score {
        // Outcome of every open scheme for each possible answer.
        val outcomes = fact.options.map { option ->
            val with = answers + (fact.id to option.id)
            open.map { engine.evaluate(it, with).verdict }
        }
        val counts = outcomes.groupingBy { it }.eachCount().values
        val n = outcomes.size.toDouble()
        val entropy = -counts.sumOf { c -> (c / n).let { p -> p * ln(p) } }
        val decided = outcomes.sumOf { o -> o.count { it != Verdict.Maybe }.toDouble() } / n
        return Score(entropy, decided)
    }
}
