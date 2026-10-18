package org.raahi.core.engine

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SchemeQuestionSelectorTest {
    private val engine = EligibilityEngine(Curated.facts.facts)
    private val selector = SchemeQuestionSelector(engine, Curated.facts.facts, Curated.schemes.schemes)
    private val options = Curated.facts.facts.associate { f -> f.id to f.options.map { it.id } }

    /** Plays the whole conversation with a fixed set of true answers. */
    private fun run(truth: Map<String, String>): Pair<List<String>, Map<String, String>> {
        val answers = mutableMapOf<String, String>()
        val asked = mutableListOf<String>()
        while (true) {
            val next = selector.next(answers, asked.toSet()) ?: break
            asked += next.id
            truth[next.id]?.let { answers[next.id] = it }
        }
        return asked to answers
    }

    @Test fun `first question splits the schemes - it is never a fact only one scheme uses`() {
        val first = selector.next(emptyMap(), emptySet())!!
        assertNotEquals("tamil_medium", first.id)
        assertNotEquals("obc_group", first.id)
    }

    @Test fun `never asks more than six questions, whatever the student says`() {
        val everyFirst = options.mapValues { it.value.first() }
        val everyLast = options.mapValues { it.value.last() }
        assertTrue(run(everyFirst).first.size <= 6)
        assertTrue(run(everyLast).first.size <= 6)
        assertTrue(run(emptyMap()).first.size <= 6) // "don't know" to everything
    }

    @Test fun `MP girl joining a degree - six questions settle the central degree schemes and rule out other states`() {
        val truth = mapOf(
            "state" to "mp", "stage" to "ug", "gender" to "female", "category" to "obc", "income" to "1l_2_5l",
            "board" to "mp_board", "marks" to "70_85", "course_year" to "first", "school_type" to "govt", "admission" to "merit",
        )
        val (asked, answers) = run(truth)
        assertTrue(asked.size <= 6)
        assertEquals(Verdict.Eligible, engine.evaluate(Curated.scheme("pragati_degree"), answers).verdict)
        // MMVY may still need board and marks after six questions, but is never wrongly ruled out.
        assertNotEquals(Verdict.NotEligible, engine.evaluate(Curated.scheme("mmvy"), answers).verdict)
        // Schemes from other states are ruled out by the state answer alone.
        assertEquals(Verdict.NotEligible, engine.evaluate(Curated.scheme("pudhumai_penn"), answers).verdict)
    }

    @Test fun `does not ask Tamil medium to a student from Madhya Pradesh`() {
        val (asked, _) = run(mapOf("state" to "mp", "stage" to "diploma", "gender" to "male", "income" to "upto_1l"))
        assertTrue("tamil_medium" !in asked)
    }

    @Test fun `stops when nothing is left to decide`() {
        val decided = mapOf(
            "state" to "other", "stage" to "class9_10", "gender" to "male", "category" to "general", "income" to "8l_plus",
            "school_type" to "private", "marks" to "below_50", "board" to "other", "tamil_medium" to "no",
            "obc_group" to "cat1", "course_year" to "later", "admission" to "management",
        )
        assertNull(selector.next(decided, emptySet()))
    }
}

