package com.studyflow.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.studyflow.app.data.local.entity.SubjectEntity
import com.studyflow.app.data.local.relation.SubjectWithCounts
import com.studyflow.app.data.local.relation.SubjectWithDiscipline
import com.studyflow.app.data.model.LearningStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface SubjectDao {

    @Query(
        """
        SELECT s.*,
               (SELECT COUNT(*) FROM contents c WHERE c.subjectId = s.id) AS contentCount,
               (SELECT COUNT(*) FROM tips t WHERE t.subjectId = s.id) AS tipCount,
               (SELECT COUNT(*) FROM materials m WHERE m.subjectId = s.id) AS materialCount
        FROM subjects s
        WHERE s.disciplineId = :disciplineId
        ORDER BY s.name COLLATE NOCASE
        """
    )
    fun observeByDiscipline(disciplineId: Long): Flow<List<SubjectWithCounts>>

    @Query("SELECT * FROM subjects WHERE disciplineId = :disciplineId ORDER BY name COLLATE NOCASE")
    fun observeAllByDiscipline(disciplineId: Long): Flow<List<SubjectEntity>>

    @Query("SELECT * FROM subjects WHERE id = :id")
    fun observeById(id: Long): Flow<SubjectEntity?>

    @Query("SELECT * FROM subjects WHERE id = :id")
    suspend fun getById(id: Long): SubjectEntity?

    @Query("SELECT * FROM subjects ORDER BY name COLLATE NOCASE")
    fun observeAll(): Flow<List<SubjectEntity>>

    @Query("SELECT COUNT(*) FROM subjects")
    fun observeCount(): Flow<Int>

    @Query(
        """
        SELECT s.*, d.name AS disciplineName
        FROM subjects s
        INNER JOIN disciplines d ON d.id = s.disciplineId
        WHERE s.status = :status
        ORDER BY s.name COLLATE NOCASE
        """
    )
    fun observeByStatus(status: LearningStatus): Flow<List<SubjectWithDiscipline>>

    @Query(
        """
        SELECT s.*, d.name AS disciplineName
        FROM subjects s
        INNER JOIN disciplines d ON d.id = s.disciplineId
        ORDER BY s.name COLLATE NOCASE
        """
    )
    fun observeAllWithDiscipline(): Flow<List<SubjectWithDiscipline>>

    @Query("UPDATE subjects SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Long, status: LearningStatus)

    @Query("UPDATE subjects SET progress = :progress WHERE id = :id")
    suspend fun updateProgress(id: Long, progress: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(subject: SubjectEntity): Long

    @Update
    suspend fun update(subject: SubjectEntity)

    @Delete
    suspend fun delete(subject: SubjectEntity)
}
