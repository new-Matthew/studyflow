package com.studyflow.app.ui.screens.evolution

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Insights
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material.icons.rounded.Quiz
import androidx.compose.material.icons.rounded.Topic
import androidx.compose.material.icons.rounded.TrackChanges
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.studyflow.app.data.local.relation.DisciplinePerformance
import com.studyflow.app.data.local.relation.ErrorReasonCount
import com.studyflow.app.ui.AppViewModelProvider
import com.studyflow.app.ui.components.SectionTitle
import com.studyflow.app.ui.components.StatCard
import com.studyflow.app.ui.components.StudyFlowTopBar
import com.studyflow.app.ui.theme.SuccessColor
import com.studyflow.app.ui.theme.WarningColor

@Composable
fun EvolutionScreen(
    viewModel: EvolutionViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        StudyFlowTopBar(
            title = "Evolução",
            subtitle = "Acompanhe seu desempenho nos estudos"
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AccuracyCard(state)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    value = state.disciplineCount.toString(),
                    label = "Disciplinas",
                    icon = Icons.Rounded.MenuBook,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    value = state.subjectCount.toString(),
                    label = "Assuntos",
                    icon = Icons.Rounded.Topic,
                    accent = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    value = state.questionCount.toString(),
                    label = "Questões",
                    icon = Icons.Rounded.Quiz,
                    accent = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.weight(1f)
                )
            }

            SectionTitle("Desempenho por disciplina")
            if (state.performanceByDiscipline.isEmpty()) {
                EmptyInfo("Responda questões para visualizar seu desempenho por disciplina.")
            } else {
                state.performanceByDiscipline.forEach { performance ->
                    PerformanceRow(performance)
                }
            }

            SectionTitle("Motivos de erro")
            if (state.errorReasons.isEmpty()) {
                EmptyInfo("Nenhum erro registrado até agora. Excelente trabalho!")
            } else {
                val max = state.errorReasons.maxOf { it.total }.coerceAtLeast(1)
                state.errorReasons.forEach { reason ->
                    ErrorReasonRow(reason = reason, max = max)
                }
            }

            SectionTitle("Resumo")
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SummaryRow("Questões respondidas", state.answeredCount.toString())
                    SummaryRow("Questões distintas", state.answeredQuestionCount.toString())
                    SummaryRow("Acertos", state.correctCount.toString())
                    SummaryRow("Erros", (state.answeredCount - state.correctCount).toString())
                    SummaryRow("Aproveitamento", "${state.accuracy}%")
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun AccuracyCard(state: EvolutionUiState) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress = { state.accuracy / 100f },
                    modifier = Modifier.size(96.dp),
                    strokeWidth = 10.dp,
                    color = SuccessColor,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
                Text(
                    text = "${state.accuracy}%",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.size(20.dp))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Rounded.TrackChanges,
                        contentDescription = null,
                        tint = SuccessColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.size(6.dp))
                    Text(
                        text = "Aproveitamento geral",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    text = if (state.answeredCount == 0) {
                        "Ainda sem respostas registradas."
                    } else {
                        "${state.correctCount} acerto(s) em ${state.answeredCount} resposta(s)."
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun PerformanceRow(performance: DisciplinePerformance) {
    val percentage = if (performance.total == 0) 0
    else performance.correct * 100 / performance.total

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = performance.disciplineName ?: "Sem disciplina",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "$percentage%",
                    style = MaterialTheme.typography.labelLarge,
                    color = if (percentage >= 60) SuccessColor else WarningColor
                )
            }
            Spacer(Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { percentage / 100f },
                modifier = Modifier.fillMaxWidth(),
                color = if (percentage >= 60) SuccessColor else WarningColor,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "${performance.correct} acerto(s) em ${performance.total} resposta(s)",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ErrorReasonRow(reason: ErrorReasonCount, max: Int) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = reason.errorReason?.label ?: "Não informado",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = reason.total.toString(),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.error
                )
            }
            Spacer(Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { reason.total.toFloat() / max },
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.error,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall
        )
    }
}

@Composable
private fun EmptyInfo(text: String) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Rounded.Insights,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.size(10.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
