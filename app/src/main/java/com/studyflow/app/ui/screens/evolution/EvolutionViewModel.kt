package com.studyflow.app.ui.screens.evolution

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studyflow.app.data.local.relation.DisciplinePerformance
import com.studyflow.app.data.local.relation.ErrorReasonCount
import com.studyflow.app.data.repository.StatsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class EvolutionUiState(
    val disciplineCount: Int = 0,
    val subjectCount: Int = 0,
    val questionCount: Int = 0,
    val answeredCount: Int = 0,
    val correctCount: Int = 0,
    val answeredQuestionCount: Int = 0,
    val performanceByDiscipline: List<DisciplinePerformance> = emptyList(),
    val errorReasons: List<ErrorReasonCount> = emptyList()
) {
    val accuracy: Int
        get() = if (answeredCount == 0) 0 else correctCount * 100 / answeredCount
}

private data class EvolutionTotals(
    val disciplineCount: Int,
    val subjectCount: Int,
    val questionCount: Int,
    val answeredCount: Int,
    val correctCount: Int,
    val answeredQuestionCount: Int
)

class EvolutionViewModel(statsRepository: StatsRepository) : ViewModel() {

    private val totals = combine(
        statsRepository.disciplineCount,
        statsRepository.subjectCount,
        statsRepository.questionCount,
        statsRepository.answeredCount,
        statsRepository.correctCount
    ) { disciplines, subjects, questions, answered, correct ->
        EvolutionTotals(disciplines, subjects, questions, answered, correct, answered)
    }

    private val totalsWithUnique = combine(
        totals,
        statsRepository.answeredQuestionCount
    ) { base, unique ->
        base.copy(answeredQuestionCount = unique)
    }

    val state: StateFlow<EvolutionUiState> = combine(
        totalsWithUnique,
        statsRepository.performanceByDiscipline,
        statsRepository.errorReasons
    ) { totals, performance, errors ->
        EvolutionUiState(
            disciplineCount = totals.disciplineCount,
            subjectCount = totals.subjectCount,
            questionCount = totals.questionCount,
            answeredCount = totals.answeredCount,
            correctCount = totals.correctCount,
            answeredQuestionCount = totals.answeredQuestionCount,
            performanceByDiscipline = performance,
            errorReasons = errors
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        EvolutionUiState()
    )
}
