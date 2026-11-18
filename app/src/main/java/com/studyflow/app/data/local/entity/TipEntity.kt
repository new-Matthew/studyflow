package com.studyflow.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.studyflow.app.data.model.TipType

@Entity(
    tableName = "tips",
    foreignKeys = [
        ForeignKey(
            entity = SubjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["subjectId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("subjectId")]
)
data class TipEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val subjectId: Long,
    val title: String,
    val description: String = "",
    val type: TipType = TipType.CONCEPT,
    val createdAt: Long = System.currentTimeMillis()
)
