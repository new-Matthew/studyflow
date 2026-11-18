package com.studyflow.app.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.studyflow.app.ui.navigation.Routes
import com.studyflow.app.ui.navigation.StudyFlowBottomBar
import com.studyflow.app.ui.navigation.topLevelRoutes
import com.studyflow.app.ui.screens.disciplines.DisciplineEditScreen
import com.studyflow.app.ui.screens.disciplines.DisciplineListScreen
import com.studyflow.app.ui.screens.evolution.EvolutionScreen
import com.studyflow.app.ui.screens.home.HomeScreen
import com.studyflow.app.ui.screens.knowledge.ContentEditScreen
import com.studyflow.app.ui.screens.knowledge.MaterialEditScreen
import com.studyflow.app.ui.screens.knowledge.TipEditScreen
import com.studyflow.app.ui.screens.notebook.NoteEditScreen
import com.studyflow.app.ui.screens.notebook.NotebookScreen
import com.studyflow.app.ui.screens.questions.QuestionEditScreen
import com.studyflow.app.ui.screens.questions.QuestionListScreen
import com.studyflow.app.ui.screens.questions.QuestionResolveScreen
import com.studyflow.app.ui.screens.subjects.SubjectDetailScreen
import com.studyflow.app.ui.screens.subjects.SubjectEditScreen
import com.studyflow.app.ui.screens.subjects.SubjectListScreen

