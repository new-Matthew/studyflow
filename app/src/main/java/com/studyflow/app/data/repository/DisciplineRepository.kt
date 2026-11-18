package com.studyflow.app.data.repository

import com.studyflow.app.data.local.dao.DisciplineDao
import com.studyflow.app.data.local.entity.DisciplineEntity
import kotlinx.coroutines.flow.Flow

class DisciplineRepository(private val disciplineDao: DisciplineDao) {

    val disciplinesWithCounts = disciplineDao.observeAllWithCounts()
    val disciplines: Flow<List<DisciplineEntity>> = disciplineDao.observeAll()
    val count: Flow<Int> = disciplineDao.observeCount()

    fun observeById(id: Long): Flow<DisciplineEntity?> = disciplineDao.observeById(id)

    suspend fun getById(id: Long): DisciplineEntity? = disciplineDao.getById(id)

    suspend fun save(discipline: DisciplineEntity) {
        if (discipline.id == 0L) {
            disciplineDao.insert(discipline)
        } else {
            disciplineDao.update(discipline)
        }
    }

    suspend fun delete(discipline: DisciplineEntity) = disciplineDao.delete(discipline)
}
