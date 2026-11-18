package com.studyflow.app.ui.screens.questions

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studyflow.app.data.local.entity.AnswerRecordEntity
import com.studyflow.app.data.local.entity.QuestionEntity
import com.studyflow.app.data.local.entity.SubjectEntity
import com.studyflow.app.data.local.relation.DisciplineWithSubjectCount
import com.studyflow.app.data.local.relation.QuestionWithContext
import com.studyflow.app.data.model.Difficulty
import com.studyflow.app.data.model.ErrorReason
import com.studyflow.app.data.repository.DisciplineRepository
import com.studyflow.app.data.repository.QuestionRepository
import com.studyflow.app.data.repository.SubjectRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class QuestionFilters(
    val disciplineId: Long? = null,
    val subjectId: Long? = null,
    val difficulty: Difficulty? = null
)

@OptIn(ExperimentalCoroutinesApi::class)
class QuestionListViewModel(
    private val questionRepository: QuestionRepository,
    disciplineRepository: DisciplineRepository,
    private val subjectRepository: SubjectRepository
) : ViewModel() {

    private val _filters = MutableStateFlow(QuestionFilters())
    val filters: StateFlow<QuestionFilters> = _filters.asStateFlow()

    val disciplines: StateFlow<List<DisciplineWithSubjectCount>> =
        disciplineRepository.disciplinesWithCounts.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            emptyList()
        )

    val subjects: StateFlow<List<SubjectEntity>> =
        _filters.map { it.disciplineId }
            .distinctUntilChanged()
            .flatMapLatest { disciplineId ->
                if (disciplineId == null) flowOf(emptyList())
                else subjectRepository.observeAllByDiscipline(disciplineId)
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val questions: StateFlow<List<QuestionWithContext>> =
        _filters.flatMapLatest { filter ->
            questionRepository.observeFiltered(
                disciplineId = filter.disciplineId,
                subjectId = filter.subjectId,
                difficulty = filter.difficulty
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun setDiscipline(id: Long?) {
        _filters.update { it.copy(disciplineId = id, subjectId = null) }
    }

    fun setSubject(id: Long?) {
        _filters.update { it.copy(subjectId = id) }
    }

    fun setDifficulty(difficulty: Difficulty?) {
        _filters.update { it.copy(difficulty = difficulty) }
    }

    fun clearFilters() {
        _filters.value = QuestionFilters()
    }

    fun delete(question: QuestionEntity) {
        viewModelScope.launch { questionRepository.delete(question) }
    }
}

data class QuestionFormState(
    val statement: String = "",
    val optionA: String = "",
    val optionB: String = "",
    val optionC: String = "",
    val optionD: String = "",
    val optionE: String = "",
    val correctAnswer: String = "A",
    val comment: String = "",
    val disciplineId: Long? = null,
    val subjectId: Long? = null,
    val board: String = "",
    val year: String = "",
    val source: String = "",
    val difficulty: Difficulty = Difficulty.MEDIUM,
    val isEditing: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val error: String? = null
)

@OptIn(ExperimentalCoroutinesApi::class)
class QuestionEditViewModel(
    private val repository: QuestionRepository,
    disciplineRepository: DisciplineRepository,
    subjectRepository: SubjectRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val questionId: Long = savedStateHandle.get<Long>("questionId") ?: 0L

    private val _state = MutableStateFlow(QuestionFormState(isEditing = questionId != 0L))
    val state: StateFlow<QuestionFormState> = _state.asStateFlow()

    val disciplines: StateFlow<List<DisciplineWithSubjectCount>> =
        disciplineRepository.disciplinesWithCounts.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            emptyList()
        )

    val subjects: StateFlow<List<SubjectEntity>> =
        _state.map { it.disciplineId }
            .distinctUntilChanged()
            .flatMapLatest { disciplineId ->
                if (disciplineId == null) flowOf(emptyList())
                else subjectRepository.observeAllByDiscipline(disciplineId)
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        if (questionId != 0L) {
            viewModelScope.launch {
                repository.getQuestion(questionId)?.let { question ->
                    _state.value = QuestionFormState(
                        statement = question.statement,
                        optionA = question.optionA,
                        optionB = question.optionB,
                        optionC = question.optionC,
                        optionD = question.optionD,
                        optionE = question.optionE,
                        correctAnswer = question.correctAnswer,
                        comment = question.comment,
                        disciplineId = question.disciplineId,
                        subjectId = question.subjectId,
                        board = question.board,
                        year = question.year?.toString() ?: "",
                        source = question.source,
                        difficulty = question.difficulty,
                        isEditing = true,
                        createdAt = question.createdAt
                    )
                }
            }
        }
    }

    fun onStatementChange(value: String) = _state.update { it.copy(statement = value, error = null) }
    fun onOptionChange(letter: String, value: String) = _state.update {
        when (letter) {
            "A" -> it.copy(optionA = value)
            "B" -> it.copy(optionB = value)
            "C" -> it.copy(optionC = value)
            "D" -> it.copy(optionD = value)
            else -> it.copy(optionE = value)
        }
    }

    fun onCorrectAnswerChange(value: String) = _state.update { it.copy(correctAnswer = value) }
    fun onCommentChange(value: String) = _state.update { it.copy(comment = value) }
    fun onDisciplineChange(id: Long?) = _state.update {
        it.copy(disciplineId = id, subjectId = null, error = null)
    }

    fun onSubjectChange(id: Long?) = _state.update { it.copy(subjectId = id) }
    fun onBoardChange(value: String) = _state.update { it.copy(board = value) }
    fun onYearChange(value: String) = _state.update {
        it.copy(year = value.filter(Char::isDigit).take(4))
    }

    fun onSourceChange(value: String) = _state.update { it.copy(source = value) }
    fun onDifficultyChange(value: Difficulty) = _state.update { it.copy(difficulty = value) }

    fun save(onSaved: () -> Unit) {
        val current = _state.value
        val options = listOf(
            current.optionA, current.optionB, current.optionC,
            current.optionD, current.optionE
        )
        when {
            current.statement.isBlank() -> {
                _state.update { it.copy(error = "Informe o enunciado da questão") }
                return
            }
            options.any { it.isBlank() } -> {
                _state.update { it.copy(error = "Preencha as alternativas A, B, C, D e E") }
                return
            }
        }
        viewModelScope.launch {
            repository.save(
                QuestionEntity(
                    id = if (current.isEditing) questionId else 0L,
                    statement = current.statement.trim(),
                    optionA = current.optionA.trim(),
                    optionB = current.optionB.trim(),
                    optionC = current.optionC.trim(),
                    optionD = current.optionD.trim(),
                    optionE = current.optionE.trim(),
                    correctAnswer = current.correctAnswer,
                    comment = current.comment.trim(),
                    disciplineId = current.disciplineId,
                    subjectId = current.subjectId,
                    board = current.board.trim(),
                    year = current.year.toIntOrNull(),
                    source = current.source.trim(),
                    difficulty = current.difficulty,
                    createdAt = current.createdAt
                )
            )
            onSaved()
        }
    }
}

data class ResolveState(
    val selectedAnswer: String? = null,
    val submitted: Boolean = false,
    val recorded: Boolean = false,
    val selectedReason: ErrorReason? = null,
    val error: String? = null
)

class QuestionResolveViewModel(
    private val repository: QuestionRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val questionId: Long = savedStateHandle.get<Long>("questionId") ?: 0L

    val question = repository.observeQuestion(questionId).stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        null
    )

    val history = repository.observeAnswersForQuestion(questionId).stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        emptyList()
    )

    private val _state = MutableStateFlow(ResolveState())
    val state: StateFlow<ResolveState> = _state.asStateFlow()

    fun selectAnswer(letter: String) {
        if (_state.value.recorded) return
        _state.value = ResolveState(selectedAnswer = letter)
    }

    fun selectReason(reason: ErrorReason?) {
        _state.update { it.copy(selectedReason = reason) }
    }

    fun submit() {
        val current = _state.value
        if (current.selectedAnswer == null) {
            _state.update { it.copy(error = "Escolha uma alternativa") }
            return
        }
        _state.update { it.copy(submitted = true, error = null) }
    }

    fun reset() {
        _state.value = ResolveState()
    }

    fun record() {
        val current = _state.value
        val selected = current.selectedAnswer ?: return
        val currentQuestion = question.value ?: return
        if (!current.submitted || current.recorded) return
        val isCorrect = selected == currentQuestion.correctAnswer
        viewModelScope.launch {
            repository.saveAnswer(
                AnswerRecordEntity(
                    questionId = currentQuestion.id,
                    selectedAnswer = selected,
                    isCorrect = isCorrect,
                    errorReason = if (isCorrect) null else current.selectedReason
                )
            )
            _state.update { it.copy(recorded = true) }
        }
    }
}
