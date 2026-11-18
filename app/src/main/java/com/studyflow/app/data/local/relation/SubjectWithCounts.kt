package com.studyflow.app.data.local.relation

import androidx.room.Embedded
import com.studyflow.app.data.local.entity.SubjectEntity

data class SubjectWithCounts(
    @Embedded val subject: SubjectEntity,
    val contentCount: Int = 0,
    val tipCount: Int = 0,
    val materialCount: Int = 0
)
