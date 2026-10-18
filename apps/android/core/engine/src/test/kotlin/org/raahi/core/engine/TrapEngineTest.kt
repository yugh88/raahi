package org.raahi.core.engine

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TrapEngineTest {
    private val lesson = Curated.electricity
    private val engine = TrapEngine(lesson)

    @Test fun `every question has exactly one right answer and every trap has questions and an explanation`() {
        lesson.questions.forEach { q -> assertEquals(q.id, 1, q.options.count { it.correct }) }
        lesson.traps.forEach { t ->
            assertTrue(t.id, lesson.questions.count { q -> q.options.any { it.trap == t.id } } >= 2)
            assertTrue(t.id, t.explain.size >= 3)
        }
    }

    @Test fun `trap test has one question per topic`() {
        val test = engine.trapTest(emptySet())
        assertEquals(lesson.topics.size, test.size)
        assertEquals(lesson.topics.map { it.id }, test.map { it.topic })
    }

    @Test fun `wrong answers point to their traps, most frequent first`() {
        val answers = listOf(
            Answer("q_ohm_1", 0, 1), // 18 A: multiplied
            Answer("q_ohm_2", 1, 2), // 48 A: multiplied
            Answer("q_comb_1", 0, 3), // 12 Ω: added in parallel
            Answer("q_circuit_1", 1, 4), // right
        )
        assertEquals(listOf("ohm_rearrange", "parallel_total"), engine.trapsHit(answers))
    }

    @Test fun `retry uses a different question that tests the same trap`() {
        val answers = listOf(Answer("q_ohm_1", 0, 1))
        val retry = engine.retryQuestion("ohm_rearrange", answers)!!
        assertNotEquals("q_ohm_1", retry.id)
        assertTrue(retry.options.any { it.trap == "ohm_rearrange" })
    }

    @Test fun `trap is cleared after two right answers in a row`() {
        val wrong = Answer("q_ohm_1", 0, 1)
        val right1 = Answer("q_ohm_2", 0, 2)
        val right2 = Answer("q_ohm_1", 1, 3)
        assertFalse(engine.isCleared("ohm_rearrange", listOf(wrong, right1)))
        assertTrue(engine.isCleared("ohm_rearrange", listOf(wrong, right1, right2)))
    }

    @Test fun `spaced repetition goes 1, 3, 7, 21 days and resets on a miss`() {
        assertEquals(listOf(1, 3, 7, 21), (0..3).map { SpacedRepetition.daysFor(it) })
        assertEquals(1, SpacedRepetition.nextBox(0, correct = true))
        assertEquals(3, SpacedRepetition.nextBox(3, correct = true))
        assertEquals(0, SpacedRepetition.nextBox(2, correct = false))
    }
}
