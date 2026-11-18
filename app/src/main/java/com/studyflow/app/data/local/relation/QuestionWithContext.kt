package com.studyflow.app.data.local.relation

import androidx.room.Embedded
import com.studyflow.app.data.local.entity.QuestionEntity

data class QuestionWithContext(
    @Embedded val question: QuestionEntity,
    val disciplineName: String? = null,
    val subjectName: String? = null,
    val answerCount: Int = 0,
    val correctCount: Int = 0
)
