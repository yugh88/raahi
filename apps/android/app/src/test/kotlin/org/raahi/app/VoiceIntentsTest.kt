package org.raahi.app

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/** Tile labels are the real Hindi labels from data/curated/scheme-facts.json. */
class VoiceIntentsTest {
    private val states = listOf("मध्य प्रदेश", "तमिलनाडु", "कर्नाटक", "केरल", "कोई और राज्य")
    private val stages = listOf("कक्षा 8", "कक्षा 9 या 10", "कक्षा 11 या 12", "ITI कोर्स", "डिप्लोमा (पॉलिटेक्निक)", "कॉलेज डिग्री (BA, BSc, BTech…)", "पोस्ट-ग्रेजुएशन")

    @Test fun `tile words pick the tile`() {
        assertEquals(0, VoiceIntents.match("मध्य प्रदेश", states))
        assertEquals(5, VoiceIntents.match("कॉलेज डिग्री", stages))
    }

    @Test fun `ordinals pick by position`() {
        assertEquals(1, VoiceIntents.match("दूसरा", states))
        assertEquals(0, VoiceIntents.match("पहला", states))
    }

    @Test fun `don't know is its own answer`() {
        assertEquals(VoiceIntents.DONT_KNOW, VoiceIntents.match("पता नहीं", states))
    }

    @Test fun `ambiguous or unrelated speech matches nothing, so the tiles stay`() {
        assertNull(VoiceIntents.match("कक्षा", stages)) // in several tiles
        assertNull(VoiceIntents.match("बारिश", states))
    }

    @Test fun `grammar has every tile and the ordinals for this many tiles only`() {
        val g = VoiceIntents.grammar(states)
        assertEquals(true, "मध्य प्रदेश" in g && "पांच" in g && "छठा" !in g)
    }
}
