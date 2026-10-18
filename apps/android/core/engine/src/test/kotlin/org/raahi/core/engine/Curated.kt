package org.raahi.core.engine

import kotlinx.serialization.json.Json
import org.raahi.core.model.Lesson
import org.raahi.core.model.SchemeCatalog
import org.raahi.core.model.SchemeFacts
import java.io.File

/** The real curated files from data/curated. Tests never use made-up records. */
object Curated {
    private val dir = File(System.getProperty("raahi.curated") ?: "../../../../data/curated")
    private val json = Json { ignoreUnknownKeys = true }

    val facts: SchemeFacts by lazy { json.decodeFromString(File(dir, "scheme-facts.json").readText()) }
    val schemes: SchemeCatalog by lazy { json.decodeFromString(File(dir, "schemes.json").readText()) }
    val electricity: Lesson by lazy { json.decodeFromString(File(dir, "lesson-electricity.json").readText()) }

    fun scheme(id: String) = schemes.schemes.first { it.id == id }
}
