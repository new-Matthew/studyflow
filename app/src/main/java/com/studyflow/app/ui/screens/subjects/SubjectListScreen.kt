package com.studyflow.app.ui.screens.subjects

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Topic
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.studyflow.app.data.local.relation.SubjectWithCounts
import com.studyflow.app.ui.AppViewModelProvider
import com.studyflow.app.ui.components.ConfirmDeleteDialog
import com.studyflow.app.ui.components.EmptyState
import com.studyflow.app.ui.components.LearningStatusChip
import com.studyflow.app.ui.components.StudyFlowTopBar

@Composable
fun SubjectListScreen(
    onBack: () -> Unit,
    onAddSubject: (Long) -> Unit,
    onOpenSubject: (Long) -> Unit,
    onEditSubject: (Long, Long) -> Unit,
    viewModel: SubjectListViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val discipline by viewModel.discipline.collectAsStateWithLifecycle()
    val subjects by viewModel.subjects.collectAsStateWithLifecycle()
    var pendingDelete by remember { mutableStateOf<SubjectWithCounts?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        StudyFlowTopBar(
            title = discipline?.name ?: "Assuntos",
            subtitle = "${subjects.size} assunto(s) nesta disciplina",
            onBack = onBack,
            actions = {
                IconButton(onClick = { onAddSubject(viewModel.disciplineId) }) {
                    Icon(Icons.Rounded.Add, contentDescription = "Novo assunto")
                }
            }
        )

        if (subjects.isEmpty()) {
            EmptyState(
                icon = Icons.Rounded.Topic,
                title = "Nenhum assunto cadastrado",
                message = "Divida esta disciplina em assuntos para estudar com mais foco.",
                actionLabel = "Cadastrar assunto",
                onAction = { onAddSubject(viewModel.disciplineId) }
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(subjects, key = { it.subject.id }) { item ->
                    SubjectCard(
                        item = item,
                        onOpen = { onOpenSubject(item.subject.id) },
                        onEdit = { onEditSubject(viewModel.disciplineId, item.subject.id) },
                        onDelete = { pendingDelete = item }
                    )
                }
            }
        }
    }

    pendingDelete?.let { item ->
        ConfirmDeleteDialog(
            title = "Excluir assunto",
            message = "Ao excluir \"${item.subject.name}\", seus conteúdos, dicas e materiais também serão removidos.",
            onConfirm = { viewModel.delete(item.subject) },
            onDismiss = { pendingDelete = null }
        )
    }
}

@Composable
private fun SubjectCard(
    item: SubjectWithCounts,
    onOpen: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        onClick = onOpen,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.subject.name,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (item.subject.description.isNotBlank()) {
                        Text(
                            text = item.subject.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                LearningStatusChip(status = item.subject.status)
            }
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "${item.contentCount} conteúdos",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${item.tipCount} dicas",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${item.materialCount} materiais",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.height(10.dp))
            LinearProgressIndicator(
                progress = { item.subject.progress / 100f },
                modifier = Modifier.fillMaxWidth(),
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
            Spacer(Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${item.subject.progress}% de progresso",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onEdit) {
                    Icon(Icons.Rounded.Edit, contentDescription = "Editar assunto")
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Rounded.DeleteOutline,
                        contentDescription = "Excluir assunto",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
