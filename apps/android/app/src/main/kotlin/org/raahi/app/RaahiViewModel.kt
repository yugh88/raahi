package org.raahi.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.raahi.core.data.Content
import org.raahi.core.data.ContentRepository
import org.raahi.core.data.LearnerRepository
import org.raahi.core.data.Progress
import org.raahi.core.data.SchemeRepository
import org.raahi.core.data.db.ProfileEntity
import org.raahi.core.data.db.TrapStateEntity
import org.raahi.core.engine.Answer
import org.raahi.core.engine.AnswerHit
import org.raahi.core.engine.SchemeResult
import org.raahi.core.engine.Verdict
import org.raahi.core.model.AppLanguage
import org.raahi.core.model.FactDef
import org.raahi.core.model.Question
import org.raahi.core.model.Scheme
import org.raahi.core.model.TrapDef
import javax.inject.Inject

/** A trap test in progress (S10): one question per topic, no score. */
data class TrapTestSession(val questions: List<Question>, val index: Int = 0, val picked: Int? = null, val answers: List<Answer> = emptyList())

/** Retrying one trap (S12 → retry → S13). */
data class RetrySession(val trap: TrapDef, val question: Question?, val picked: Int? = null, val clearedNow: Boolean = false)

/** The scheme check (S19 → S20). Answers are option ids; null = "don't know". */
data class SchemeCheck(val asked: List<String> = emptyList(), val answers: Map<String, String?> = emptyMap())

data class AskState(val query: String = "", val hit: AnswerHit? = null, val searched: Boolean = false, val voiceSeconds: Int? = null)

/**
 * One state holder for the demo app: reads the encrypted database and the bundled pack and
 * runs the engines. Screens stay stateless and get plain UI states built from this.
 */
