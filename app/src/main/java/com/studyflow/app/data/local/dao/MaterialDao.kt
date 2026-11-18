package com.studyflow.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.studyflow.app.data.local.entity.MaterialEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MaterialDao {

    @Query("SELECT * FROM materials WHERE subjectId = :subjectId ORDER BY createdAt DESC")
    fun observeBySubject(subjectId: Long): Flow<List<MaterialEntity>>

    @Query("SELECT * FROM materials WHERE id = :id")
    fun observeById(id: Long): Flow<MaterialEntity?>

    @Query("SELECT * FROM materials WHERE id = :id")
    suspend fun getById(id: Long): MaterialEntity?

    @Query("SELECT COUNT(*) FROM materials WHERE subjectId = :subjectId")
    fun observeCount(subjectId: Long): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(material: MaterialEntity): Long

    @Update
    suspend fun update(material: MaterialEntity)

    @Delete
    suspend fun delete(material: MaterialEntity)
}
