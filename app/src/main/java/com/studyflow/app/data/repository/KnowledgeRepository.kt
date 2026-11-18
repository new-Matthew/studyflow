package com.studyflow.app.data.repository

import com.studyflow.app.data.local.dao.ContentDao
import com.studyflow.app.data.local.dao.MaterialDao
import com.studyflow.app.data.local.dao.TipDao
import com.studyflow.app.data.local.entity.ContentEntity
import com.studyflow.app.data.local.entity.MaterialEntity
import com.studyflow.app.data.local.entity.TipEntity
import kotlinx.coroutines.flow.Flow

class KnowledgeRepository(
    private val contentDao: ContentDao,
    private val tipDao: TipDao,
    private val materialDao: MaterialDao
) {

    fun observeContents(subjectId: Long): Flow<List<ContentEntity>> =
        contentDao.observeBySubject(subjectId)

    fun observeContent(id: Long): Flow<ContentEntity?> = contentDao.observeById(id)

    suspend fun getContent(id: Long): ContentEntity? = contentDao.getById(id)

    suspend fun saveContent(content: ContentEntity) {
        if (content.id == 0L) contentDao.insert(content) else contentDao.update(content)
    }

    suspend fun deleteContent(content: ContentEntity) = contentDao.delete(content)

    fun observeTips(subjectId: Long): Flow<List<TipEntity>> = tipDao.observeBySubject(subjectId)

    fun observeTip(id: Long): Flow<TipEntity?> = tipDao.observeById(id)

    suspend fun getTip(id: Long): TipEntity? = tipDao.getById(id)

    suspend fun saveTip(tip: TipEntity) {
        if (tip.id == 0L) tipDao.insert(tip) else tipDao.update(tip)
    }

    suspend fun deleteTip(tip: TipEntity) = tipDao.delete(tip)

    fun observeMaterials(subjectId: Long): Flow<List<MaterialEntity>> =
        materialDao.observeBySubject(subjectId)

    fun observeMaterial(id: Long): Flow<MaterialEntity?> = materialDao.observeById(id)

    suspend fun getMaterial(id: Long): MaterialEntity? = materialDao.getById(id)

    suspend fun saveMaterial(material: MaterialEntity) {
        if (material.id == 0L) materialDao.insert(material) else materialDao.update(material)
    }

    suspend fun deleteMaterial(material: MaterialEntity) = materialDao.delete(material)
}
