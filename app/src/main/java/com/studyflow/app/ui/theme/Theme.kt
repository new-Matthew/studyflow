package com.studyflow.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = IndigoPrimary,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    primaryContainer = IndigoContainer,
    onPrimaryContainer = androidx.compose.ui.graphics.Color(0xFF1E1B4B),
    secondary = AccentSecondary,
    onSecondary = androidx.compose.ui.graphics.Color.White,
    secondaryContainer = androidx.compose.ui.graphics.Color(0xFFCCFBF1),
    onSecondaryContainer = androidx.compose.ui.graphics.Color(0xFF134E4A),
    tertiary = AccentTertiary,
    onTertiary = androidx.compose.ui.graphics.Color.White,
    tertiaryContainer = androidx.compose.ui.graphics.Color(0xFFFEF3C7),
    onTertiaryContainer = androidx.compose.ui.graphics.Color(0xFF78350F),
    background = androidx.compose.ui.graphics.Color(0xFFF8FAFC),
    onBackground = androidx.compose.ui.graphics.Color(0xFF0F172A),
    surface = androidx.compose.ui.graphics.Color(0xFFFFFFFF),
    onSurface = androidx.compose.ui.graphics.Color(0xFF0F172A),
    surfaceVariant = androidx.compose.ui.graphics.Color(0xFFF1F5F9),
    onSurfaceVariant = androidx.compose.ui.graphics.Color(0xFF475569),
    error = DangerColor,
    onError = androidx.compose.ui.graphics.Color.White
)

private val DarkColors = darkColorScheme(
    primary = IndigoPrimaryDark,
    onPrimary = androidx.compose.ui.graphics.Color(0xFF1E1B4B),
    primaryContainer = IndigoContainerDark,
    onPrimaryContainer = androidx.compose.ui.graphics.Color(0xFFE0E7FF),
    secondary = AccentSecondaryDark,
    onSecondary = androidx.compose.ui.graphics.Color(0xFF134E4A),
    secondaryContainer = androidx.compose.ui.graphics.Color(0xFF115E59),
    onSecondaryContainer = androidx.compose.ui.graphics.Color(0xFFCCFBF1),
    tertiary = AccentTertiaryDark,
    onTertiary = androidx.compose.ui.graphics.Color(0xFF78350F),
    tertiaryContainer = androidx.compose.ui.graphics.Color(0xFF92400E),
    onTertiaryContainer = androidx.compose.ui.graphics.Color(0xFFFEF3C7),
    background = androidx.compose.ui.graphics.Color(0xFF0B1120),
    onBackground = androidx.compose.ui.graphics.Color(0xFFE2E8F0),
    surface = androidx.compose.ui.graphics.Color(0xFF111827),
    onSurface = androidx.compose.ui.graphics.Color(0xFFE2E8F0),
    surfaceVariant = androidx.compose.ui.graphics.Color(0xFF1E293B),
    onSurfaceVariant = androidx.compose.ui.graphics.Color(0xFF94A3B8),
    error = androidx.compose.ui.graphics.Color(0xFFF87171),
    onError = androidx.compose.ui.graphics.Color(0xFF450A0A)
)

@Composable
fun StudyFlowTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = StudyFlowTypography,
        content = content
    )
}
