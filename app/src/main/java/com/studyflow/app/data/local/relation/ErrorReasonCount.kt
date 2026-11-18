package com.studyflow.app.data.local.relation

import com.studyflow.app.data.model.ErrorReason

data class ErrorReasonCount(
    val errorReason: ErrorReason?,
    val total: Int
)
