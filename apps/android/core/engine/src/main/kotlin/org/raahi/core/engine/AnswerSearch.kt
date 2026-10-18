package org.raahi.core.engine

import org.raahi.core.model.AppLanguage
import org.raahi.core.model.Lesson
import org.raahi.core.model.Localized
import org.raahi.core.model.SchemeCatalog
import kotlin.math.ln

/** An answer Didi can give offline. Every entry comes from curated data and carries its source. */
data class AnswerEntry(
    val id: String,
    val title: Localized,
    val lines: List<Localized>,
    val sourceTitle: String,
    val sourceUrl: String,
)

data class AnswerHit(val entry: AnswerEntry, val score: Double)

/**
 * Offline Ask Didi: matches the student's words against the lesson's explanations and the
 * scheme records. If nothing matches well enough it returns null and Didi says she doesn't
 * know yet — she never answers without a source.
 */
class AnswerSearch(private val entries: List<AnswerEntry>) {
    private val docs = entries.map { e -> e to tokens(listOf(e.title) + e.lines) }
    private val idf: Map<String, Double> = docs.flatMap { it.second.toSet() }.groupingBy { it }.eachCount()
        .mapValues { (_, df) -> ln((docs.size + 1.0) / df) }

    fun search(query: String): AnswerHit? {
        val q = normalize(query).filter { it !in STOPWORDS }.toSet()
        if (q.isEmpty()) return null
        // "How do I apply / which documents" questions prefer a scheme's steps over its rules.
        val applyIntent = q.any { it in APPLY_WORDS }
        return docs.map { (entry, words) ->
            val set = words.toSet()
            val score = q.sumOf { w -> if (w in set || set.any { it.length >= 4 && w.length >= 4 && (it.startsWith(w) || w.startsWith(it)) }) idf[w] ?: idf.values.max() else 0.0 }
            AnswerHit(entry, score + if (applyIntent && entry.id.endsWith(":apply") && score > 0) APPLY_BONUS else 0.0)
        }.filter { it.score >= MIN_SCORE }.maxByOrNull { it.score }
    }

    companion object {
        private const val MIN_SCORE = 1.2
        private const val APPLY_BONUS = 2.0
        private val APPLY_WORDS = setOf(
            "आवेदन", "अप्लाई", "कागज़", "कागज", "दस्तावेज़", "दस्तावेज", "डॉक्यूमेंट", "फ़ॉर्म", "फॉर्म",
            "apply", "application", "documents", "document", "form",
        )

        fun from(catalog: SchemeCatalog, lessons: List<Lesson>): AnswerSearch {
            val schemeEntries = catalog.schemes.map { s ->
                AnswerEntry(
                    id = "scheme:${s.id}",
                    title = s.name,
                    lines = listOf(s.benefit) + s.rules.map { it.reason },
                    sourceTitle = s.sourceTitle,
                    sourceUrl = s.sourceUrl,
                )
            }
            // "How do I apply / which documents" for each scheme, from its official steps.
            val applyEntries = catalog.schemes.map { s ->
                val docs = s.documents.mapNotNull { catalog.documents[it] }
                AnswerEntry(
                    id = "scheme:${s.id}:apply",
                    title = Localized(en = "${s.name.en}: how to apply, documents", hi = "${s.name.hi ?: s.name.en}: आवेदन, कागज़, दस्तावेज़"),
                    lines = s.steps.map { it.text } + if (docs.isEmpty()) emptyList() else listOf(
                        Localized(en = "Documents: " + docs.joinToString(", ") { it.en }, hi = "कागज़: " + docs.joinToString(", ") { it.hi ?: it.en }),
                    ),
                    sourceTitle = s.sourceTitle,
                    sourceUrl = s.sourceUrl,
                )
            }
            val lessonEntries = lessons.flatMap { lesson ->
                lesson.traps.map { t ->
                    val topic = lesson.topics.first { it.id == t.topic }
                    AnswerEntry(
                        id = "trap:${t.id}",
                        title = topic.title,
                        lines = t.explain,
                        sourceTitle = lesson.basedOn,
                        sourceUrl = lesson.sourceUrl,
                    )
                }
            }
            return AnswerSearch(schemeEntries + applyEntries + lessonEntries)
        }

        private fun tokens(texts: List<Localized>): List<String> =
            texts.flatMap { t -> AppLanguage.entries.map { t.get(it) }.distinct().flatMap(::normalize) }.filter { it !in STOPWORDS }

        fun normalize(s: String): List<String> =
            s.lowercase().replace(Regex("[^\\p{L}\\p{M}\\p{N}]+"), " ").split(' ').filter { it.length > 1 }

        private val STOPWORDS = setOf(
            "the", "is", "a", "an", "of", "to", "in", "on", "for", "and", "or", "what", "how", "why", "which", "do", "does",
            "it", "be", "can", "my", "me", "you", "your", "i", "with", "are", "this", "that", "about", "tell", "kya", "hai",
            "ka", "ki", "ke", "kaise", "kyon", "batao", "mujhe",
            "का", "की", "के", "है", "हैं", "क्या", "कैसे", "क्यों", "में", "से", "को", "और", "या", "यह", "वह", "मुझे", "बताओ",
            "बताइए", "दीदी", "हो", "तो", "भी", "पर", "कौन", "कौन-सी", "कितना", "कितनी", "एक", "होता", "होती", "जाता",
        )
    }
}
