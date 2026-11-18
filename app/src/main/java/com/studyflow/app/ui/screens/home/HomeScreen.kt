package com.studyflow.app.ui.screens.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.EditNote
import androidx.compose.material.icons.rounded.Insights
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material.icons.rounded.Quiz
import androidx.compose.material.icons.rounded.Topic
import androidx.compose.material.icons.rounded.TrackChanges
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.studyflow.app.data.local.relation.AnswerRecordWithQuestion
import com.studyflow.app.data.local.relation.SubjectWithDiscipline
import com.studyflow.app.ui.AppViewModelProvider
import com.studyflow.app.ui.components.LearningStatusChip
import com.studyflow.app.ui.components.SectionTitle
import com.studyflow.app.ui.components.StatCard
import com.studyflow.app.ui.components.StudyFlowTopBar
import com.studyflow.app.ui.theme.SuccessColor
import com.studyflow.app.ui.util.formatDateTime
import com.studyflow.app.ui.util.greetingForHour
import java.time.LocalTime

@Composable
fun HomeScreen(
    onAddDiscipline: () -> Unit,
    onAddSubject: () -> Unit,
    onAddQuestion: () -> Unit,
    onAddNote: () -> Unit,
    onOpenDiscipline: () -> Unit,
    onOpenQuestion: () -> Unit,
    onOpenNotebook: () -> Unit,
    onOpenEvolution: () -> Unit,
    onOpenSubject: (Long) -> Unit,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val greeting = remember { greetingForHour(LocalTime.now().hour) }

    Column(modifier = Modifier.fillMaxSize()) {
        StudyFlowTopBar(
            title = "StudyFlow",
            subtitle = "Seu painel de estudos"
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            HeroCard(greeting = greeting, accuracy = state.accuracy, answered = state.answeredCount)

            SectionTitle("Acesso rápido")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickAction(Icons.Rounded.MenuBook, "Disciplina", onAddDiscipline, Modifier.weight(1f))
                QuickAction(Icons.Rounded.Topic, "Assunto", onAddSubject, Modifier.weight(1f))
                QuickAction(Icons.Rounded.Quiz, "Questão", onAddQuestion, Modifier.weight(1f))
                QuickAction(Icons.Rounded.EditNote, "Anotação", onAddNote, Modifier.weight(1f))
            }

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
                    value = state.answeredCount.toString(),
                    label = "Respondidas",
                    icon = Icons.Rounded.Quiz,
                    accent = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    value = "${state.accuracy}%",
                    label = "Acertos",
                    icon = Icons.Rounded.TrackChanges,
                    accent = SuccessColor,
                    modifier = Modifier.weight(1f)
                )
            }

            SectionTitle(
                title = "Precisa de revisão",
                actionLabel = "Ver evolução",
                onAction = onOpenEvolution
            )
            if (state.subjectsToReview.isEmpty()) {
                InfoCard(
                    text = "Nenhum assunto marcado para revisão. Continue avançando!",
                    icon = Icons.Rounded.CheckCircle,
                    tint = SuccessColor
                )
            } else {
                state.subjectsToReview.take(3).forEach { item ->
                    ReviewCard(item = item, onClick = { onOpenSubject(item.subject.id) })
                }
            }

            SectionTitle(
                title = "Atividades recentes",
                actionLabel = "Questões",
                onAction = onOpenQuestion
            )
            if (state.recentAnswers.isEmpty()) {
                InfoCard(
                    text = "Responda sua primeira questão para começar a acompanhar seu desempenho.",
                    icon = Icons.Rounded.Insights,
                    tint = MaterialTheme.colorScheme.primary
                )
            } else {
                state.recentAnswers.take(5).forEach { record ->
                    RecentAnswerRow(record = record)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                LinkCard(
                    title = "Disciplinas",
                    icon = Icons.Rounded.MenuBook,
                    onClick = onOpenDiscipline,
                    modifier = Modifier.weight(1f)
                )
                LinkCard(
                    title = "Caderno",
                    icon = Icons.Rounded.EditNote,
                    onClick = onOpenNotebook,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun HeroCard(greeting: String, accuracy: Int, answered: Int) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.secondary
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Column {
                Text(
                    text = "$greeting, estudante!",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = if (answered == 0) {
                        "Organize seus estudos e comece sua jornada de aprendizado."
                    } else {
                        "Você já respondeu $answered questão(ões) com $accuracy% de acertos."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }
    }
}

@Composable
private fun QuickAction(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(Modifier.height(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun InfoCard(text: String, icon: ImageVector, tint: Color) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = tint)
            Spacer(Modifier.width(10.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ReviewCard(item: SubjectWithDiscipline, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.subject.name,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = item.disciplineName,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            LearningStatusChip(status = item.subject.status)
        }
    }
}

@Composable
private fun RecentAnswerRow(record: AnswerRecordWithQuestion) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (record.record.isCorrect) {
                    Icons.Rounded.CheckCircle
                } else {
                    Icons.Rounded.Cancel
                },
                contentDescription = null,
                tint = if (record.record.isCorrect) SuccessColor else MaterialTheme.colorScheme.error,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = record.statement,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = formatDateTime(record.record.answeredAt),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun LinkCard(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(8.dp))
            Text(text = title, style = MaterialTheme.typography.titleSmall)
        }
    }
}
