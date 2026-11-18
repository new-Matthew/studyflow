package com.studyflow.app.data.local.relation

import androidx.room.Embedded
import com.studyflow.app.data.local.entity.NoteEntity

data class NoteWithSubject(
    @Embedded val note: NoteEntity,
    val subjectName: String? = null
)
