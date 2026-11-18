package com.studyflow.app.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studyflow.app.data.local.relation.AnswerRecordWithQuestion
import com.studyflow.app.data.local.relation.SubjectWithDiscipline
import com.studyflow.app.data.repository.StatsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class HomeUiState(
    val disciplineCount: Int = 0,
    val questionCount: Int = 0,
    val answeredCount: Int = 0,
    val correctCount: Int = 0,
    val subjectsToReview: List<SubjectWithDiscipline> = emptyList(),
    val recentAnswers: List<AnswerRecordWithQuestion> = emptyList()
) {
    val accuracy: Int
        get() = if (answeredCount == 0) 0 else correctCount * 100 / answeredCount
}

private data class HomeSummary(
    val disciplineCount: Int,
    val questionCount: Int,
    val answeredCount: Int,
    val correctCount: Int
)

class HomeViewModel(statsRepository: StatsRepository) : ViewModel() {

    private val summary = combine(
        statsRepository.disciplineCount,
        statsRepository.questionCount,
        statsRepository.answeredCount,
        statsRepository.correctCount
    ) { disciplines, questions, answered, correct ->
        HomeSummary(disciplines, questions, answered, correct)
    }

    val state: StateFlow<HomeUiState> = combine(
        summary,
        statsRepository.subjectsToReview,
        statsRepository.recentAnswers
    ) { totals, review, recent ->
        HomeUiState(
            disciplineCount = totals.disciplineCount,
            questionCount = totals.questionCount,
            answeredCount = totals.answeredCount,
            correctCount = totals.correctCount,
            subjectsToReview = review,
            recentAnswers = recent
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        HomeUiState()
    )
}
