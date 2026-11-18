package com.studyflow.app.ui.screens.questions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.studyflow.app.data.model.Difficulty
import com.studyflow.app.ui.AppViewModelProvider
import com.studyflow.app.ui.components.LabeledDropdown
import com.studyflow.app.ui.components.StudyFlowTopBar

@Composable
fun QuestionEditScreen(
    onBack: () -> Unit,
    onSaved: () -> Unit,
    viewModel: QuestionEditViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val disciplines by viewModel.disciplines.collectAsStateWithLifecycle()
    val subjects by viewModel.subjects.collectAsStateWithLifecycle()

    val disciplineOptions = listOf("Sem disciplina") + disciplines.map { it.discipline.name }
    val selectedDiscipline = disciplines
        .firstOrNull { it.discipline.id == state.disciplineId }
        ?.discipline?.name ?: "Sem disciplina"

    val subjectOptions = listOf("Sem assunto") + subjects.map { it.name }
    val selectedSubject = subjects.firstOrNull { it.id == state.subjectId }?.name ?: "Sem assunto"

    Column(modifier = Modifier.fillMaxSize()) {
        StudyFlowTopBar(
            title = if (state.isEditing) "Editar questão" else "Nova questão",
            onBack = onBack
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            OutlinedTextField(
                value = state.statement,
                onValueChange = viewModel::onStatementChange,
                label = { Text("Enunciado") },
                isError = state.error != null,
                supportingText = { state.error?.let { Text(it) } },
                minLines = 4,
                modifier = Modifier.fillMaxWidth()
            )

            Text("Alternativas", style = MaterialTheme.typography.titleSmall)
            listOf("A", "B", "C", "D", "E").forEach { letter ->
                val value = when (letter) {
                    "A" -> state.optionA
                    "B" -> state.optionB
                    "C" -> state.optionC
                    "D" -> state.optionD
                    else -> state.optionE
                }
                OutlinedTextField(
                    value = value,
                    onValueChange = { viewModel.onOptionChange(letter, it) },
                    label = { Text("Alternativa $letter") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            LabeledDropdown(
                label = "Gabarito",
                options = listOf("A", "B", "C", "D", "E"),
                selected = state.correctAnswer,
                onSelected = viewModel::onCorrectAnswerChange
            )

            OutlinedTextField(
                value = state.comment,
                onValueChange = viewModel::onCommentChange,
                label = { Text("Comentário da questão") },
                minLines = 3,
                modifier = Modifier.fillMaxWidth()
            )

            LabeledDropdown(
                label = "Disciplina",
                options = disciplineOptions,
                selected = selectedDiscipline,
                onSelected = { name ->
                    viewModel.onDisciplineChange(
                        if (name == "Sem disciplina") null
                        else disciplines.firstOrNull { it.discipline.name == name }?.discipline?.id
                    )
                }
            )

            LabeledDropdown(
                label = "Assunto",
                options = subjectOptions,
                selected = selectedSubject,
                onSelected = { name ->
                    viewModel.onSubjectChange(
                        if (name == "Sem assunto") null
                        else subjects.firstOrNull { it.name == name }?.id
                    )
                }
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = state.board,
                    onValueChange = viewModel::onBoardChange,
                    label = { Text("Banca") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = state.year,
                    onValueChange = viewModel::onYearChange,
                    label = { Text("Ano") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }

            OutlinedTextField(
                value = state.source,
                onValueChange = viewModel::onSourceChange,
                label = { Text("Fonte") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            LabeledDropdown(
                label = "Nível de dificuldade",
                options = Difficulty.entries.map { it.label },
                selected = state.difficulty.label,
                onSelected = { label ->
                    Difficulty.entries.firstOrNull { it.label == label }
                        ?.let(viewModel::onDifficultyChange)
                }
            )

            Button(
                onClick = { viewModel.save(onSaved) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (state.isEditing) "Salvar alterações" else "Salvar questão")
            }

            TextButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
                Text("Cancelar")
            }
        }
    }
}
