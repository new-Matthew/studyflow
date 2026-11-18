package com.studyflow.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.studyflow.app.data.local.entity.QuestionEntity
import com.studyflow.app.data.local.relation.QuestionWithContext
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestionDao {

    @Query(
        """
        SELECT q.*,
               d.name AS disciplineName,
               s.name AS subjectName,
               (SELECT COUNT(*) FROM answer_records a WHERE a.questionId = q.id) AS answerCount,
               (SELECT COUNT(*) FROM answer_records a WHERE a.questionId = q.id AND a.isCorrect = 1) AS correctCount
        FROM questions q
        LEFT JOIN disciplines d ON d.id = q.disciplineId
        LEFT JOIN subjects s ON s.id = q.subjectId
        ORDER BY q.createdAt DESC
        """
    )
    fun observeAllWithContext(): Flow<List<QuestionWithContext>>

    @Query(
        """
        SELECT q.*,
               d.name AS disciplineName,
               s.name AS subjectName,
               (SELECT COUNT(*) FROM answer_records a WHERE a.questionId = q.id) AS answerCount,
               (SELECT COUNT(*) FROM answer_records a WHERE a.questionId = q.id AND a.isCorrect = 1) AS correctCount
        FROM questions q
        LEFT JOIN disciplines d ON d.id = q.disciplineId
        LEFT JOIN subjects s ON s.id = q.subjectId
        WHERE (:disciplineId IS NULL OR q.disciplineId = :disciplineId)
          AND (:subjectId IS NULL OR q.subjectId = :subjectId)
          AND (:difficulty IS NULL OR q.difficulty = :difficulty)
        ORDER BY q.createdAt DESC
        """
    )
    fun observeFiltered(
        disciplineId: Long?,
        subjectId: Long?,
        difficulty: String?
    ): Flow<List<QuestionWithContext>>

    @Query("SELECT * FROM questions WHERE id = :id")
    fun observeById(id: Long): Flow<QuestionEntity?>

    @Query("SELECT * FROM questions WHERE id = :id")
    suspend fun getById(id: Long): QuestionEntity?

    @Query("SELECT COUNT(*) FROM questions")
    fun observeCount(): Flow<Int>

    @Query("SELECT * FROM questions ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<QuestionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(question: QuestionEntity): Long

    @Update
    suspend fun update(question: QuestionEntity)

    @Delete
    suspend fun delete(question: QuestionEntity)
}
