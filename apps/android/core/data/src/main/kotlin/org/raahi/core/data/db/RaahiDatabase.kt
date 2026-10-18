package org.raahi.core.data.db

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

/** The one learner on this phone (shared-phone profiles come with sync, Phase 4). */
@Entity(tableName = "profile")
data class ProfileEntity(
    @PrimaryKey val id: Int = 1,
    val name: String,
    val languageTag: String,
    val persona: String,
    val interests: String,
    val goal: String?,
    val readingLevel: String,
    val createdAt: Long,
)

/** Every answer to a lesson or trap-test question. */
@Entity(tableName = "answer")
data class AnswerEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val questionId: String,
    val picked: Int,
    val correct: Boolean,
    val trapId: String?,
    val atMillis: Long,
)

/** A trap Didi found, and its spaced re-check box once cleared. */
@Entity(tableName = "trap_state")
data class TrapStateEntity(
    @PrimaryKey val trapId: String,
    val foundAt: Long,
    val clearedAt: Long?,
    val box: Int,
    val dueAt: Long?,
)

/**
 * Scheme-check answers. Income and category are bands, kept on this phone only: this table is
 * never read by sync (there is no outbox entry for it).
 */
@Entity(tableName = "scheme_answer")
data class SchemeAnswerEntity(
    @PrimaryKey val factId: String,
    /** null = "don't know". */
    val optionId: String?,
    val answeredAt: Long,
)

@Entity(tableName = "application")
data class ApplicationEntity(
    @PrimaryKey val schemeId: String,
    /** "guide_opened" or "applied" — set only by the student. */
    val status: String,
    val updatedAt: Long,
)

@Entity(tableName = "document_status")
data class DocumentStatusEntity(
    @PrimaryKey val docId: String,
    /** "have" or "missing" — the student's own answer; no document is stored. */
    val status: String,
    val updatedAt: Long,
)

@Dao
interface LearnerDao {
    @Query("SELECT * FROM profile WHERE id = 1")
    fun profile(): Flow<ProfileEntity?>

    @Upsert
    suspend fun saveProfile(p: ProfileEntity)

    @Insert
    suspend fun addAnswer(a: AnswerEntity)

    @Query("SELECT * FROM answer ORDER BY atMillis")
    fun answers(): Flow<List<AnswerEntity>>

    @Query("SELECT * FROM answer ORDER BY atMillis")
    suspend fun answersNow(): List<AnswerEntity>

    @Query("SELECT * FROM trap_state")
    fun traps(): Flow<List<TrapStateEntity>>

    @Query("SELECT * FROM trap_state")
    suspend fun trapsNow(): List<TrapStateEntity>

    @Query("SELECT * FROM trap_state WHERE trapId = :id")
    suspend fun trap(id: String): TrapStateEntity?

    @Upsert
    suspend fun saveTrap(t: TrapStateEntity)
}

@Dao
interface SchemeDao {
    @Query("SELECT * FROM scheme_answer")
    fun answers(): Flow<List<SchemeAnswerEntity>>

    @Query("SELECT * FROM scheme_answer ORDER BY answeredAt")
    suspend fun answersNow(): List<SchemeAnswerEntity>

    @Upsert
    suspend fun saveAnswer(a: SchemeAnswerEntity)

    @Query("DELETE FROM scheme_answer")
    suspend fun clearAnswers()

    @Query("SELECT * FROM application")
    fun applications(): Flow<List<ApplicationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveApplication(a: ApplicationEntity)

    @Query("SELECT * FROM document_status")
    fun documents(): Flow<List<DocumentStatusEntity>>

    @Upsert
    suspend fun saveDocument(d: DocumentStatusEntity)
}

@Database(
    entities = [
        ProfileEntity::class, AnswerEntity::class, TrapStateEntity::class,
        SchemeAnswerEntity::class, ApplicationEntity::class, DocumentStatusEntity::class,
    ],
    version = 1,
    exportSchema = false,
)
abstract class RaahiDatabase : RoomDatabase() {
    abstract fun learner(): LearnerDao
    abstract fun schemes(): SchemeDao
}
