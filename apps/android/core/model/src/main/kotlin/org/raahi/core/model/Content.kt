package org.raahi.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Text in the bundled languages. Missing languages fall back to English. */
@Serializable
data class Localized(
    val en: String,
    val hi: String? = null,
    val ta: String? = null,
    val te: String? = null,
    val kn: String? = null,
    val ml: String? = null,
) {
    fun get(language: AppLanguage): String = when (language) {
        AppLanguage.Hindi -> hi
        AppLanguage.Tamil -> ta
        AppLanguage.Telugu -> te
        AppLanguage.Kannada -> kn
        AppLanguage.Malayalam -> ml
    } ?: en
}

// ---- Schemes (data/curated/schemes.json, scheme-facts.json) ----

@Serializable
data class SchemeFacts(val version: Int, val facts: List<FactDef>)

@Serializable
data class FactDef(
    val id: String,
    val question: Localized,
    @SerialName("on_device_only") val onDeviceOnly: Boolean,
    val options: List<FactOption>,
)

/** One answer tile. Number facts (income, marks) carry the band they stand for. */
@Serializable
data class FactOption(
    val id: String,
    val label: Localized,
    val min: Long? = null,
    @SerialName("min_inclusive") val minInclusive: Boolean = true,
    val max: Long? = null,
    @SerialName("max_inclusive") val maxInclusive: Boolean = true,
)

@Serializable
data class SchemeCatalog(
    val version: Int,
    @SerialName("checked_on") val checkedOn: String,
    val schemes: List<Scheme>,
)

@Serializable
data class Scheme(
    val id: String,
    val name: Localized,
    val by: Localized,
    val state: String? = null,
    val benefit: Localized,
    @SerialName("source_url") val sourceUrl: String,
    @SerialName("source_title") val sourceTitle: String,
    @SerialName("apply_url") val applyUrl: String? = null,
    val deadline: String? = null,
    @SerialName("deadline_source") val deadlineSource: String? = null,
    val helpline: String? = null,
    val rules: List<Rule>,
    val steps: List<SchemeStep>,
    val documents: List<String>,
    @SerialName("draft_fields") val draftFields: List<String>,
)

@Serializable
enum class Op {
    @SerialName("in") In,
    @SerialName("not_in") NotIn,
    @SerialName("lte") Lte,
    @SerialName("lt") Lt,
    @SerialName("gte") Gte,

    /** Cannot be decided from answers; always shown as "confirm this". */
    @SerialName("check") Check,
}

@Serializable
data class Condition(val fact: String, val op: Op, val values: List<String> = emptyList(), val value: Long? = null)

@Serializable
data class Rule(
    val fact: String? = null,
    val op: Op,
    val values: List<String> = emptyList(),
    val value: Long? = null,
    @SerialName("only_if") val onlyIf: Condition? = null,
    val reason: Localized,
    val where: String,
)

@Serializable
data class SchemeStep(val text: Localized, val where: String)

// ---- Lessons (data/curated/lesson-*.json) ----

@Serializable
data class Lesson(
    val version: Int,
    val id: String,
    val title: Localized,
    val subject: Localized,
    @SerialName("based_on") val basedOn: String,
    @SerialName("source_url") val sourceUrl: String,
    @SerialName("authored_by") val authoredBy: String,
    @SerialName("checked_on") val checkedOn: String,
    val topics: List<Topic>,
    val traps: List<TrapDef>,
    val questions: List<Question>,
)

@Serializable
data class Topic(val id: String, val title: Localized, val picture: String)

@Serializable
data class TrapDef(
    val id: String,
    val topic: String,
    val name: Localized,
    val misconception: Localized,
    val explain: List<Localized>,
)

@Serializable
data class Question(
    val id: String,
    val topic: String,
    /** Questions in one group test the same idea; a retry uses a different member. */
    val group: String,
    val picture: String,
    val prompt: Localized,
    val options: List<AnswerOption>,
) {
    val correctIndex: Int get() = options.indexOfFirst { it.correct }
}

@Serializable
data class AnswerOption(val text: Localized, val correct: Boolean = false, val trap: String? = null)
