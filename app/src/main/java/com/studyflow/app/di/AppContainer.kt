package com.studyflow.app.di

import android.content.Context
import com.studyflow.app.data.local.StudyFlowDatabase
import com.studyflow.app.data.repository.DisciplineRepository
import com.studyflow.app.data.repository.KnowledgeRepository
import com.studyflow.app.data.repository.NotebookRepository
import com.studyflow.app.data.repository.QuestionRepository
import com.studyflow.app.data.repository.StatsRepository
import com.studyflow.app.data.repository.SubjectRepository

class AppContainer(context: Context) {

    private val database = StudyFlowDatabase.getInstance(context)
    private val disciplineDao = database.disciplineDao()
    private val subjectDao = database.subjectDao()
    private val contentDao = database.contentDao()
    private val tipDao = database.tipDao()
    private val materialDao = database.materialDao()
    private val questionDao = database.questionDao()
    private val answerRecordDao = database.answerRecordDao()
    private val noteDao = database.noteDao()

    val disciplineRepository = DisciplineRepository(disciplineDao)
    val subjectRepository = SubjectRepository(subjectDao)
    val knowledgeRepository = KnowledgeRepository(contentDao, tipDao, materialDao)
    val questionRepository = QuestionRepository(questionDao, answerRecordDao)
    val notebookRepository = NotebookRepository(noteDao)
    val statsRepository = StatsRepository(
        disciplineDao,
        subjectDao,
        questionDao,
        answerRecordDao,
        noteDao
    )
}
