package com.studyflow.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.studyflow.app.data.model.LearningStatus

@Entity(
    tableName = "subjects",
    foreignKeys = [
        ForeignKey(
            entity = DisciplineEntity::class,
            parentColumns = ["id"],
            childColumns = ["disciplineId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("disciplineId")]
)
data class SubjectEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val disciplineId: Long,
    val name: String,
    val description: String = "",
    val status: LearningStatus = LearningStatus.NOT_STARTED,
    val progress: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)
