package com.studyflow.app.ui.screens.questions

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.studyflow.app.data.local.entity.QuestionEntity
import com.studyflow.app.data.local.relation.AnswerRecordWithQuestion
import com.studyflow.app.data.model.ErrorReason
import com.studyflow.app.ui.AppViewModelProvider
import com.studyflow.app.ui.components.DifficultyChip
import com.studyflow.app.ui.components.SectionTitle
import com.studyflow.app.ui.components.StudyFlowTopBar
import com.studyflow.app.ui.theme.SuccessColor
import com.studyflow.app.ui.util.formatDateTime

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun QuestionResolveScreen(
    onBack: () -> Unit,
    onEdit: (Long) -> Unit,
    viewModel: QuestionResolveViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val question by viewModel.question.collectAsStateWithLifecycle()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val history by viewModel.history.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        StudyFlowTopBar(
            title = "Resolver questão",
            subtitle = question?.board?.ifBlank { null },
            onBack = onBack
        )

        val current = question
        if (current == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Questão não encontrada")
            }
            return@Column
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                DifficultyChip(difficulty = current.difficulty)
                if (current.year != null) {
                    Text(
                        text = "${current.board.ifBlank { "Ano" }} ${current.year}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Text(
                    text = current.statement,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(16.dp)
                )
            }

            optionsOf(current).forEach { (letter, text) ->
                OptionCard(
                    letter = letter,
                    text = text,
                    isSelected = state.selectedAnswer == letter,
                    isCorrect = letter == current.correctAnswer,
                    submitted = state.submitted,
                    onClick = { viewModel.selectAnswer(letter) }
                )
            }

            if (state.error != null) {
                Text(
                    text = state.error ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            if (!state.submitted) {
                Button(
                    onClick = viewModel::submit,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Responder")
                }
            } else {
                val isCorrect = state.selectedAnswer == current.correctAnswer
                ResultCard(current = current, isCorrect = isCorrect)

                if (!isCorrect && !state.recorded) {
                    Text(
                        text = "Por que você errou? (opcional)",
                        style = MaterialTheme.typography.titleSmall
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        ErrorReason.entries.forEach { reason ->
                            FilterChip(
                                selected = state.selectedReason == reason,
                                onClick = {
                                    viewModel.selectReason(
                                        if (state.selectedReason == reason) null else reason
                                    )
                                },
                                label = { Text(reason.label) }
                            )
                        }
                    }
                }

                if (!state.recorded) {
                    Button(
                        onClick = viewModel::record,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Salvar no histórico")
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Rounded.CheckCircle,
                            contentDescription = null,
                            tint = SuccessColor
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Resposta registrada no histórico.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = SuccessColor,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    OutlinedButton(
                        onClick = viewModel::reset,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Responder novamente")
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable { onEdit(current.id) }
                    .padding(12.dp)
            ) {
                Text(
                    text = "Editar questão",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            SectionTitle("Histórico de respostas")
            if (history.isEmpty()) {
                Text(
                    text = "Nenhuma tentativa registrada ainda.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                history.forEach { record ->
                    HistoryRow(record)
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

private fun optionsOf(question: QuestionEntity): List<Pair<String, String>> = listOf(
    "A" to question.optionA,
    "B" to question.optionB,
    "C" to question.optionC,
    "D" to question.optionD,
    "E" to question.optionE
).filter { it.second.isNotBlank() }

@Composable
private fun OptionCard(
    letter: String,
    text: String,
    isSelected: Boolean,
    isCorrect: Boolean,
    submitted: Boolean,
    onClick: () -> Unit
) {
    val borderColor = when {
        submitted && isCorrect -> SuccessColor
        submitted && isSelected && !isCorrect -> MaterialTheme.colorScheme.error
        isSelected -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    val background = when {
        submitted && isCorrect -> SuccessColor.copy(alpha = 0.10f)
        submitted && isSelected && !isCorrect -> MaterialTheme.colorScheme.error.copy(alpha = 0.10f)
        isSelected -> MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
        else -> MaterialTheme.colorScheme.surface
    }

    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = background),
        border = BorderStroke(1.5.dp, borderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(borderColor.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = letter,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = borderColor
                )
            }
            Spacer(Modifier.width(12.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
            when {
                submitted && isCorrect -> Icon(
                    Icons.Rounded.CheckCircle,
                    contentDescription = "Correta",
                    tint = SuccessColor
                )
                submitted && isSelected && !isCorrect -> Icon(
                    Icons.Rounded.Cancel,
                    contentDescription = "Incorreta",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun ResultCard(current: QuestionEntity, isCorrect: Boolean) {
    val accent = if (isCorrect) SuccessColor else MaterialTheme.colorScheme.error
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = accent.copy(alpha = 0.10f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = if (isCorrect) "Você acertou!" else "Resposta incorreta",
                style = MaterialTheme.typography.titleMedium,
                color = accent,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Gabarito: ${current.correctAnswer}",
                style = MaterialTheme.typography.labelLarge
            )
            if (current.comment.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = current.comment,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun HistoryRow(record: AnswerRecordWithQuestion) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (record.record.isCorrect) Icons.Rounded.CheckCircle else Icons.Rounded.Cancel,
            contentDescription = null,
            tint = if (record.record.isCorrect) SuccessColor else Color(0xFFDC2626),
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Alternativa ${record.record.selectedAnswer}",
                style = MaterialTheme.typography.bodyMedium
            )
            val reason = record.record.errorReason?.label
            Text(
                text = buildString {
                    append(formatDateTime(record.record.answeredAt))
                    if (reason != null) append(" · $reason")
                },
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
