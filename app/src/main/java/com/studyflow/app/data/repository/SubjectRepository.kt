package com.studyflow.app.data.repository

import com.studyflow.app.data.local.dao.SubjectDao
import com.studyflow.app.data.local.entity.SubjectEntity
import com.studyflow.app.data.local.relation.SubjectWithCounts
import com.studyflow.app.data.local.relation.SubjectWithDiscipline
import com.studyflow.app.data.model.LearningStatus
import kotlinx.coroutines.flow.Flow

class SubjectRepository(private val subjectDao: SubjectDao) {

    val count: Flow<Int> = subjectDao.observeCount()
    val allSubjects: Flow<List<SubjectEntity>> = subjectDao.observeAll()
    val allWithDiscipline: Flow<List<SubjectWithDiscipline>> = subjectDao.observeAllWithDiscipline()

    fun observeByDiscipline(disciplineId: Long): Flow<List<SubjectWithCounts>> =
        subjectDao.observeByDiscipline(disciplineId)

    fun observeAllByDiscipline(disciplineId: Long): Flow<List<SubjectEntity>> =
        subjectDao.observeAllByDiscipline(disciplineId)

    fun observeById(id: Long): Flow<SubjectEntity?> = subjectDao.observeById(id)

    suspend fun getById(id: Long): SubjectEntity? = subjectDao.getById(id)

    fun observeByStatus(status: LearningStatus): Flow<List<SubjectWithDiscipline>> =
        subjectDao.observeByStatus(status)

    suspend fun updateStatus(id: Long, status: LearningStatus) =
        subjectDao.updateStatus(id, status)

    suspend fun updateProgress(id: Long, progress: Int) =
        subjectDao.updateProgress(id, progress.coerceIn(0, 100))

    suspend fun save(subject: SubjectEntity) {
        if (subject.id == 0L) {
            subjectDao.insert(subject)
        } else {
            subjectDao.update(subject)
        }
    }

    suspend fun delete(subject: SubjectEntity) = subjectDao.delete(subject)
}
