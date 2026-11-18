package com.studyflow.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.studyflow.app.data.local.entity.TipEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TipDao {

    @Query("SELECT * FROM tips WHERE subjectId = :subjectId ORDER BY createdAt DESC")
    fun observeBySubject(subjectId: Long): Flow<List<TipEntity>>

    @Query("SELECT * FROM tips WHERE id = :id")
    fun observeById(id: Long): Flow<TipEntity?>

    @Query("SELECT * FROM tips WHERE id = :id")
    suspend fun getById(id: Long): TipEntity?

    @Query("SELECT * FROM tips ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<TipEntity>>

    @Query("SELECT COUNT(*) FROM tips WHERE subjectId = :subjectId")
    fun observeCount(subjectId: Long): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(tip: TipEntity): Long

    @Update
    suspend fun update(tip: TipEntity)

    @Delete
    suspend fun delete(tip: TipEntity)
}
