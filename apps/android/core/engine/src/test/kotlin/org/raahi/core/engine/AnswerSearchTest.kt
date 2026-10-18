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

/**
 * Free speech in Ask Didi: these are the exact transcripts vosk-model-small-hi-0.22 produced
 * (no grammar) for questions spoken by the macOS Hindi voice, including its mistakes.
 */
class AnswerSearchFromSpeechTest {
    private val search = AnswerSearch.from(Curated.schemes, listOf(Curated.electricity))
    private fun id(q: String) = search.search(q)?.entry?.id

    @Test fun `ohm heard as unka still finds Ohm's law`() = assertEquals("trap:ohm_rearrange", id("उनका का नियम क्या होता है"))
    @Test fun `electricity bill unit`() = assertEquals("trap:kwh_unit", id("बिजली का बिल यूनिट में क्या मापता है"))
    @Test fun `parallel said as samanantar`() = assertEquals("trap:parallel_total", id("समानांतर में प्रतिरोध जोड़ने पर क्या होता है"))
    @Test fun `marks for MMVY`() = assertEquals("scheme:mmvy", id("मेधावी विद्यार्थी योजना के लिए कितने अंक चाहिए"))
    @Test fun `who gets pragati`() = assertEquals(true, id("प्रगति छात्रवृति किसको मिलती है")?.startsWith("scheme:pragati"))
    @Test fun `bulb heard as world still finds the circuit`() = assertEquals("trap:open_circuit", id("वर्ल्ड कब जलता है"))
    @Test fun `weather is not known`() = assertNull(id("आज मौसम कैसा है"))
}

class AnswerSearchApplyTest {
    private val search = AnswerSearch.from(Curated.schemes, listOf(Curated.electricity))
    private fun id(q: String) = search.search(q)?.entry?.id

    @Test fun `how to apply goes to the steps`() = assertEquals("scheme:mmvy:apply", id("मेधावी विद्यार्थी योजना में आवेदन कैसे करें"))
    @Test fun `documents question goes to the steps and documents`() = assertEquals("scheme:pragati_degree:apply", id("प्रगति छात्रवृत्ति डिग्री के लिए कौन से कागज़ चाहिए"))
    @Test fun `naming the scheme alone still gives the scheme`() = assertEquals("scheme:mmvy", id("मेधावी विद्यार्थी योजना"))
}
