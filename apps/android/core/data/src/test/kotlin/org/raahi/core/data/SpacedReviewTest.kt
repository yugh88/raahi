package org.raahi.core.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.raahi.core.data.db.RaahiDatabase

/** Trap found → cleared → spaced re-checks, on the real Electricity questions. */
@RunWith(AndroidJUnit4::class)
class SpacedReviewTest {
    private val context: Context = ApplicationProvider.getApplicationContext()
    private val db = Room.inMemoryDatabaseBuilder(context, RaahiDatabase::class.java).allowMainThreadQueries().build()
    private val content = ContentRepository(context)
    private val learner = LearnerRepository(db, content)
    private val q = content.content.lesson.questions.associateBy { it.id }
    private val day = 24L * 60 * 60 * 1000

    @After fun close() = db.close()

    @Test fun `cleared trap comes back after 1 day, then 3, and a miss reopens it`() = runTest {
        val t0 = 1_000_000_000_000L
        learner.answer(q.getValue("q_ohm_1"), 0, t0) // 18 A: trap found
        learner.answer(q.getValue("q_ohm_2"), 0, t0 + 1) // right
        assertEquals("ohm_rearrange", learner.answer(q.getValue("q_ohm_1"), 1, t0 + 2)) // right again: cleared
        var trap = db.learner().trap("ohm_rearrange")!!
        assertEquals(t0 + 2 + 1 * day, trap.dueAt)

        learner.answer(q.getValue("q_ohm_2"), 0, t0 + 2 * day) // re-check passed
        trap = db.learner().trap("ohm_rearrange")!!
        assertEquals(1, trap.box)
        assertEquals(t0 + 2 * day + 3 * day, trap.dueAt)

        learner.answer(q.getValue("q_ohm_1"), 2, t0 + 6 * day) // 0.5 A: missed the due re-check
        trap = db.learner().trap("ohm_rearrange")!!
        assertNull(trap.clearedAt)
        assertEquals(0, trap.box)
    }

    @Test fun `a right answer before the re-check is due does not move the box`() = runTest {
        val t0 = 1_000_000_000_000L
        learner.answer(q.getValue("q_comb_1"), 0, t0)
        learner.answer(q.getValue("q_comb_2"), 0, t0 + 1)
        learner.answer(q.getValue("q_comb_1"), 2, t0 + 2)
        learner.answer(q.getValue("q_comb_2"), 0, t0 + 3) // same day: not due yet
        val trap = db.learner().trap("parallel_total")!!
        assertNotNull(trap.clearedAt)
        assertEquals(0, trap.box)
    }
}
