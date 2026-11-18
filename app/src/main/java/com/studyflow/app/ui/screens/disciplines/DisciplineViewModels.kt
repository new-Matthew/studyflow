package com.studyflow.app.ui.screens.disciplines

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studyflow.app.data.local.entity.DisciplineEntity
import com.studyflow.app.data.local.relation.DisciplineWithSubjectCount
import com.studyflow.app.data.repository.DisciplineRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DisciplineListViewModel(
    private val repository: DisciplineRepository
) : ViewModel() {

    val disciplines: StateFlow<List<DisciplineWithSubjectCount>> =
        repository.disciplinesWithCounts.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            emptyList()
        )

    fun delete(item: DisciplineWithSubjectCount) {
        viewModelScope.launch { repository.delete(item.discipline) }
    }
}

data class DisciplineFormState(
    val name: String = "",
    val description: String = "",
    val colorHex: String = "#4F46E5",
    val icon: String = "book",
    val isEditing: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val error: String? = null
)

class DisciplineEditViewModel(
    private val repository: DisciplineRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val disciplineId: Long = savedStateHandle.get<Long>("disciplineId") ?: 0L

    private val _state = MutableStateFlow(
        DisciplineFormState(isEditing = disciplineId != 0L)
    )
    val state: StateFlow<DisciplineFormState> = _state.asStateFlow()

    init {
        if (disciplineId != 0L) {
            viewModelScope.launch {
                repository.getById(disciplineId)?.let { discipline ->
                    _state.value = DisciplineFormState(
                        name = discipline.name,
                        description = discipline.description,
                        colorHex = discipline.colorHex,
                        icon = discipline.icon,
                        isEditing = true,
                        createdAt = discipline.createdAt
                    )
                }
            }
        }
    }

    fun onNameChange(value: String) = _state.update { it.copy(name = value, error = null) }
    fun onDescriptionChange(value: String) = _state.update { it.copy(description = value) }
    fun onColorChange(value: String) = _state.update { it.copy(colorHex = value) }
    fun onIconChange(value: String) = _state.update { it.copy(icon = value) }

    fun save(onSaved: () -> Unit) {
        val current = _state.value
        if (current.name.isBlank()) {
            _state.update { it.copy(error = "Informe o nome da disciplina") }
            return
        }
        viewModelScope.launch {
            repository.save(
                DisciplineEntity(
                    id = if (current.isEditing) disciplineId else 0L,
                    name = current.name.trim(),
                    description = current.description.trim(),
                    colorHex = current.colorHex,
                    icon = current.icon,
                    createdAt = current.createdAt
                )
            )
            onSaved()
        }
    }
}
