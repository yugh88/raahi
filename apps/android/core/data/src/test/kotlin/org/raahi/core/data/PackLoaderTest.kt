package org.raahi.core.data

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import android.content.Context
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

/** The bundled pack loads, and every file matches the hash in its manifest. */
@RunWith(AndroidJUnit4::class)
class PackLoaderTest {
    private val context: Context = ApplicationProvider.getApplicationContext()

    @Test fun `bundled pack loads with verified hashes`() {
        val content = PackLoader(context.assets).load()
        assertEquals("core-hi-v1", content.packId)
        assertEquals(12, content.lesson.questions.size)
        assertTrue(content.schemes.schemes.size >= 10)
    }

    @Test(expected = PackIntegrityException::class)
    fun `a pack id that is not bundled is refused`() {
        PackLoader(context.assets, packId = "missing").load()
    }
}
