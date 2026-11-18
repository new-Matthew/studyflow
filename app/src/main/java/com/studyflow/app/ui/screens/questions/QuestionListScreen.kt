package com.studyflow.app.ui.screens.questions

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Quiz
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.studyflow.app.data.local.relation.QuestionWithContext
import com.studyflow.app.data.model.Difficulty
import com.studyflow.app.ui.AppViewModelProvider
import com.studyflow.app.ui.components.ConfirmDeleteDialog
import com.studyflow.app.ui.components.DifficultyChip
import com.studyflow.app.ui.components.EmptyState
import com.studyflow.app.ui.components.LabeledDropdown
import com.studyflow.app.ui.components.StudyFlowTopBar

@Composable
fun QuestionListScreen(
    onCreateQuestion: () -> Unit,
    onEditQuestion: (Long) -> Unit,
    onResolveQuestion: (Long) -> Unit,
    viewModel: QuestionListViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val filters by viewModel.filters.collectAsStateWithLifecycle()
    val disciplines by viewModel.disciplines.collectAsStateWithLifecycle()
    val subjects by viewModel.subjects.collectAsStateWithLifecycle()
    val questions by viewModel.questions.collectAsStateWithLifecycle()
    var pendingDelete by remember { mutableStateOf<QuestionWithContext?>(null) }

    val disciplineOptions = listOf("Todas") + disciplines.map { it.discipline.name }
    val disciplineSelected = disciplines
        .firstOrNull { it.discipline.id == filters.disciplineId }
        ?.discipline?.name ?: "Todas"

    val subjectOptions = listOf("Todos") + subjects.map { it.name }
    val subjectSelected = subjects.firstOrNull { it.id == filters.subjectId }?.name ?: "Todos"

    val difficultyOptions = listOf("Todas") + Difficulty.entries.map { it.label }
    val difficultySelected = filters.difficulty?.label ?: "Todas"

    Column(modifier = Modifier.fillMaxSize()) {
        StudyFlowTopBar(
            title = "Questões",
            subtitle = "${questions.size} encontrada(s)",
            actions = {
                IconButton(onClick = onCreateQuestion) {
                    Icon(Icons.Rounded.Add, contentDescription = "Nova questão")
                }
            }
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LabeledDropdown(
                    label = "Disciplina",
                    options = disciplineOptions,
                    selected = disciplineSelected,
                    onSelected = { name ->
                        viewModel.setDiscipline(
                            if (name == "Todas") null
                            else disciplines.firstOrNull { it.discipline.name == name }?.discipline?.id
                        )
                    }
                )
                LabeledDropdown(
                    label = "Assunto",
                    options = subjectOptions,
                    selected = subjectSelected,
                    onSelected = { name ->
                        viewModel.setSubject(
                            if (name == "Todos") null
                            else subjects.firstOrNull { it.name == name }?.id
                        )
                    }
                )
                LabeledDropdown(
                    label = "Dificuldade",
                    options = difficultyOptions,
                    selected = difficultySelected,
                    onSelected = { label ->
                        viewModel.setDifficulty(
                            if (label == "Todas") null
                            else Difficulty.entries.firstOrNull { it.label == label }
                        )
                    }
                )
                TextButton(onClick = viewModel::clearFilters) {
                    Text("Limpar filtros")
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        if (questions.isEmpty()) {
            EmptyState(
                icon = Icons.Rounded.Quiz,
                title = "Nenhuma questão",
                message = "Cadastre questões ou ajuste os filtros para encontrá-las.",
                actionLabel = "Cadastrar questão",
                onAction = onCreateQuestion
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(questions, key = { it.question.id }) { item ->
                    QuestionCard(
                        item = item,
                        onResolve = { onResolveQuestion(item.question.id) },
                        onEdit = { onEditQuestion(item.question.id) },
                        onDelete = { pendingDelete = item }
                    )
                }
            }
        }
    }

    pendingDelete?.let { item ->
        ConfirmDeleteDialog(
            title = "Excluir questão",
            message = "O histórico de respostas desta questão também será removido.",
            onConfirm = { viewModel.delete(item.question) },
            onDismiss = { pendingDelete = null }
        )
    }
}

@Composable
private fun QuestionCard(
    item: QuestionWithContext,
    onResolve: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val question = item.question
    Card(
        onClick = onResolve,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = question.statement,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(10.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                item.disciplineName?.let { name ->
                    Tag(text = name, color = MaterialTheme.colorScheme.primary)
                }
                DifficultyChip(difficulty = question.difficulty)
            }
            if (item.subjectName != null || question.board.isNotBlank() || question.year != null) {
                Spacer(Modifier.height(6.dp))
                val details = buildList {
                    item.subjectName?.let { add(it) }
                    if (question.board.isNotBlank()) add(question.board)
                    question.year?.let { add(it.toString()) }
                }.joinToString(" · ")
                if (details.isNotBlank()) {
                    Text(
                        text = details,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                val accuracy = if (item.answerCount > 0) {
                    "${item.answerCount} resposta(s) · ${item.correctCount * 100 / item.answerCount}% acertos"
                } else {
                    "Ainda não respondida"
                }
                Text(
                    text = accuracy,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onEdit) {
                    Icon(Icons.Rounded.Edit, contentDescription = "Editar questão")
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Rounded.DeleteOutline,
                        contentDescription = "Excluir questão",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
private fun Tag(text: String, color: androidx.compose.ui.graphics.Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(color.copy(alpha = 0.14f))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = color,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
