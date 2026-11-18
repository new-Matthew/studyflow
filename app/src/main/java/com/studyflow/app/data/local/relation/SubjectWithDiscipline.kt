package com.studyflow.app.data.local.relation

import androidx.room.Embedded
import com.studyflow.app.data.local.entity.SubjectEntity

data class SubjectWithDiscipline(
    @Embedded val subject: SubjectEntity,
    val disciplineName: String = ""
)
