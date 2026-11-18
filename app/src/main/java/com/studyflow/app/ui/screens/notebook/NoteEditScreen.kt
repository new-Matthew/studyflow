package com.studyflow.app.ui.screens.notebook

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.studyflow.app.ui.AppViewModelProvider
import com.studyflow.app.ui.components.LabeledDropdown
import com.studyflow.app.ui.components.StudyFlowTopBar

@Composable
fun NoteEditScreen(
    onBack: () -> Unit,
    onSaved: () -> Unit,
    viewModel: NoteEditViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val subjects by viewModel.subjects.collectAsStateWithLifecycle()

    val subjectOptions = listOf("Sem assunto") + subjects.map { it.name }
    val selectedSubject = subjects.firstOrNull { it.id == state.subjectId }?.name ?: "Sem assunto"

    Column(modifier = Modifier.fillMaxSize()) {
        StudyFlowTopBar(
            title = if (state.isEditing) "Editar anotação" else "Nova anotação",
            onBack = onBack
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = state.title,
                onValueChange = viewModel::onTitleChange,
                label = { Text("Título") },
                isError = state.error != null,
                supportingText = { state.error?.let { Text(it) } },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            LabeledDropdown(
                label = "Assunto relacionado",
                options = subjectOptions,
                selected = selectedSubject,
                onSelected = { name ->
                    viewModel.onSubjectChange(
                        if (name == "Sem assunto") null
                        else subjects.firstOrNull { it.name == name }?.id
                    )
                }
            )

            OutlinedTextField(
                value = state.content,
                onValueChange = viewModel::onContentChange,
                label = { Text("Anotação") },
                minLines = 12,
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = { viewModel.save(onSaved) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (state.isEditing) "Salvar alterações" else "Salvar anotação")
            }
        }
    }
}