@Composable
fun StudyFlowApp() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showBottomBar = currentRoute in topLevelRoutes

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                StudyFlowBottomBar(
                    currentRoute = currentRoute,
                    onNavigate = { route -> navController.navigateTopLevel(route) }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.HOME,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Routes.HOME) {
                HomeScreen(
                    onAddDiscipline = { navController.navigate(Routes.disciplineEdit()) },
                    onAddSubject = { navController.navigateTopLevel(Routes.DISCIPLINES) },
                    onAddQuestion = { navController.navigate(Routes.questionEdit()) },
                    onAddNote = { navController.navigate(Routes.noteEdit()) },
                    onOpenDiscipline = { navController.navigateTopLevel(Routes.DISCIPLINES) },
                    onOpenQuestion = { navController.navigateTopLevel(Routes.QUESTIONS) },
                    onOpenNotebook = { navController.navigateTopLevel(Routes.NOTEBOOK) },
                    onOpenEvolution = { navController.navigateTopLevel(Routes.EVOLUTION) },
                    onOpenSubject = { navController.navigate(Routes.subjectDetail(it)) }
                )
            }

            composable(Routes.DISCIPLINES) {
                DisciplineListScreen(
                    onAddDiscipline = { navController.navigate(Routes.disciplineEdit()) },
                    onOpenDiscipline = { navController.navigate(Routes.subjects(it)) },
                    onEditDiscipline = { navController.navigate(Routes.disciplineEdit(it)) }
                )
            }

            composable(
                route = Routes.DISCIPLINE_EDIT,
                arguments = listOf(
                    navArgument("disciplineId") {
                        type = NavType.LongType
                        defaultValue = 0L
                    }
                )
            ) {
                DisciplineEditScreen(
                    onBack = { navController.popBackStack() },
                    onSaved = { navController.popBackStack() }
                )
            }

            composable(
                route = Routes.SUBJECTS,
                arguments = listOf(navArgument("disciplineId") { type = NavType.LongType })
            ) {
                SubjectListScreen(
                    onBack = { navController.popBackStack() },
                    onAddSubject = { disciplineId ->
                        navController.navigate(Routes.subjectEdit(disciplineId))
                    },
                    onOpenSubject = { navController.navigate(Routes.subjectDetail(it)) },
                    onEditSubject = { disciplineId, subjectId ->
                        navController.navigate(Routes.subjectEdit(disciplineId, subjectId))
                    }
                )
            }

            composable(
                route = Routes.SUBJECT_EDIT,
                arguments = listOf(
                    navArgument("disciplineId") {
                        type = NavType.LongType
                        defaultValue = 0L
                    },
                    navArgument("subjectId") {
                        type = NavType.LongType
                        defaultValue = 0L
                    }
                )
            ) {
                SubjectEditScreen(
                    onBack = { navController.popBackStack() },
                    onSaved = { navController.popBackStack() }
                )
            }

            composable(
                route = Routes.SUBJECT_DETAIL,
                arguments = listOf(navArgument("subjectId") { type = NavType.LongType })
            ) {
                SubjectDetailScreen(
                    onBack = { navController.popBackStack() },
                    onAddContent = { navController.navigate(Routes.contentEdit(it)) },
                    onEditContent = { subjectId, contentId ->
                        navController.navigate(Routes.contentEdit(subjectId, contentId))
                    },
                    onAddTip = { navController.navigate(Routes.tipEdit(it)) },
                    onEditTip = { subjectId, tipId ->
                        navController.navigate(Routes.tipEdit(subjectId, tipId))
                    },
                    onAddMaterial = { navController.navigate(Routes.materialEdit(it)) },
                    onEditMaterial = { subjectId, materialId ->
                        navController.navigate(Routes.materialEdit(subjectId, materialId))
                    }
                )
            }

            composable(
                route = Routes.CONTENT_EDIT,
                arguments = listOf(
                    navArgument("subjectId") { type = NavType.LongType },
                    navArgument("contentId") {
                        type = NavType.LongType
                        defaultValue = 0L
                    }
                )
            ) {
                ContentEditScreen(
                    onBack = { navController.popBackStack() },
                    onSaved = { navController.popBackStack() }
                )
            }

            composable(
                route = Routes.TIP_EDIT,
                arguments = listOf(
                    navArgument("subjectId") { type = NavType.LongType },
                    navArgument("tipId") {
                        type = NavType.LongType
                        defaultValue = 0L
                    }
                )
            ) {
                TipEditScreen(
                    onBack = { navController.popBackStack() },
                    onSaved = { navController.popBackStack() }
                )
            }

            composable(
                route = Routes.MATERIAL_EDIT,
                arguments = listOf(
                    navArgument("subjectId") { type = NavType.LongType },
                    navArgument("materialId") {
                        type = NavType.LongType
                        defaultValue = 0L
                    }
                )
            ) {
                MaterialEditScreen(
                    onBack = { navController.popBackStack() },
                    onSaved = { navController.popBackStack() }
                )
            }

            composable(Routes.QUESTIONS) {
                QuestionListScreen(
                    onCreateQuestion = { navController.navigate(Routes.questionEdit()) },
                    onEditQuestion = { navController.navigate(Routes.questionEdit(it)) },
                    onResolveQuestion = { navController.navigate(Routes.questionResolve(it)) }
                )
            }

            composable(
                route = Routes.QUESTION_EDIT,
                arguments = listOf(
                    navArgument("questionId") {
                        type = NavType.LongType
                        defaultValue = 0L
                    }
                )
            ) {
                QuestionEditScreen(
                    onBack = { navController.popBackStack() },
                    onSaved = { navController.popBackStack() }
                )
            }

            composable(
                route = Routes.QUESTION_RESOLVE,
                arguments = listOf(navArgument("questionId") { type = NavType.LongType })
            ) {
                QuestionResolveScreen(
                    onBack = { navController.popBackStack() },
                    onEdit = { navController.navigate(Routes.questionEdit(it)) }
                )
            }

            composable(Routes.NOTEBOOK) {
                NotebookScreen(
                    onCreateNote = { navController.navigate(Routes.noteEdit()) },
                    onEditNote = { navController.navigate(Routes.noteEdit(it)) }
                )
            }

            composable(
                route = Routes.NOTE_EDIT,
                arguments = listOf(
                    navArgument("noteId") {
                        type = NavType.LongType
                        defaultValue = 0L
                    }
                )
            ) {
                NoteEditScreen(
                    onBack = { navController.popBackStack() },
                    onSaved = { navController.popBackStack() }
                )
            }

            composable(Routes.EVOLUTION) {
                EvolutionScreen()
            }
        }
    }
}

private fun NavHostController.navigateTopLevel(route: String) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}
