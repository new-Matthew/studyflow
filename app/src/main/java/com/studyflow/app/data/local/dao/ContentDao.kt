package com.studyflow.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.studyflow.app.data.local.entity.ContentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ContentDao {

    @Query("SELECT * FROM contents WHERE subjectId = :subjectId ORDER BY createdAt DESC")
    fun observeBySubject(subjectId: Long): Flow<List<ContentEntity>>

    @Query("SELECT * FROM contents WHERE id = :id")
    fun observeById(id: Long): Flow<ContentEntity?>

    @Query("SELECT * FROM contents WHERE id = :id")
    suspend fun getById(id: Long): ContentEntity?

    @Query("SELECT COUNT(*) FROM contents WHERE subjectId = :subjectId")
    fun observeCount(subjectId: Long): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(content: ContentEntity): Long

    @Update
    suspend fun update(content: ContentEntity)

    @Delete
    suspend fun delete(content: ContentEntity)
}
