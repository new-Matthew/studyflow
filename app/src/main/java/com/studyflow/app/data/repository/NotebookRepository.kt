package com.studyflow.app.data.repository

import com.studyflow.app.data.local.dao.NoteDao
import com.studyflow.app.data.local.entity.NoteEntity
import com.studyflow.app.data.local.relation.NoteWithSubject
import kotlinx.coroutines.flow.Flow

class NotebookRepository(private val noteDao: NoteDao) {

    val notes: Flow<List<NoteWithSubject>> = noteDao.observeAllWithSubject()
    val count: Flow<Int> = noteDao.observeCount()

    fun observeById(id: Long): Flow<NoteEntity?> = noteDao.observeById(id)

    suspend fun getById(id: Long): NoteEntity? = noteDao.getById(id)

    suspend fun save(note: NoteEntity) {
        val now = System.currentTimeMillis()
        val toSave = if (note.id == 0L) {
            note.copy(createdAt = now, updatedAt = now)
        } else {
            note.copy(updatedAt = now)
        }
        if (toSave.id == 0L) noteDao.insert(toSave) else noteDao.update(toSave)
    }

    suspend fun delete(note: NoteEntity) = noteDao.delete(note)
}
