package com.studyflow.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.studyflow.app.data.local.entity.NoteEntity
import com.studyflow.app.data.local.relation.NoteWithSubject
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Query(
        """
        SELECT n.*, s.name AS subjectName
        FROM notes n
        LEFT JOIN subjects s ON s.id = n.subjectId
        ORDER BY n.updatedAt DESC
        """
    )
    fun observeAllWithSubject(): Flow<List<NoteWithSubject>>

    @Query("SELECT * FROM notes WHERE id = :id")
    fun observeById(id: Long): Flow<NoteEntity?>

    @Query("SELECT * FROM notes WHERE id = :id")
    suspend fun getById(id: Long): NoteEntity?

    @Query("SELECT COUNT(*) FROM notes")
    fun observeCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: NoteEntity): Long

    @Update
    suspend fun update(note: NoteEntity)

    @Delete
    suspend fun delete(note: NoteEntity)
}
