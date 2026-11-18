package com.studyflow.app.data.local.relation

import androidx.room.Embedded
import com.studyflow.app.data.local.entity.DisciplineEntity

data class DisciplineWithSubjectCount(
    @Embedded val discipline: DisciplineEntity,
    val subjectCount: Int = 0
)
