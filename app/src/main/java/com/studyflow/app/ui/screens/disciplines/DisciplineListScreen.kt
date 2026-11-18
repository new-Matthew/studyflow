package com.studyflow.app.ui.screens.disciplines

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.MenuBook
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
import com.studyflow.app.data.local.relation.DisciplineWithSubjectCount
import com.studyflow.app.ui.AppViewModelProvider
import com.studyflow.app.ui.components.ConfirmDeleteDialog
import com.studyflow.app.ui.components.EmptyState
import com.studyflow.app.ui.components.StudyFlowTopBar
import com.studyflow.app.ui.util.colorFromHex
import com.studyflow.app.ui.util.iconForKey

@Composable
fun DisciplineListScreen(
    onAddDiscipline: () -> Unit,
    onOpenDiscipline: (Long) -> Unit,
    onEditDiscipline: (Long) -> Unit,
    viewModel: DisciplineListViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val disciplines by viewModel.disciplines.collectAsStateWithLifecycle()
    var pendingDelete by remember { mutableStateOf<DisciplineWithSubjectCount?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        StudyFlowTopBar(
            title = "Disciplinas",
            subtitle = "${disciplines.size} cadastrada(s)",
            actions = {
                IconButton(onClick = onAddDiscipline) {
                    Icon(Icons.Rounded.Add, contentDescription = "Nova disciplina")
                }
            }
        )

        if (disciplines.isEmpty()) {
            EmptyState(
                icon = Icons.Rounded.MenuBook,
                title = "Comece sua organização",
                message = "Cadastre sua primeira disciplina e comece a estruturar seus estudos.",
                actionLabel = "Cadastrar disciplina",
                onAction = onAddDiscipline
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(disciplines, key = { it.discipline.id }) { item ->
                    DisciplineCard(
                        item = item,
                        onOpen = { onOpenDiscipline(item.discipline.id) },
                        onEdit = { onEditDiscipline(item.discipline.id) },
                        onDelete = { pendingDelete = item }
                    )
                }
            }
        }
    }

    pendingDelete?.let { item ->
        ConfirmDeleteDialog(
            title = "Excluir disciplina",
            message = "Ao excluir \"${item.discipline.name}\", todos os assuntos, conteúdos e materiais vinculados também serão removidos. Questões serão mantidas sem vínculo.",
            onConfirm = { viewModel.delete(item) },
            onDismiss = { pendingDelete = null }
        )
    }
}

@Composable
private fun DisciplineCard(
    item: DisciplineWithSubjectCount,
    onOpen: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val accent = colorFromHex(item.discipline.colorHex)
    Card(
        onClick = onOpen,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(accent.copy(alpha = 0.16f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = iconForKey(item.discipline.icon),
                    contentDescription = null,
                    tint = accent,
                    modifier = Modifier.size(26.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.discipline.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (item.discipline.description.isNotBlank()) {
                    Text(
                        text = item.discipline.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    text = pluralAssuntos(item.subjectCount),
                    style = MaterialTheme.typography.labelMedium,
                    color = accent
                )
            }
            IconButton(onClick = onEdit) {
                Icon(Icons.Rounded.Edit, contentDescription = "Editar disciplina")
            }
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Rounded.DeleteOutline,
                    contentDescription = "Excluir disciplina",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

private fun pluralAssuntos(count: Int): String =
    if (count == 1) "1 assunto" else "$count assuntos"
