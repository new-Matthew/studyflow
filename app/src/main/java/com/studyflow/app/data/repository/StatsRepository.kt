package com.studyflow.app.data.repository

import com.studyflow.app.data.local.dao.AnswerRecordDao
import com.studyflow.app.data.local.dao.DisciplineDao
import com.studyflow.app.data.local.dao.NoteDao
import com.studyflow.app.data.local.dao.QuestionDao
import com.studyflow.app.data.local.dao.SubjectDao
import com.studyflow.app.data.local.relation.AnswerRecordWithQuestion
import com.studyflow.app.data.local.relation.DisciplinePerformance
import com.studyflow.app.data.local.relation.ErrorReasonCount
import com.studyflow.app.data.local.relation.SubjectWithDiscipline
import com.studyflow.app.data.model.LearningStatus
import kotlinx.coroutines.flow.Flow

class StatsRepository(
    disciplineDao: DisciplineDao,
    subjectDao: SubjectDao,
    questionDao: QuestionDao,
    answerRecordDao: AnswerRecordDao,
    noteDao: NoteDao
) {
    val disciplineCount: Flow<Int> = disciplineDao.observeCount()
    val subjectCount: Flow<Int> = subjectDao.observeCount()
    val questionCount: Flow<Int> = questionDao.observeCount()
    val noteCount: Flow<Int> = noteDao.observeCount()

    val answeredCount: Flow<Int> = answerRecordDao.observeCount()
    val correctCount: Flow<Int> = answerRecordDao.observeCorrectCount()
    val answeredQuestionCount: Flow<Int> = answerRecordDao.observeAnsweredQuestionCount()

    val subjectsToReview: Flow<List<SubjectWithDiscipline>> =
        subjectDao.observeByStatus(LearningStatus.REVIEW)

    val recentAnswers: Flow<List<AnswerRecordWithQuestion>> =
        answerRecordDao.observeRecent(5)

    val errorReasons: Flow<List<ErrorReasonCount>> = answerRecordDao.observeErrorReasons()
    val performanceByDiscipline: Flow<List<DisciplinePerformance>> =
        answerRecordDao.observePerformanceByDiscipline()
}
