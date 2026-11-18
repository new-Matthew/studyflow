package com.studyflow.app.ui.screens.knowledge

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.studyflow.app.data.model.MaterialType
import com.studyflow.app.data.model.TipType
import com.studyflow.app.ui.AppViewModelProvider
import com.studyflow.app.ui.components.LabeledDropdown
import com.studyflow.app.ui.components.StudyFlowTopBar

@Composable
fun ContentEditScreen(
    onBack: () -> Unit,
    onSaved: () -> Unit,
    viewModel: ContentEditViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        StudyFlowTopBar(
            title = if (state.isEditing) "Editar conteúdo" else "Novo conteúdo",
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
            OutlinedTextField(
                value = state.topics,
                onValueChange = viewModel::onTopicsChange,
                label = { Text("Tópicos (separados por vírgula)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = state.explanation,
                onValueChange = viewModel::onExplanationChange,
                label = { Text("Explicação detalhada") },
                minLines = 6,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = state.examples,
                onValueChange = viewModel::onExamplesChange,
                label = { Text("Exemplos práticos") },
                minLines = 4,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = "Anexos de imagens e PDFs estarão disponíveis em versões futuras.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Button(
                onClick = { viewModel.save(onSaved) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (state.isEditing) "Salvar alterações" else "Salvar conteúdo")
            }
        }
    }
}

@Composable
fun TipEditScreen(
    onBack: () -> Unit,
    onSaved: () -> Unit,
    viewModel: TipEditViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        StudyFlowTopBar(
            title = if (state.isEditing) "Editar dica" else "Nova dica",
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
                label = { Text("Título da dica") },
                isError = state.error != null,
                supportingText = { state.error?.let { Text(it) } },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            LabeledDropdown(
                label = "Tipo de dica",
                options = TipType.entries.map { it.label },
                selected = state.type.label,
                onSelected = { label ->
                    TipType.entries.firstOrNull { it.label == label }?.let(viewModel::onTypeChange)
                }
            )
            OutlinedTextField(
                value = state.description,
                onValueChange = viewModel::onDescriptionChange,
                label = { Text("Descrição") },
                minLines = 5,
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = { viewModel.save(onSaved) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (state.isEditing) "Salvar alterações" else "Salvar dica")
            }
        }
    }
}

@Composable
fun MaterialEditScreen(
    onBack: () -> Unit,
    onSaved: () -> Unit,
    viewModel: MaterialEditViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        StudyFlowTopBar(
            title = if (state.isEditing) "Editar material" else "Novo material",
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
                label = "Tipo de material",
                options = MaterialType.entries.map { it.label },
                selected = state.type.label,
                onSelected = { label ->
                    MaterialType.entries.firstOrNull { it.label == label }?.let(viewModel::onTypeChange)
                }
            )
            OutlinedTextField(
                value = state.url,
                onValueChange = viewModel::onUrlChange,
                label = { Text("Link ou referência") },
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
            Button(
                onClick = { viewModel.save(onSaved) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (state.isEditing) "Salvar alterações" else "Salvar material")
            }
        }
    }
}
