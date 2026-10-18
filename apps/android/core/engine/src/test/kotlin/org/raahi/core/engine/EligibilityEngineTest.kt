package org.raahi.core.engine

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.raahi.core.model.AppLanguage
import org.raahi.core.model.Op

class EligibilityEngineTest {
    private val engine = EligibilityEngine(Curated.facts.facts)

    @Test fun `curated data has at least ten schemes with full steps and sources`() {
        val schemes = Curated.schemes.schemes
        assertTrue(schemes.size >= 10)
        schemes.forEach { s ->
            assertTrue("${s.id} source", s.sourceUrl.startsWith("https://"))
            assertTrue("${s.id} steps", s.steps.size >= 3)
            assertTrue("${s.id} hindi name", s.name.hi != null)
            s.rules.filter { it.op != Op.Check }.forEach { r ->
                val fact = Curated.facts.facts.firstOrNull { it.id == r.fact }
                assertTrue("${s.id} uses unknown fact ${r.fact}", fact != null)
                r.values.forEach { v -> assertTrue("${s.id}: ${r.fact}=$v", fact!!.options.any { it.id == v }) }
            }
        }
    }

    @Test fun `NMMSS - class 8 government school, income band up to 3_5 lakh, 55 percent is eligible`() {
        val r = engine.evaluate(
            Curated.scheme("nmmss"),
            mapOf("stage" to "class8", "school_type" to "govt", "income" to "2_5l_3_5l", "marks" to "55_60", "category" to "general"),
        )
        assertEquals(Verdict.Eligible, r.verdict)
    }

    @Test fun `NMMSS - income above 3_5 lakh fails with the guideline's own reason`() {
        val r = engine.evaluate(Curated.scheme("nmmss"), mapOf("stage" to "class8", "income" to "3_5l_4_5l"))
        assertEquals(Verdict.NotEligible, r.verdict)
        assertEquals("माता-पिता की आय ₹3.5 लाख सालाना या उससे कम होनी चाहिए।", VerdictExplainer.explain(r, AppLanguage.Hindi).reason)
    }

    @Test fun `NMMSS - 50 to 55 percent depends on SC-ST relaxation`() {
        val base = mapOf("stage" to "class8", "school_type" to "aided", "income" to "upto_1l", "marks" to "50_55")
        assertEquals(Verdict.Maybe, engine.evaluate(Curated.scheme("nmmss"), base).verdict)
        assertEquals(Verdict.Eligible, engine.evaluate(Curated.scheme("nmmss"), base + ("category" to "st")).verdict)
        assertEquals(Verdict.NotEligible, engine.evaluate(Curated.scheme("nmmss"), base + ("category" to "obc")).verdict)
    }

    @Test fun `NMMSS - Navodaya and private schools are excluded`() {
        val r = engine.evaluate(Curated.scheme("nmmss"), mapOf("school_type" to "central_residential"))
        assertEquals(Verdict.NotEligible, r.verdict)
    }

    @Test fun `MMVY - order of 09-07-2026 uses less than 8 lakh, so the 8 lakh-plus band fails`() {
        val mp = mapOf("state" to "mp", "stage" to "ug", "board" to "mp_board", "marks" to "70_85")
        assertEquals(Verdict.Eligible, engine.evaluate(Curated.scheme("mmvy"), mp + ("income" to "4_5l_8l")).verdict)
        assertEquals(Verdict.NotEligible, engine.evaluate(Curated.scheme("mmvy"), mp + ("income" to "8l_plus")).verdict)
    }

    @Test fun `MMVY - CBSE needs 85 percent, MP Board 70`() {
        val mp = mapOf("state" to "mp", "stage" to "ug", "income" to "upto_1l", "marks" to "70_85")
        assertEquals(Verdict.Eligible, engine.evaluate(Curated.scheme("mmvy"), mp + ("board" to "mp_board")).verdict)
        assertEquals(Verdict.NotEligible, engine.evaluate(Curated.scheme("mmvy"), mp + ("board" to "cbse_icse")).verdict)
    }

    @Test fun `Pragati - income exactly at the 8 lakh band edge cannot be decided`() {
        val girl = mapOf("gender" to "female", "stage" to "ug", "course_year" to "first")
        assertEquals(Verdict.Eligible, engine.evaluate(Curated.scheme("pragati_degree"), girl + ("income" to "4_5l_8l")).verdict)
        // "₹8 lakh or more" includes exactly 8 lakh (allowed) and above (not allowed).
        assertEquals(Verdict.Maybe, engine.evaluate(Curated.scheme("pragati_degree"), girl + ("income" to "8l_plus")).verdict)
    }

    @Test fun `Karnataka - category-1 and other groups have different income limits`() {
        val ka = mapOf("state" to "ka", "category" to "obc", "stage" to "ug", "admission" to "merit", "income" to "1l_2_5l", "marks" to "70_85")
        assertEquals(Verdict.Eligible, engine.evaluate(Curated.scheme("ka_bcwd_pms"), ka + ("obc_group" to "cat1")).verdict)
        assertEquals(Verdict.NotEligible, engine.evaluate(Curated.scheme("ka_bcwd_pms"), ka + ("obc_group" to "g2a3a3b")).verdict)
    }

    @Test fun `Tamil Pudhalvan needs Tamil medium`() {
        val tn = mapOf("state" to "tn", "gender" to "male", "school_type" to "aided", "stage" to "diploma")
        assertEquals(Verdict.Eligible, engine.evaluate(Curated.scheme("tamil_pudhalvan"), tn + ("tamil_medium" to "yes")).verdict)
        assertEquals(Verdict.NotEligible, engine.evaluate(Curated.scheme("tamil_pudhalvan"), tn + ("tamil_medium" to "no")).verdict)
    }

    @Test fun `check rules never decide, they are listed to confirm`() {
        val r = engine.evaluate(Curated.scheme("csss"), mapOf("stage" to "ug", "income" to "upto_1l"))
        assertEquals(Verdict.Eligible, r.verdict)
        assertEquals(2, r.toConfirm.size)
    }

    @Test fun `unknown answers keep the scheme as maybe and say what is missing`() {
        val r = engine.evaluate(Curated.scheme("pms_sc"), mapOf("category" to "sc"))
        assertEquals(Verdict.Maybe, r.verdict)
        assertTrue(r.missing.any { it.fact == "income" })
        assertNull(r.failed.firstOrNull())
    }
}
