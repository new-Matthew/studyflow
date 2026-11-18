package com.studyflow.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.EditNote
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Insights
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material.icons.rounded.Quiz
import androidx.compose.ui.graphics.vector.ImageVector

object Routes {
    const val HOME = "home"
    const val DISCIPLINES = "disciplines"
    const val QUESTIONS = "questions"
    const val NOTEBOOK = "notebook"
    const val EVOLUTION = "evolution"

    const val DISCIPLINE_EDIT = "discipline_edit?disciplineId={disciplineId}"
    const val SUBJECTS = "subjects/{disciplineId}"
    const val SUBJECT_EDIT = "subject_edit?disciplineId={disciplineId}&subjectId={subjectId}"
    const val SUBJECT_DETAIL = "subject_detail/{subjectId}"
    const val CONTENT_EDIT = "content_edit?subjectId={subjectId}&contentId={contentId}"
    const val TIP_EDIT = "tip_edit?subjectId={subjectId}&tipId={tipId}"
    const val MATERIAL_EDIT = "material_edit?subjectId={subjectId}&materialId={materialId}"
    const val QUESTION_EDIT = "question_edit?questionId={questionId}"
    const val QUESTION_RESOLVE = "question_resolve/{questionId}"
    const val NOTE_EDIT = "note_edit?noteId={noteId}"

    fun disciplineEdit(id: Long = 0L) = "discipline_edit?disciplineId=$id"
    fun subjects(disciplineId: Long) = "subjects/$disciplineId"
    fun subjectEdit(disciplineId: Long, subjectId: Long = 0L) =
        "subject_edit?disciplineId=$disciplineId&subjectId=$subjectId"
    fun subjectDetail(subjectId: Long) = "subject_detail/$subjectId"
    fun contentEdit(subjectId: Long, contentId: Long = 0L) =
        "content_edit?subjectId=$subjectId&contentId=$contentId"
    fun tipEdit(subjectId: Long, tipId: Long = 0L) =
        "tip_edit?subjectId=$subjectId&tipId=$tipId"
    fun materialEdit(subjectId: Long, materialId: Long = 0L) =
        "material_edit?subjectId=$subjectId&materialId=$materialId"
    fun questionEdit(questionId: Long = 0L) = "question_edit?questionId=$questionId"
    fun questionResolve(questionId: Long) = "question_resolve/$questionId"
    fun noteEdit(noteId: Long = 0L) = "note_edit?noteId=$noteId"
}

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(Routes.HOME, "Início", Icons.Rounded.Home),
    BottomNavItem(Routes.DISCIPLINES, "Disciplinas", Icons.Rounded.MenuBook),
    BottomNavItem(Routes.QUESTIONS, "Questões", Icons.Rounded.Quiz),
    BottomNavItem(Routes.NOTEBOOK, "Caderno", Icons.Rounded.EditNote),
    BottomNavItem(Routes.EVOLUTION, "Evolução", Icons.Rounded.Insights)
)

val topLevelRoutes = bottomNavItems.map { it.route }.toSet()
