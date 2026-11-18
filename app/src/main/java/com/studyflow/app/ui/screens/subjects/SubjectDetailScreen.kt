package com.studyflow.app.ui.screens.subjects

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material.icons.rounded.Link
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material.icons.rounded.OpenInNew
import androidx.compose.material.icons.rounded.Topic
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.studyflow.app.data.local.entity.ContentEntity
import com.studyflow.app.data.local.entity.MaterialEntity
import com.studyflow.app.data.local.entity.TipEntity
import com.studyflow.app.data.model.LearningStatus
import com.studyflow.app.data.model.MaterialType
import com.studyflow.app.data.model.TipType
import com.studyflow.app.ui.AppViewModelProvider
import com.studyflow.app.ui.components.ConfirmDeleteDialog
import com.studyflow.app.ui.components.EmptyState
import com.studyflow.app.ui.components.LabeledDropdown
import com.studyflow.app.ui.components.LearningStatusChip
import com.studyflow.app.ui.components.StudyFlowTopBar

private val tabs = listOf("Conteúdos", "Dicas", "Materiais")

@Composable
fun SubjectDetailScreen(
    onBack: () -> Unit,
    onAddContent: (Long) -> Unit,
    onEditContent: (Long, Long) -> Unit,
    onAddTip: (Long) -> Unit,
    onEditTip: (Long, Long) -> Unit,
    onAddMaterial: (Long) -> Unit,
    onEditMaterial: (Long, Long) -> Unit,
    viewModel: SubjectDetailViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val subject by viewModel.subject.collectAsStateWithLifecycle()
    val contents by viewModel.contents.collectAsStateWithLifecycle()
    val tips by viewModel.tips.collectAsStateWithLifecycle()
    val materials by viewModel.materials.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableIntStateOf(0) }
    var pendingContent by remember { mutableStateOf<ContentEntity?>(null) }
    var pendingTip by remember { mutableStateOf<TipEntity?>(null) }
    var pendingMaterial by remember { mutableStateOf<MaterialEntity?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        StudyFlowTopBar(
            title = subject?.name ?: "Assunto",
            subtitle = "Área de conhecimento",
            onBack = onBack
        )

        subject?.let { current ->
            SubjectOverviewCard(
                status = current.status,
                progress = current.progress,
                onStatusChange = viewModel::updateStatus,
                onProgressChange = viewModel::updateProgress
            )
        }

        TabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { index, tab ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(tab) }
                )
            }
        }

        Box(modifier = Modifier.weight(1f)) {
            when (selectedTab) {
                0 -> ContentTab(
                    contents = contents,
                    onAdd = { onAddContent(viewModel.subjectId) },
                    onEdit = { onEditContent(viewModel.subjectId, it.id) },
                    onDelete = { pendingContent = it }
                )
                1 -> TipTab(
                    tips = tips,
                    onAdd = { onAddTip(viewModel.subjectId) },
                    onEdit = { onEditTip(viewModel.subjectId, it.id) },
                    onDelete = { pendingTip = it }
                )
                else -> MaterialTab(
                    materials = materials,
                    onAdd = { onAddMaterial(viewModel.subjectId) },
                    onEdit = { onEditMaterial(viewModel.subjectId, it.id) },
                    onDelete = { pendingMaterial = it }
                )
            }
        }
    }

    pendingContent?.let { content ->
        ConfirmDeleteDialog(
            title = "Excluir conteúdo",
            message = "Remover \"${content.title}\"?",
            onConfirm = { viewModel.deleteContent(content) },
            onDismiss = { pendingContent = null }
        )
    }
    pendingTip?.let { tip ->
        ConfirmDeleteDialog(
            title = "Excluir dica",
            message = "Remover \"${tip.title}\"?",
            onConfirm = { viewModel.deleteTip(tip) },
            onDismiss = { pendingTip = null }
        )
    }
    pendingMaterial?.let { material ->
        ConfirmDeleteDialog(
            title = "Excluir material",
            message = "Remover \"${material.title}\"?",
            onConfirm = { viewModel.deleteMaterial(material) },
            onDismiss = { pendingMaterial = null }
        )
    }
}

@Composable
private fun SubjectOverviewCard(
    status: LearningStatus,
    progress: Int,
    onStatusChange: (LearningStatus) -> Unit,
    onProgressChange: (Int) -> Unit
) {
    var localProgress by remember(progress) { mutableFloatStateOf(progress.toFloat()) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Situação do aprendizado",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.weight(1f)
                )
                LearningStatusChip(status = status)
            }
            Spacer(Modifier.height(8.dp))
            LabeledDropdown(
                label = "Alterar situação",
                options = LearningStatus.entries.map { it.label },
                selected = status.label,
                onSelected = { label ->
                    LearningStatus.entries.firstOrNull { it.label == label }?.let(onStatusChange)
                }
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = "Progresso: ${localProgress.toInt()}%",
                style = MaterialTheme.typography.titleSmall
            )
            LinearProgressIndicator(
                progress = { localProgress / 100f },
                modifier = Modifier.fillMaxWidth(),
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
            Slider(
                value = localProgress,
                onValueChange = { localProgress = it },
                onValueChangeFinished = { onProgressChange(localProgress.toInt()) },
                valueRange = 0f..100f,
                steps = 19
            )
        }
    }
}

