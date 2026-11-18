package com.studyflow.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.studyflow.app.data.model.ErrorReason

@Entity(
    tableName = "answer_records",
    foreignKeys = [
        ForeignKey(
            entity = QuestionEntity::class,
            parentColumns = ["id"],
            childColumns = ["questionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("questionId")]
)
data class AnswerRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val questionId: Long,
    val selectedAnswer: String,
    val isCorrect: Boolean,
    val errorReason: ErrorReason? = null,
    val answeredAt: Long = System.currentTimeMillis()
)
