package com.studyflow.app.ui.screens.disciplines

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.studyflow.app.ui.AppViewModelProvider
import com.studyflow.app.ui.components.ColorSelector
import com.studyflow.app.ui.components.IconSelector
import com.studyflow.app.ui.components.StudyFlowTopBar
import com.studyflow.app.ui.util.colorFromHex
import com.studyflow.app.ui.util.iconForKey

@Composable
fun DisciplineEditScreen(
    onBack: () -> Unit,
    onSaved: () -> Unit,
    viewModel: DisciplineEditViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val accent = colorFromHex(state.colorHex)

    Column(modifier = Modifier.fillMaxSize()) {
        StudyFlowTopBar(
            title = if (state.isEditing) "Editar disciplina" else "Nova disciplina",
            onBack = onBack
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(accent.copy(alpha = 0.16f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = iconForKey(state.icon),
                            contentDescription = null,
                            tint = accent,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            text = state.name.ifBlank { "Pré-visualização" },
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = state.description.ifBlank { "Descrição da disciplina" },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            OutlinedTextField(
                value = state.name,
                onValueChange = viewModel::onNameChange,
                label = { Text("Nome da disciplina") },
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

            Column {
                Text("Cor", style = MaterialTheme.typography.titleSmall)
                Spacer(Modifier.height(8.dp))
                ColorSelector(
                    selectedHex = state.colorHex,
                    onSelected = viewModel::onColorChange
                )
            }

            Column {
                Text("Ícone", style = MaterialTheme.typography.titleSmall)
                Spacer(Modifier.height(8.dp))
                IconSelector(
                    selectedKey = state.icon,
                    onSelected = viewModel::onIconChange,
                    tint = accent
                )
            }

            Button(
                onClick = { viewModel.save(onSaved) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (state.isEditing) "Salvar alterações" else "Cadastrar disciplina")
            }
        }
    }
}