@HiltViewModel
class RaahiViewModel @Inject constructor(
    private val contentRepo: ContentRepository,
    private val learner: LearnerRepository,
    private val schemesRepo: SchemeRepository,
) : ViewModel() {

    private val _content = MutableStateFlow<Content?>(null)
    val content: StateFlow<Content?> = _content.asStateFlow()

    /** Null until loaded; [ProfileState.None] when nobody has onboarded on this phone yet. */
    val profile: StateFlow<ProfileState?> = learner.profile.map { p -> p?.let { ProfileState.Some(it) } ?: ProfileState.None }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val progress: StateFlow<Progress> = learner.progress.stateIn(viewModelScope, SharingStarted.Eagerly, Progress(0, 0, 0))
    val answers: StateFlow<List<Answer>> = learner.answers.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    val traps: StateFlow<List<TrapStateEntity>> = learner.traps.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val documents: StateFlow<Map<String, Boolean>> = schemesRepo.documents.map { l -> l.associate { it.docId to (it.status == "have") } }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyMap())
    val applications: StateFlow<Map<String, String>> = schemesRepo.applications.map { l -> l.associate { it.schemeId to it.status } }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyMap())

    init {
        viewModelScope.launch {
            _content.value = withContext(Dispatchers.IO) { contentRepo.content.also { contentRepo.answerSearch } }
        }
        // Restore the last scheme check from the database (bands stay on this phone).
        viewModelScope.launch {
            val saved = withContext(Dispatchers.IO) { schemesRepo.answersOnce() }
            if (saved.isNotEmpty()) _scheme.value = SchemeCheck(saved.map { it.first }, saved.toMap())
        }
    }

    fun saveProfile(name: String, language: AppLanguage, persona: String, interests: Set<String>, goal: String?) = viewModelScope.launch {
        learner.saveProfile(
            ProfileEntity(
                name = name.trim(), languageTag = language.tag, persona = persona, interests = interests.joinToString(","),
                goal = goal, readingLevel = "Standard", createdAt = System.currentTimeMillis(),
            ),
        )
    }

    fun setLanguage(language: AppLanguage) = viewModelScope.launch {
        (profile.value as? ProfileState.Some)?.p?.let { learner.saveProfile(it.copy(languageTag = language.tag)) }
    }

    // ---- S4 lesson question ----

    private val _lessonPicked = MutableStateFlow<Int?>(null)
    val lessonPicked: StateFlow<Int?> = _lessonPicked.asStateFlow()
    private val _lessonQuestion = MutableStateFlow<Question?>(null)
    val lessonQuestion: StateFlow<Question?> = _lessonQuestion.asStateFlow()

    /** Today's lesson: the first question not yet answered right, in pack order. */
    fun openLesson() {
        val c = _content.value ?: return
        val rightOnce = answers.value.filter { contentRepo.trapEngine.isCorrect(it) }.map { it.questionId }.toSet()
        _lessonQuestion.value = c.lesson.questions.firstOrNull { it.id !in rightOnce } ?: c.lesson.questions.first()
        _lessonPicked.value = null
    }

    fun pickLesson(i: Int) {
        val q = _lessonQuestion.value ?: return
        if (_lessonPicked.value == q.correctIndex) return
        _lessonPicked.value = i
        viewModelScope.launch { learner.answer(q, i) }
    }

    // ---- S10 trap test → S11 summary ----

    private val _trapTest = MutableStateFlow<TrapTestSession?>(null)
    val trapTest: StateFlow<TrapTestSession?> = _trapTest.asStateFlow()

    fun startTrapTest() {
        val seen = answers.value.map { it.questionId }.toSet()
        _trapTest.value = TrapTestSession(contentRepo.trapEngine.trapTest(seen))
    }

    /** Records the answer; returns true when the test is finished. */
    fun pickTrapTest(i: Int): Boolean {
        val s = _trapTest.value ?: return true
        val q = s.questions[s.index]
        val a = Answer(q.id, i, System.currentTimeMillis())
        viewModelScope.launch { learner.answer(q, i, a.atMillis) }
        val next = s.copy(answers = s.answers + a, picked = null, index = s.index + 1)
        _trapTest.value = next
        return next.index >= s.questions.size
    }

    /** Traps found in the last trap test, most frequent first. */
    fun foundTraps(): List<TrapDef> {
        val c = _content.value ?: return emptyList()
        val s = _trapTest.value ?: return emptyList()
        return contentRepo.trapEngine.trapsHit(s.answers).mapNotNull { id -> c.lesson.traps.firstOrNull { it.id == id } }
    }

    // ---- S12 explanation → retry → S13 cleared ----

    private val _retry = MutableStateFlow<RetrySession?>(null)
    val retry: StateFlow<RetrySession?> = _retry.asStateFlow()

    fun focusTrap(trap: TrapDef) {
        _retry.value = RetrySession(trap, contentRepo.trapEngine.retryQuestion(trap.id, answers.value))
    }

    /** Returns true when this answer cleared the trap. */
    fun pickRetry(i: Int, onResult: (cleared: Boolean, correct: Boolean) -> Unit) {
        val s = _retry.value ?: return
        val q = s.question ?: return
        _retry.update { it?.copy(picked = i) }
        viewModelScope.launch {
            val cleared = learner.answer(q, i) == s.trap.id
            val correct = q.options[i].correct
            if (!cleared && correct) {
                // One more right answer needed: next question for the same trap.
                val nextQ = contentRepo.trapEngine.retryQuestion(s.trap.id, learner.answersNow())
                _retry.value = RetrySession(s.trap, nextQ)
            } else if (cleared) {
                _retry.update { it?.copy(clearedNow = true) }
            }
            onResult(cleared, correct)
        }
    }

    // ---- S19 scheme check → S20 verdict ----

    private val _scheme = MutableStateFlow(SchemeCheck())
    val scheme: StateFlow<SchemeCheck> = _scheme.asStateFlow()

    fun restartSchemeCheck() {
        _scheme.value = SchemeCheck()
        viewModelScope.launch { schemesRepo.restart() }
    }

    fun nextSchemeQuestion(): FactDef? {
        val s = _scheme.value
        return contentRepo.selector.next(known(s), s.asked.toSet())
    }

    fun answerScheme(fact: FactDef, optionIndex: Int?) {
        val option = optionIndex?.let { fact.options[it].id }
        _scheme.update { it.copy(asked = it.asked + fact.id, answers = it.answers + (fact.id to option)) }
        viewModelScope.launch { schemesRepo.answer(fact.id, option) }
    }

    fun results(): List<SchemeResult> {
        val c = _content.value ?: return emptyList()
        val known = known(_scheme.value)
        return c.schemes.schemes.map { contentRepo.eligibility.evaluate(it, known) }
            .sortedBy { it.verdict.ordinal }
    }

    fun hasSchemeResults(): Boolean = _scheme.value.asked.isNotEmpty()

    private fun known(s: SchemeCheck): Map<String, String> = s.answers.mapNotNull { (k, v) -> v?.let { k to it } }.toMap()

    // ---- S21 enrollment guide, S18 documents ----

    private val _guide = MutableStateFlow<Pair<Scheme, Int>?>(null)
    val guide: StateFlow<Pair<Scheme, Int>?> = _guide.asStateFlow()

    fun openGuide(scheme: Scheme) {
        _guide.value = scheme to 0
        viewModelScope.launch { if (applications.value[scheme.id] == null) schemesRepo.setApplication(scheme.id, "guide_opened") }
    }

    fun guideStep(i: Int) = _guide.update { g -> g?.let { it.first to i.coerceIn(0, it.first.steps.lastIndex) } }

    fun markApplied() {
        val s = _guide.value?.first ?: return
        viewModelScope.launch { schemesRepo.setApplication(s.id, "applied") }
    }

    fun toggleDocument(docId: String) {
        val have = documents.value[docId] == true
        viewModelScope.launch { schemesRepo.setDocument(docId, !have) }
    }

    /** Documents needed by the schemes the student may get, in a stable order. */
    fun neededDocuments(): List<String> =
        results().filter { it.verdict != Verdict.NotEligible }.flatMap { it.scheme.documents }.distinct()
            .ifEmpty { _content.value?.schemes?.schemes?.flatMap { it.documents }?.distinct().orEmpty() }

    // ---- S5 Ask Didi (offline answer bank) ----

    private val _ask = MutableStateFlow(AskState())
    val ask: StateFlow<AskState> = _ask.asStateFlow()

    fun askDidi(query: String, voiceSeconds: Int? = null) {
        _ask.value = AskState(query, contentRepo.answerSearch.search(query), searched = true, voiceSeconds = voiceSeconds)
    }

    val trapsCleared: StateFlow<Int> = combine(traps, answers) { t, _ -> t.count { it.clearedAt != null } }
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0)
}

sealed interface ProfileState {
    data object None : ProfileState
    data class Some(val p: ProfileEntity) : ProfileState
}
