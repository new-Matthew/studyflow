package com.studyflow.app.ui.screens.subjects

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studyflow.app.data.local.entity.SubjectEntity
import com.studyflow.app.data.local.relation.SubjectWithCounts
import com.studyflow.app.data.model.LearningStatus
import com.studyflow.app.data.repository.DisciplineRepository
import com.studyflow.app.data.repository.KnowledgeRepository
import com.studyflow.app.data.repository.SubjectRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SubjectListViewModel(
    private val subjectRepository: SubjectRepository,
    disciplineRepository: DisciplineRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val disciplineId: Long = savedStateHandle.get<Long>("disciplineId") ?: 0L

    val discipline = disciplineRepository.observeById(disciplineId).stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        null
    )

    val subjects: StateFlow<List<SubjectWithCounts>> =
        subjectRepository.observeByDiscipline(disciplineId).stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            emptyList()
        )

    fun delete(subject: SubjectEntity) {
        viewModelScope.launch { subjectRepository.delete(subject) }
    }
}

data class SubjectFormState(
    val name: String = "",
    val description: String = "",
    val status: LearningStatus = LearningStatus.NOT_STARTED,
    val progress: Int = 0,
    val isEditing: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val error: String? = null
)

class SubjectEditViewModel(
    private val repository: SubjectRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val disciplineId: Long = savedStateHandle.get<Long>("disciplineId") ?: 0L
    private val subjectId: Long = savedStateHandle.get<Long>("subjectId") ?: 0L

    private val _state = MutableStateFlow(SubjectFormState(isEditing = subjectId != 0L))
    val state: StateFlow<SubjectFormState> = _state.asStateFlow()

    init {
        if (subjectId != 0L) {
            viewModelScope.launch {
                repository.getById(subjectId)?.let { subject ->
                    _state.value = SubjectFormState(
                        name = subject.name,
                        description = subject.description,
                        status = subject.status,
                        progress = subject.progress,
                        isEditing = true,
                        createdAt = subject.createdAt
                    )
                }
            }
        }
    }

    fun onNameChange(value: String) = _state.update { it.copy(name = value, error = null) }
    fun onDescriptionChange(value: String) = _state.update { it.copy(description = value) }
    fun onStatusChange(value: LearningStatus) = _state.update { it.copy(status = value) }
    fun onProgressChange(value: Int) = _state.update { it.copy(progress = value.coerceIn(0, 100)) }

    fun save(onSaved: () -> Unit) {
        val current = _state.value
        if (current.name.isBlank()) {
            _state.update { it.copy(error = "Informe o nome do assunto") }
            return
        }
        if (disciplineId == 0L) {
            _state.update { it.copy(error = "Disciplina inválida") }
            return
        }
        viewModelScope.launch {
            repository.save(
                SubjectEntity(
                    id = if (current.isEditing) subjectId else 0L,
                    disciplineId = disciplineId,
                    name = current.name.trim(),
                    description = current.description.trim(),
                    status = current.status,
                    progress = current.progress,
                    createdAt = current.createdAt
                )
            )
            onSaved()
        }
    }
}

class SubjectDetailViewModel(
    private val subjectRepository: SubjectRepository,
    private val knowledgeRepository: KnowledgeRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val subjectId: Long = savedStateHandle.get<Long>("subjectId") ?: 0L

    val subject = subjectRepository.observeById(subjectId).stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        null
    )

    val contents = knowledgeRepository.observeContents(subjectId).stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        emptyList()
    )

    val tips = knowledgeRepository.observeTips(subjectId).stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        emptyList()
    )

    val materials = knowledgeRepository.observeMaterials(subjectId).stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        emptyList()
    )

    fun updateStatus(status: LearningStatus) {
        viewModelScope.launch { subjectRepository.updateStatus(subjectId, status) }
    }

    fun updateProgress(progress: Int) {
        viewModelScope.launch { subjectRepository.updateProgress(subjectId, progress) }
    }

    fun deleteContent(content: com.studyflow.app.data.local.entity.ContentEntity) {
        viewModelScope.launch { knowledgeRepository.deleteContent(content) }
    }

    fun deleteTip(tip: com.studyflow.app.data.local.entity.TipEntity) {
        viewModelScope.launch { knowledgeRepository.deleteTip(tip) }
    }

    fun deleteMaterial(material: com.studyflow.app.data.local.entity.MaterialEntity) {
        viewModelScope.launch { knowledgeRepository.deleteMaterial(material) }
    }
}
