package com.studyflow.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.studyflow.app.data.local.entity.DisciplineEntity
import com.studyflow.app.data.local.relation.DisciplineWithSubjectCount
import kotlinx.coroutines.flow.Flow

@Dao
interface DisciplineDao {

    @Query(
        """
        SELECT d.*,
               (SELECT COUNT(*) FROM subjects s WHERE s.disciplineId = d.id) AS subjectCount
        FROM disciplines d
        ORDER BY d.name COLLATE NOCASE
        """
    )
    fun observeAllWithCounts(): Flow<List<DisciplineWithSubjectCount>>

    @Query("SELECT * FROM disciplines ORDER BY name COLLATE NOCASE")
    fun observeAll(): Flow<List<DisciplineEntity>>

    @Query("SELECT * FROM disciplines WHERE id = :id")
    fun observeById(id: Long): Flow<DisciplineEntity?>

    @Query("SELECT * FROM disciplines WHERE id = :id")
    suspend fun getById(id: Long): DisciplineEntity?

    @Query("SELECT COUNT(*) FROM disciplines")
    fun observeCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(discipline: DisciplineEntity): Long

    @Update
    suspend fun update(discipline: DisciplineEntity)

    @Delete
    suspend fun delete(discipline: DisciplineEntity)
}
