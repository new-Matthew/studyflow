package com.studyflow.app.ui.screens.knowledge

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studyflow.app.data.local.entity.ContentEntity
import com.studyflow.app.data.local.entity.MaterialEntity
import com.studyflow.app.data.local.entity.TipEntity
import com.studyflow.app.data.model.MaterialType
import com.studyflow.app.data.model.TipType
import com.studyflow.app.data.repository.KnowledgeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ContentFormState(
    val title: String = "",
    val explanation: String = "",
    val examples: String = "",
    val topics: String = "",
    val isEditing: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val error: String? = null
)

class ContentEditViewModel(
    private val repository: KnowledgeRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val subjectId: Long = savedStateHandle.get<Long>("subjectId") ?: 0L
    private val contentId: Long = savedStateHandle.get<Long>("contentId") ?: 0L

    private val _state = MutableStateFlow(ContentFormState(isEditing = contentId != 0L))
    val state: StateFlow<ContentFormState> = _state.asStateFlow()

    init {
        if (contentId != 0L) {
            viewModelScope.launch {
                repository.getContent(contentId)?.let { content ->
                    _state.value = ContentFormState(
                        title = content.title,
                        explanation = content.explanation,
                        examples = content.examples,
                        topics = content.topics,
                        isEditing = true,
                        createdAt = content.createdAt
                    )
                }
            }
        }
    }

    fun onTitleChange(value: String) = _state.update { it.copy(title = value, error = null) }
    fun onExplanationChange(value: String) = _state.update { it.copy(explanation = value) }
    fun onExamplesChange(value: String) = _state.update { it.copy(examples = value) }
    fun onTopicsChange(value: String) = _state.update { it.copy(topics = value) }

    fun save(onSaved: () -> Unit) {
        val current = _state.value
        if (current.title.isBlank()) {
            _state.update { it.copy(error = "Informe o título do conteúdo") }
            return
        }
        viewModelScope.launch {
            repository.saveContent(
                ContentEntity(
                    id = if (current.isEditing) contentId else 0L,
                    subjectId = subjectId,
                    title = current.title.trim(),
                    explanation = current.explanation.trim(),
                    examples = current.examples.trim(),
                    topics = current.topics.trim(),
                    createdAt = current.createdAt
                )
            )
            onSaved()
        }
    }
}

data class TipFormState(
    val title: String = "",
    val description: String = "",
    val type: TipType = TipType.CONCEPT,
    val isEditing: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val error: String? = null
)

class TipEditViewModel(
    private val repository: KnowledgeRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val subjectId: Long = savedStateHandle.get<Long>("subjectId") ?: 0L
    private val tipId: Long = savedStateHandle.get<Long>("tipId") ?: 0L

    private val _state = MutableStateFlow(TipFormState(isEditing = tipId != 0L))
    val state: StateFlow<TipFormState> = _state.asStateFlow()

    init {
        if (tipId != 0L) {
            viewModelScope.launch {
                repository.getTip(tipId)?.let { tip ->
                    _state.value = TipFormState(
                        title = tip.title,
                        description = tip.description,
                        type = tip.type,
                        isEditing = true,
                        createdAt = tip.createdAt
                    )
                }
            }
        }
    }

    fun onTitleChange(value: String) = _state.update { it.copy(title = value, error = null) }
    fun onDescriptionChange(value: String) = _state.update { it.copy(description = value) }
    fun onTypeChange(value: TipType) = _state.update { it.copy(type = value) }

    fun save(onSaved: () -> Unit) {
        val current = _state.value
        if (current.title.isBlank()) {
            _state.update { it.copy(error = "Informe o título da dica") }
            return
        }
        viewModelScope.launch {
            repository.saveTip(
                TipEntity(
                    id = if (current.isEditing) tipId else 0L,
                    subjectId = subjectId,
                    title = current.title.trim(),
                    description = current.description.trim(),
                    type = current.type,
                    createdAt = current.createdAt
                )
            )
            onSaved()
        }
    }
}

data class MaterialFormState(
    val title: String = "",
    val url: String = "",
    val description: String = "",
    val type: MaterialType = MaterialType.LINK,
    val isEditing: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val error: String? = null
)

class MaterialEditViewModel(
    private val repository: KnowledgeRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val subjectId: Long = savedStateHandle.get<Long>("subjectId") ?: 0L
    private val materialId: Long = savedStateHandle.get<Long>("materialId") ?: 0L

    private val _state = MutableStateFlow(MaterialFormState(isEditing = materialId != 0L))
    val state: StateFlow<MaterialFormState> = _state.asStateFlow()

    init {
        if (materialId != 0L) {
            viewModelScope.launch {
                repository.getMaterial(materialId)?.let { material ->
                    _state.value = MaterialFormState(
                        title = material.title,
                        url = material.url,
                        description = material.description,
                        type = material.type,
                        isEditing = true,
                        createdAt = material.createdAt
                    )
                }
            }
        }
    }

    fun onTitleChange(value: String) = _state.update { it.copy(title = value, error = null) }
    fun onUrlChange(value: String) = _state.update { it.copy(url = value) }
    fun onDescriptionChange(value: String) = _state.update { it.copy(description = value) }
    fun onTypeChange(value: MaterialType) = _state.update { it.copy(type = value) }

    fun save(onSaved: () -> Unit) {
        val current = _state.value
        if (current.title.isBlank()) {
            _state.update { it.copy(error = "Informe o título do material") }
            return
        }
        viewModelScope.launch {
            repository.saveMaterial(
                MaterialEntity(
                    id = if (current.isEditing) materialId else 0L,
                    subjectId = subjectId,
                    title = current.title.trim(),
                    url = current.url.trim(),
                    description = current.description.trim(),
                    type = current.type,
                    createdAt = current.createdAt
                )
            )
            onSaved()
        }
    }
}
