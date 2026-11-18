package com.studyflow.app.ui

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.studyflow.app.StudyFlowApplication
import com.studyflow.app.ui.screens.disciplines.DisciplineEditViewModel
import com.studyflow.app.ui.screens.disciplines.DisciplineListViewModel
import com.studyflow.app.ui.screens.evolution.EvolutionViewModel
import com.studyflow.app.ui.screens.home.HomeViewModel
import com.studyflow.app.ui.screens.knowledge.ContentEditViewModel
import com.studyflow.app.ui.screens.knowledge.MaterialEditViewModel
import com.studyflow.app.ui.screens.knowledge.TipEditViewModel
import com.studyflow.app.ui.screens.notebook.NoteEditViewModel
import com.studyflow.app.ui.screens.notebook.NotebookViewModel
import com.studyflow.app.ui.screens.questions.QuestionEditViewModel
import com.studyflow.app.ui.screens.questions.QuestionListViewModel
import com.studyflow.app.ui.screens.questions.QuestionResolveViewModel
import com.studyflow.app.ui.screens.subjects.SubjectDetailViewModel
import com.studyflow.app.ui.screens.subjects.SubjectEditViewModel
import com.studyflow.app.ui.screens.subjects.SubjectListViewModel

object AppViewModelProvider {

    val Factory = viewModelFactory {
        initializer {
            HomeViewModel(app().container.statsRepository)
        }
        initializer {
            EvolutionViewModel(app().container.statsRepository)
        }
        initializer {
            DisciplineListViewModel(app().container.disciplineRepository)
        }
        initializer {
            DisciplineEditViewModel(
                app().container.disciplineRepository,
                createSavedStateHandle()
            )
        }
        initializer {
            SubjectListViewModel(
                app().container.subjectRepository,
                app().container.disciplineRepository,
                createSavedStateHandle()
            )
        }
        initializer {
            SubjectEditViewModel(
                app().container.subjectRepository,
                createSavedStateHandle()
            )
        }
        initializer {
            SubjectDetailViewModel(
                app().container.subjectRepository,
                app().container.knowledgeRepository,
                createSavedStateHandle()
            )
        }
        initializer {
            ContentEditViewModel(
                app().container.knowledgeRepository,
                createSavedStateHandle()
            )
        }
        initializer {
            TipEditViewModel(
                app().container.knowledgeRepository,
                createSavedStateHandle()
            )
        }
        initializer {
            MaterialEditViewModel(
                app().container.knowledgeRepository,
                createSavedStateHandle()
            )
        }
        initializer {
            QuestionListViewModel(
                app().container.questionRepository,
                app().container.disciplineRepository,
                app().container.subjectRepository
            )
        }
        initializer {
            QuestionEditViewModel(
                app().container.questionRepository,
                app().container.disciplineRepository,
                app().container.subjectRepository,
                createSavedStateHandle()
            )
        }
        initializer {
            QuestionResolveViewModel(
                app().container.questionRepository,
                createSavedStateHandle()
            )
        }
        initializer {
            NotebookViewModel(app().container.notebookRepository)
        }
        initializer {
            NoteEditViewModel(
                app().container.notebookRepository,
                app().container.subjectRepository,
                createSavedStateHandle()
            )
        }
    }
}

private fun CreationExtras.app(): StudyFlowApplication =
    this[APPLICATION_KEY] as StudyFlowApplication
