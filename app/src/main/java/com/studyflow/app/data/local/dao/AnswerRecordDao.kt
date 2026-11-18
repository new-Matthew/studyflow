package com.studyflow.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.studyflow.app.data.local.entity.AnswerRecordEntity
import com.studyflow.app.data.local.relation.AnswerRecordWithQuestion
import com.studyflow.app.data.local.relation.DisciplinePerformance
import com.studyflow.app.data.local.relation.ErrorReasonCount
import kotlinx.coroutines.flow.Flow

@Dao
interface AnswerRecordDao {

    @Insert
    suspend fun insert(record: AnswerRecordEntity): Long

    @Query(
        """
        SELECT a.*, q.statement AS statement, q.difficulty AS difficulty, d.name AS disciplineName
        FROM answer_records a
        INNER JOIN questions q ON q.id = a.questionId
        LEFT JOIN disciplines d ON d.id = q.disciplineId
        ORDER BY a.answeredAt DESC
        """
    )
    fun observeAllWithQuestion(): Flow<List<AnswerRecordWithQuestion>>

    @Query(
        """
        SELECT a.*, q.statement AS statement, q.difficulty AS difficulty, d.name AS disciplineName
        FROM answer_records a
        INNER JOIN questions q ON q.id = a.questionId
        LEFT JOIN disciplines d ON d.id = q.disciplineId
        ORDER BY a.answeredAt DESC
        LIMIT :limit
        """
    )
    fun observeRecent(limit: Int): Flow<List<AnswerRecordWithQuestion>>

    @Query(
        """
        SELECT a.*, q.statement AS statement, q.difficulty AS difficulty, d.name AS disciplineName
        FROM answer_records a
        INNER JOIN questions q ON q.id = a.questionId
        LEFT JOIN disciplines d ON d.id = q.disciplineId
        WHERE a.questionId = :questionId
        ORDER BY a.answeredAt DESC
        """
    )
    fun observeByQuestion(questionId: Long): Flow<List<AnswerRecordWithQuestion>>

    @Query("SELECT COUNT(*) FROM answer_records")
    fun observeCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM answer_records WHERE isCorrect = 1")
    fun observeCorrectCount(): Flow<Int>

    @Query(
        """
        SELECT errorReason, COUNT(*) AS total
        FROM answer_records
        WHERE isCorrect = 0 AND errorReason IS NOT NULL
        GROUP BY errorReason
        ORDER BY total DESC
        """
    )
    fun observeErrorReasons(): Flow<List<ErrorReasonCount>>

    @Query("SELECT COUNT(DISTINCT questionId) FROM answer_records")
    fun observeAnsweredQuestionCount(): Flow<Int>

    @Query(
        """
        SELECT d.name AS disciplineName,
               COUNT(*) AS total,
               SUM(CASE WHEN a.isCorrect = 1 THEN 1 ELSE 0 END) AS correct
        FROM answer_records a
        INNER JOIN questions q ON q.id = a.questionId
        LEFT JOIN disciplines d ON d.id = q.disciplineId
        GROUP BY q.disciplineId
        ORDER BY total DESC
        """
    )
    fun observePerformanceByDiscipline(): Flow<List<DisciplinePerformance>>
}
