package com.studyflow.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.studyflow.app.data.model.Difficulty

@Entity(
    tableName = "questions",
    foreignKeys = [
        ForeignKey(
            entity = DisciplineEntity::class,
            parentColumns = ["id"],
            childColumns = ["disciplineId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = SubjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["subjectId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("disciplineId"), Index("subjectId")]
)
data class QuestionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val statement: String,
    val optionA: String = "",
    val optionB: String = "",
    val optionC: String = "",
    val optionD: String = "",
    val optionE: String = "",
    val correctAnswer: String = "A",
    val comment: String = "",
    val disciplineId: Long? = null,
    val subjectId: Long? = null,
    val board: String = "",
    val year: Int? = null,
    val source: String = "",
    val difficulty: Difficulty = Difficulty.MEDIUM,
    val createdAt: Long = System.currentTimeMillis()
)
