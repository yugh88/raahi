package org.raahi.core.engine

import org.raahi.core.model.Lesson
import org.raahi.core.model.Question

/** One answer the student gave. */
data class Answer(val questionId: String, val picked: Int, val atMillis: Long)

/**
 * Finds traps (misconceptions) from wrong answers and decides when one is cleared.
 * A wrong option tagged with a trap is evidence of that trap; there is no score.
 */
class TrapEngine(private val lesson: Lesson) {
    private val questions = lesson.questions.associateBy { it.id }

    /** A short trap test: one question per topic, the first not yet seen in its group. */
    fun trapTest(seen: Set<String>): List<Question> =
        lesson.topics.mapNotNull { topic ->
            val inTopic = lesson.questions.filter { it.topic == topic.id }
            inTopic.firstOrNull { it.id !in seen } ?: inTopic.firstOrNull()
        }

    /** Trap ids hit in [answers], most frequent first. */
    fun trapsHit(answers: List<Answer>): List<String> =
        answers.mapNotNull { a -> questions[a.questionId]?.options?.getOrNull(a.picked)?.trap }
            .groupingBy { it }.eachCount()
            .entries.sortedByDescending { it.value }.map { it.key }

    /** A different question that tests the same idea as the trap, for the retry. */
    fun retryQuestion(trapId: String, answers: List<Answer>): Question? {
        val candidates = lesson.questions.filter { q -> q.options.any { it.trap == trapId } }
        val tried = answers.map { it.questionId }.toSet()
        return candidates.firstOrNull { it.id !in tried } ?: candidates.minByOrNull { q -> answers.count { it.questionId == q.id } }
    }

    /** Cleared after the last two answers on this trap's questions were both right. */
    fun isCleared(trapId: String, answers: List<Answer>): Boolean {
        val onTrap = answers.filter { a -> questions[a.questionId]?.options?.any { it.trap == trapId } == true }
        return onTrap.size >= 2 && onTrap.takeLast(2).all { isCorrect(it) }
    }

    fun isCorrect(a: Answer): Boolean = questions[a.questionId]?.correctIndex == a.picked
}

/** Leitner boxes: a cleared trap comes back after 1, 3, 7, then 21 days. */
object SpacedRepetition {
    val intervalsDays = listOf(1, 3, 7, 21)
    private const val DAY = 24L * 60 * 60 * 1000

    /** Next box after an answer: right moves up one box, wrong goes back to the first. */
    fun nextBox(box: Int, correct: Boolean): Int = if (correct) (box + 1).coerceAtMost(intervalsDays.lastIndex) else 0

    fun dueAt(box: Int, fromMillis: Long): Long = fromMillis + intervalsDays[box.coerceIn(0, intervalsDays.lastIndex)] * DAY

    fun daysFor(box: Int): Int = intervalsDays[box.coerceIn(0, intervalsDays.lastIndex)]
}
