package com.studyflow.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "disciplines")
data class DisciplineEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String = "",
    val colorHex: String = "#4F46E5",
    val icon: String = "book",
    val createdAt: Long = System.currentTimeMillis()
)
