package com.studyflow.app.data.repository

import com.studyflow.app.data.local.dao.AnswerRecordDao
import com.studyflow.app.data.local.dao.QuestionDao
import com.studyflow.app.data.local.entity.AnswerRecordEntity
import com.studyflow.app.data.local.entity.QuestionEntity
import com.studyflow.app.data.local.relation.AnswerRecordWithQuestion
import com.studyflow.app.data.local.relation.DisciplinePerformance
import com.studyflow.app.data.local.relation.ErrorReasonCount
import com.studyflow.app.data.local.relation.QuestionWithContext
import com.studyflow.app.data.model.Difficulty
import kotlinx.coroutines.flow.Flow

class QuestionRepository(
    private val questionDao: QuestionDao,
    private val answerRecordDao: AnswerRecordDao
) {

    val count: Flow<Int> = questionDao.observeCount()
    val allQuestions: Flow<List<QuestionEntity>> = questionDao.observeAll()

    val answeredCount: Flow<Int> = answerRecordDao.observeCount()
    val correctCount: Flow<Int> = answerRecordDao.observeCorrectCount()
    val answeredQuestionCount: Flow<Int> = answerRecordDao.observeAnsweredQuestionCount()
    val errorReasons: Flow<List<ErrorReasonCount>> = answerRecordDao.observeErrorReasons()
    val performanceByDiscipline: Flow<List<DisciplinePerformance>> =
        answerRecordDao.observePerformanceByDiscipline()

    fun observeAllWithContext(): Flow<List<QuestionWithContext>> =
        questionDao.observeAllWithContext()

    fun observeFiltered(
        disciplineId: Long?,
        subjectId: Long?,
        difficulty: Difficulty?
    ): Flow<List<QuestionWithContext>> =
        questionDao.observeFiltered(disciplineId, subjectId, difficulty?.name)

    fun observeQuestion(id: Long): Flow<QuestionEntity?> = questionDao.observeById(id)

    suspend fun getQuestion(id: Long): QuestionEntity? = questionDao.getById(id)

    fun observeRecentAnswers(limit: Int): Flow<List<AnswerRecordWithQuestion>> =
        answerRecordDao.observeRecent(limit)

    fun observeAnswersForQuestion(questionId: Long): Flow<List<AnswerRecordWithQuestion>> =
        answerRecordDao.observeByQuestion(questionId)

    fun observeAllAnswers(): Flow<List<AnswerRecordWithQuestion>> =
        answerRecordDao.observeAllWithQuestion()

    suspend fun save(question: QuestionEntity) {
        if (question.id == 0L) questionDao.insert(question) else questionDao.update(question)
    }

    suspend fun delete(question: QuestionEntity) = questionDao.delete(question)

    suspend fun saveAnswer(record: AnswerRecordEntity) = answerRecordDao.insert(record)
}