@Composable
private fun TabHeader(count: Int, label: String, onAdd: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$count $label",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        TextButton(onClick = onAdd) {
            Icon(Icons.Rounded.Add, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(4.dp))
            Text("Adicionar")
        }
    }
}

@Composable
private fun ContentTab(
    contents: List<ContentEntity>,
    onAdd: () -> Unit,
    onEdit: (ContentEntity) -> Unit,
    onDelete: (ContentEntity) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        TabHeader(contents.size, "conteúdo(s)", onAdd)
        if (contents.isEmpty()) {
            EmptyState(
                icon = Icons.Rounded.MenuBook,
                title = "Sem conteúdos",
                message = "Registre explicações, exemplos e tópicos deste assunto.",
                actionLabel = "Criar conteúdo",
                onAction = onAdd
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(contents, key = { it.id }) { content ->
                    ContentCard(content, onEdit, onDelete)
                }
            }
        }
    }
}

@Composable
private fun ContentCard(
    content: ContentEntity,
    onEdit: (ContentEntity) -> Unit,
    onDelete: (ContentEntity) -> Unit
) {
    Card(
        onClick = { onEdit(content) },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = content.title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                IconButton(onClick = { onEdit(content) }) {
                    Icon(Icons.Rounded.Edit, contentDescription = "Editar conteúdo")
                }
                IconButton(onClick = { onDelete(content) }) {
                    Icon(
                        Icons.Rounded.DeleteOutline,
                        contentDescription = "Excluir conteúdo",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
            if (content.topics.isNotBlank()) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(horizontal = 10.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = content.topics,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(Modifier.height(6.dp))
            }
            if (content.explanation.isNotBlank()) {
                Text(
                    text = content.explanation,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun TipTab(
    tips: List<TipEntity>,
    onAdd: () -> Unit,
    onEdit: (TipEntity) -> Unit,
    onDelete: (TipEntity) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        TabHeader(tips.size, "dica(s)", onAdd)
        if (tips.isEmpty()) {
            EmptyState(
                icon = Icons.Rounded.Lightbulb,
                title = "Sem dicas",
                message = "Destaque conceitos, pegadinhas de prova e observações importantes.",
                actionLabel = "Criar dica",
                onAction = onAdd
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(tips, key = { it.id }) { tip ->
                    TipCard(tip, onEdit, onDelete)
                }
            }
        }
    }
}

@Composable
private fun TipCard(
    tip: TipEntity,
    onEdit: (TipEntity) -> Unit,
    onDelete: (TipEntity) -> Unit
) {
    Card(
        onClick = { onEdit(tip) },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                TypeBadge(tip.type.label, MaterialTheme.colorScheme.tertiary)
                Spacer(Modifier.width(8.dp))
                Text(
                    text = tip.title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                IconButton(onClick = { onEdit(tip) }) {
                    Icon(Icons.Rounded.Edit, contentDescription = "Editar dica")
                }
                IconButton(onClick = { onDelete(tip) }) {
                    Icon(
                        Icons.Rounded.DeleteOutline,
                        contentDescription = "Excluir dica",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
            if (tip.description.isNotBlank()) {
                Text(
                    text = tip.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun MaterialTab(
    materials: List<MaterialEntity>,
    onAdd: () -> Unit,
    onEdit: (MaterialEntity) -> Unit,
    onDelete: (MaterialEntity) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        TabHeader(materials.size, "material(is)", onAdd)
        if (materials.isEmpty()) {
            EmptyState(
                icon = Icons.Rounded.Link,
                title = "Sem materiais",
                message = "Salve links, referências e materiais complementares.",
                actionLabel = "Criar material",
                onAction = onAdd
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(materials, key = { it.id }) { material ->
                    MaterialCard(material, onEdit, onDelete)
                }
            }
        }
    }
}

@Composable
private fun MaterialCard(
    material: MaterialEntity,
    onEdit: (MaterialEntity) -> Unit,
    onDelete: (MaterialEntity) -> Unit
) {
    val context = LocalContext.current
    Card(
        onClick = { onEdit(material) },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                TypeBadge(material.type.label, MaterialTheme.colorScheme.secondary)
                Spacer(Modifier.width(8.dp))
                Text(
                    text = material.title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                IconButton(onClick = { onEdit(material) }) {
                    Icon(Icons.Rounded.Edit, contentDescription = "Editar material")
                }
                IconButton(onClick = { onDelete(material) }) {
                    Icon(
                        Icons.Rounded.DeleteOutline,
                        contentDescription = "Excluir material",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
            if (material.description.isNotBlank()) {
                Text(
                    text = material.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            if (material.url.isNotBlank()) {
                Spacer(Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .clickable {
                            runCatching {
                                context.startActivity(
                                    Intent(Intent.ACTION_VIEW, Uri.parse(material.url))
                                )
                            }
                        }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Rounded.OpenInNew,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = material.url,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
private fun TypeBadge(label: String, color: androidx.compose.ui.graphics.Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(color.copy(alpha = 0.14f))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Medium
        )
    }
}
