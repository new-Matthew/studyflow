package com.studyflow.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.studyflow.app.data.model.MaterialType

@Entity(
    tableName = "materials",
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
data class MaterialEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val subjectId: Long,
    val title: String,
    val url: String = "",
    val description: String = "",
    val type: MaterialType = MaterialType.LINK,
    val createdAt: Long = System.currentTimeMillis()
)
