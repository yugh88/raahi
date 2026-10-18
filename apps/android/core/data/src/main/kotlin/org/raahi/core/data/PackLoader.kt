package org.raahi.core.data

import android.content.res.AssetManager
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.raahi.core.model.Lesson
import org.raahi.core.model.SchemeCatalog
import org.raahi.core.model.SchemeFacts
import java.security.MessageDigest

@Serializable
data class PackManifest(val id: String, val version: Int, val title: String, val files: List<PackFile>)

@Serializable
data class PackFile(val path: String, val sha256: String, val bytes: Long)

/** Everything the app teaches and checks, loaded from the bundled pack. */
data class Content(val lesson: Lesson, val schemes: SchemeCatalog, val facts: SchemeFacts, val packId: String)

class PackIntegrityException(message: String) : Exception(message)

/** Reads a pack from assets/packs/<id>, refusing any file whose SHA-256 doesn't match the manifest. */
class PackLoader(private val assets: AssetManager, private val packId: String = "core-hi-v1") {
    private val json = Json { ignoreUnknownKeys = true }

    fun load(): Content {
        val manifest = json.decodeFromString<PackManifest>(read("manifest.json").decodeToString())
        fun file(name: String): String {
            val entry = manifest.files.firstOrNull { it.path == name } ?: throw PackIntegrityException("$name not in manifest")
            val bytes = read(name)
            val hash = MessageDigest.getInstance("SHA-256").digest(bytes).joinToString("") { "%02x".format(it) }
            if (hash != entry.sha256) throw PackIntegrityException("$name hash mismatch")
            return bytes.decodeToString()
        }
        return Content(
            lesson = json.decodeFromString(file("lesson-electricity.json")),
            schemes = json.decodeFromString(file("schemes.json")),
            facts = json.decodeFromString(file("scheme-facts.json")),
            packId = manifest.id,
        )
    }

    private fun read(name: String): ByteArray =
        try {
            assets.open("packs/$packId/$name").use { it.readBytes() }
        } catch (e: java.io.IOException) {
            throw PackIntegrityException("pack $packId: cannot read $name")
        }
}
