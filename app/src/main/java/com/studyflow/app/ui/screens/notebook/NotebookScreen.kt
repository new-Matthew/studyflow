package com.studyflow.app.ui.screens.notebook

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.rounded.EditNote
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.studyflow.app.data.local.relation.NoteWithSubject
import com.studyflow.app.ui.AppViewModelProvider
import com.studyflow.app.ui.components.ConfirmDeleteDialog
import com.studyflow.app.ui.components.EmptyState
import com.studyflow.app.ui.components.StudyFlowTopBar
import com.studyflow.app.ui.util.formatDateTime

@Composable
fun NotebookScreen(
    onCreateNote: () -> Unit,
    onEditNote: (Long) -> Unit,
    viewModel: NotebookViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val notes by viewModel.notes.collectAsStateWithLifecycle()
    var pendingDelete by remember { mutableStateOf<NoteWithSubject?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        StudyFlowTopBar(
            title = "Caderno",
            subtitle = "${notes.size} anotação(ões)",
            actions = {
                IconButton(onClick = onCreateNote) {
                    Icon(Icons.Rounded.Add, contentDescription = "Nova anotação")
                }
            }
        )

        if (notes.isEmpty()) {
            EmptyState(
                icon = Icons.Rounded.EditNote,
                title = "Seu caderno está vazio",
                message = "Escreva anotações livres, resumos e observações dos seus estudos.",
                actionLabel = "Criar anotação",
                onAction = onCreateNote
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(notes, key = { it.note.id }) { item ->
                    NoteCard(
                        item = item,
                        onOpen = { onEditNote(item.note.id) },
                        onDelete = { pendingDelete = item }
                    )
                }
            }
        }
    }

    pendingDelete?.let { item ->
        ConfirmDeleteDialog(
            title = "Excluir anotação",
            message = "Remover \"${item.note.title}\"?",
            onConfirm = { viewModel.delete(item.note) },
            onDismiss = { pendingDelete = null }
        )
    }
}

@Composable
private fun NoteCard(
    item: NoteWithSubject,
    onOpen: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        onClick = onOpen,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = item.note.title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Rounded.DeleteOutline,
                        contentDescription = "Excluir anotação",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
            if (item.note.content.isNotBlank()) {
                Text(
                    text = item.note.content,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (item.subjectName != null) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(MaterialTheme.colorScheme.secondaryContainer)
                            .padding(horizontal = 10.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = item.subjectName,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
                Spacer(Modifier.weight(1f))
                Text(
                    text = formatDateTime(item.note.updatedAt),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
