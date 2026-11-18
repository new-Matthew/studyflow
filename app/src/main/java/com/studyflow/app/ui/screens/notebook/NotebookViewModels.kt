package com.studyflow.app.ui.screens.notebook

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studyflow.app.data.local.entity.NoteEntity
import com.studyflow.app.data.local.entity.SubjectEntity
import com.studyflow.app.data.local.relation.NoteWithSubject
import com.studyflow.app.data.repository.NotebookRepository
import com.studyflow.app.data.repository.SubjectRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NotebookViewModel(
    private val repository: NotebookRepository
) : ViewModel() {

    val notes: StateFlow<List<NoteWithSubject>> = repository.notes.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        emptyList()
    )

    fun delete(note: NoteEntity) {
        viewModelScope.launch { repository.delete(note) }
    }
}

data class NoteFormState(
    val title: String = "",
    val content: String = "",
    val subjectId: Long? = null,
    val isEditing: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val error: String? = null
)

class NoteEditViewModel(
    private val repository: NotebookRepository,
    subjectRepository: SubjectRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val noteId: Long = savedStateHandle.get<Long>("noteId") ?: 0L

    private val _state = MutableStateFlow(NoteFormState(isEditing = noteId != 0L))
    val state: StateFlow<NoteFormState> = _state.asStateFlow()

    val subjects: StateFlow<List<SubjectEntity>> = subjectRepository.allSubjects.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        emptyList()
    )

    init {
        if (noteId != 0L) {
            viewModelScope.launch {
                repository.getById(noteId)?.let { note ->
                    _state.value = NoteFormState(
                        title = note.title,
                        content = note.content,
                        subjectId = note.subjectId,
                        isEditing = true,
                        createdAt = note.createdAt
                    )
                }
            }
        }
    }

    fun onTitleChange(value: String) = _state.update { it.copy(title = value, error = null) }
    fun onContentChange(value: String) = _state.update { it.copy(content = value) }
    fun onSubjectChange(id: Long?) = _state.update { it.copy(subjectId = id) }

    fun save(onSaved: () -> Unit) {
        val current = _state.value
        if (current.title.isBlank()) {
            _state.update { it.copy(error = "Informe o título da anotação") }
            return
        }
        viewModelScope.launch {
            repository.save(
                NoteEntity(
                    id = if (current.isEditing) noteId else 0L,
                    title = current.title.trim(),
                    content = current.content.trim(),
                    subjectId = current.subjectId,
                    createdAt = current.createdAt,
                    updatedAt = System.currentTimeMillis()
                )
            )
            onSaved()
        }
    }
}
