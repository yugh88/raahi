package org.raahi.core.data

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import androidx.room.Room
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory
import org.raahi.core.data.db.AnswerEntity
import org.raahi.core.data.db.ApplicationEntity
import org.raahi.core.data.db.DocumentStatusEntity
import org.raahi.core.data.db.ProfileEntity
import org.raahi.core.data.db.RaahiDatabase
import org.raahi.core.data.db.SchemeAnswerEntity
import org.raahi.core.data.db.TrapStateEntity
import org.raahi.core.engine.Answer
import org.raahi.core.engine.AnswerSearch
import org.raahi.core.engine.EligibilityEngine
import org.raahi.core.engine.SchemeQuestionSelector
import org.raahi.core.engine.SpacedRepetition
import org.raahi.core.engine.TrapEngine
import org.raahi.core.model.Question
import org.raahi.core.security.DatabaseKey
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    @Provides
    @Singleton
    fun database(@ApplicationContext context: Context): RaahiDatabase {
        System.loadLibrary("sqlcipher")
        return Room.databaseBuilder(context, RaahiDatabase::class.java, "raahi.db")
            .openHelperFactory(SupportOpenHelperFactory(DatabaseKey.sqlcipherRawKey(context)))
            .build()
    }
}

/** The bundled pack and the engines built on it. Loaded once, off the main thread by callers. */
@Singleton
class ContentRepository @Inject constructor(@ApplicationContext context: Context) {
    val content: Content by lazy { PackLoader(context.assets).load() }
    val trapEngine by lazy { TrapEngine(content.lesson) }
    val eligibility by lazy { EligibilityEngine(content.facts.facts) }
    val selector by lazy { SchemeQuestionSelector(eligibility, content.facts.facts, content.schemes.schemes) }
    val answerSearch by lazy { AnswerSearch.from(content.schemes, listOf(content.lesson)) }
}

data class Progress(val diyas: Int, val streakDays: Int, val answered: Int)

@Singleton
class LearnerRepository @Inject constructor(
    private val db: RaahiDatabase,
    private val content: ContentRepository,
) {
    private val dao get() = db.learner()

    val profile: Flow<ProfileEntity?> get() = dao.profile()
    val traps: Flow<List<TrapStateEntity>> get() = dao.traps()
    val answers: Flow<List<Answer>> get() = dao.answers().map { list -> list.map { Answer(it.questionId, it.picked, it.atMillis) } }

    val progress: Flow<Progress> get() = dao.answers().map { list ->
        Progress(diyas = list.count { it.correct } * DIYAS_PER_RIGHT_ANSWER, streakDays = streak(list.map { it.atMillis }), answered = list.size)
    }

    suspend fun saveProfile(p: ProfileEntity) = dao.saveProfile(p)

    suspend fun answersNow(): List<Answer> = dao.answersNow().map { Answer(it.questionId, it.picked, it.atMillis) }

    /**
     * Saves an answer. A wrong option tagged with a trap marks that trap as found; two right
     * answers in a row on a found trap clear it and schedule the first spaced re-check.
     * Returns the trap the answer cleared, if any.
     */
    suspend fun answer(q: Question, picked: Int, now: Long = System.currentTimeMillis()): String? {
        val option = q.options[picked]
        dao.addAnswer(AnswerEntity(questionId = q.id, picked = picked, correct = option.correct, trapId = option.trap, atMillis = now))
        option.trap?.let { trap ->
            val existing = dao.trap(trap)
            dao.saveTrap(TrapStateEntity(trap, foundAt = existing?.foundAt ?: now, clearedAt = null, box = 0, dueAt = null))
        }
        if (!option.correct) return null
        val all = dao.answersNow().map { Answer(it.questionId, it.picked, it.atMillis) }
        val engine = content.trapEngine
        val trapsOfQuestion = q.options.mapNotNull { it.trap }.toSet()
        for (trap in trapsOfQuestion) {
            val state = dao.trap(trap) ?: continue
            if (state.clearedAt == null && engine.isCleared(trap, all)) {
                dao.saveTrap(state.copy(clearedAt = now, box = 0, dueAt = SpacedRepetition.dueAt(0, now)))
                return trap
            }
        }
        return null
    }

    private fun streak(times: List<Long>): Int {
        val days = times.map { dayIndex(it) }.toSet()
        var d = dayIndex(System.currentTimeMillis())
        if (d !in days) d -= 1 // today not started yet: count up to yesterday
        var n = 0
        while (d in days) { n++; d-- }
        return n
    }

    private fun dayIndex(t: Long): Long {
        val c = Calendar.getInstance().apply { timeInMillis = t }
        return c.get(Calendar.YEAR) * 400L + c.get(Calendar.DAY_OF_YEAR)
    }

    companion object {
        const val DIYAS_PER_RIGHT_ANSWER = 10
    }
}

@Singleton
class SchemeRepository @Inject constructor(private val db: RaahiDatabase) {
    private val dao get() = db.schemes()

    /** factId → optionId; "don't know" answers are kept as asked but have no option. */
    val answers: Flow<List<SchemeAnswerEntity>> get() = dao.answers()
    val applications: Flow<List<ApplicationEntity>> get() = dao.applications()
    val documents: Flow<List<DocumentStatusEntity>> get() = dao.documents()

    suspend fun answersOnce(): List<Pair<String, String?>> = dao.answersNow().map { it.factId to it.optionId }

    suspend fun answer(factId: String, optionId: String?) = dao.saveAnswer(SchemeAnswerEntity(factId, optionId, System.currentTimeMillis()))
    suspend fun restart() = dao.clearAnswers()
    suspend fun setApplication(schemeId: String, status: String) = dao.saveApplication(ApplicationEntity(schemeId, status, System.currentTimeMillis()))
    suspend fun setDocument(docId: String, have: Boolean) = dao.saveDocument(DocumentStatusEntity(docId, if (have) "have" else "missing", System.currentTimeMillis()))
}
