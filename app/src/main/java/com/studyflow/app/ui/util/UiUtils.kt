package com.studyflow.app.ui.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Book
import androidx.compose.material.icons.rounded.Business
import androidx.compose.material.icons.rounded.Calculate
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.Gavel
import androidx.compose.material.icons.rounded.HistoryEdu
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.LocalHospital
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Psychology
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material.icons.rounded.Science
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

val disciplineIconKeys = listOf(
    "book", "science", "math", "code", "language", "history",
    "law", "health", "psychology", "business", "music", "public"
)

val disciplineColorHexList = listOf(
    "#4F46E5", "#0D9488", "#D97706", "#DC2626", "#7C3AED",
    "#2563EB", "#DB2777", "#16A34A", "#0891B2", "#9333EA"
)

fun iconForKey(key: String): ImageVector = when (key) {
    "science" -> Icons.Rounded.Science
    "math" -> Icons.Rounded.Calculate
    "code" -> Icons.Rounded.Code
    "language" -> Icons.Rounded.Language
    "history" -> Icons.Rounded.HistoryEdu
    "law" -> Icons.Rounded.Gavel
    "health" -> Icons.Rounded.LocalHospital
    "psychology" -> Icons.Rounded.Psychology
    "business" -> Icons.Rounded.Business
    "music" -> Icons.Rounded.MusicNote
    "public" -> Icons.Rounded.Public
    else -> Icons.Rounded.Book
}

fun colorFromHex(hex: String): Color = runCatching {
    Color(android.graphics.Color.parseColor(hex))
}.getOrDefault(Color(0xFF4F46E5))

fun disciplineIconLabel(key: String): String = when (key) {
    "science" -> "Ciências"
    "math" -> "Matemática"
    "code" -> "Programação"
    "language" -> "Idiomas"
    "history" -> "História"
    "law" -> "Direito"
    "health" -> "Saúde"
    "psychology" -> "Psicologia"
    "business" -> "Negócios"
    "music" -> "Música"
    "public" -> "Atualidades"
    else -> "Livro"
}

private val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
private val dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm")

fun formatDate(millis: Long): String =
    Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).format(dateFormatter)

fun formatDateTime(millis: Long): String =
    Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).format(dateTimeFormatter)

fun greetingForHour(hour: Int): String = when (hour) {
    in 5..11 -> "Bom dia"
    in 12..17 -> "Boa tarde"
    else -> "Boa noite"
}
