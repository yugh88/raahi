package org.raahi.core.engine

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AnswerSearchTest {
    private val search = AnswerSearch.from(Curated.schemes, listOf(Curated.electricity))

    @Test fun `hindi question about ohm's law finds the ohm explanation`() {
        assertEquals("trap:ohm_rearrange", search.search("ओम का नियम क्या है?")?.entry?.id)
    }

    @Test fun `english question about kwh finds the energy explanation`() {
        assertEquals("trap:kwh_unit", search.search("what is kWh on my electricity bill")?.entry?.id)
    }

    @Test fun `scheme by name finds the scheme record with its source`() {
        val hit = search.search("मेधावी विद्यार्थी योजना")!!
        assertEquals("scheme:mmvy", hit.entry.id)
        assertEquals(Curated.scheme("mmvy").sourceUrl, hit.entry.sourceUrl)
    }

    @Test fun `unrelated question gets no answer instead of a guess`() {
        assertNull(search.search("आज मौसम कैसा है"))
        assertNull(search.search("क्या है"))
    }
}
