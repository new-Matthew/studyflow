package com.studyflow.app.ui.screens.subjects

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.studyflow.app.data.model.LearningStatus
import com.studyflow.app.ui.AppViewModelProvider
import com.studyflow.app.ui.components.LabeledDropdown
import com.studyflow.app.ui.components.StudyFlowTopBar

@Composable
fun SubjectEditScreen(
    onBack: () -> Unit,
    onSaved: () -> Unit,
    viewModel: SubjectEditViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        StudyFlowTopBar(
            title = if (state.isEditing) "Editar assunto" else "Novo assunto",
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
                value = state.name,
                onValueChange = viewModel::onNameChange,
                label = { Text("Nome do assunto") },
                isError = state.error != null,
                supportingText = { state.error?.let { Text(it) } },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.description,
                onValueChange = viewModel::onDescriptionChange,
                label = { Text("Descrição (opcional)") },
                minLines = 3,
                modifier = Modifier.fillMaxWidth()
            )

            LabeledDropdown(
                label = "Situação do aprendizado",
                options = LearningStatus.entries.map { it.label },
                selected = state.status.label,
                onSelected = { label ->
                    LearningStatus.entries.firstOrNull { it.label == label }?.let(viewModel::onStatusChange)
                }
            )

            Column {
                Text(
                    text = "Progresso: ${state.progress}%",
                    style = MaterialTheme.typography.titleSmall
                )
                Spacer(Modifier.height(4.dp))
                Slider(
                    value = state.progress.toFloat(),
                    onValueChange = { viewModel.onProgressChange(it.toInt()) },
                    valueRange = 0f..100f,
                    steps = 19
                )
            }

            Button(
                onClick = { viewModel.save(onSaved) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (state.isEditing) "Salvar alterações" else "Cadastrar assunto")
            }
        }
    }
}
