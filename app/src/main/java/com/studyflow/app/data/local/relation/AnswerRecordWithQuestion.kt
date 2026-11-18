package com.studyflow.app.data.local.relation

import androidx.room.Embedded
import com.studyflow.app.data.local.entity.AnswerRecordEntity

data class AnswerRecordWithQuestion(
    @Embedded val record: AnswerRecordEntity,
    val statement: String = "",
    val difficulty: String = "",
    val disciplineName: String? = null
)
