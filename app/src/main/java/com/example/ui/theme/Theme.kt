package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

private val DarkAthleticColorScheme = darkColorScheme(
    primary = AthleticAccent,
    onPrimary = AthleticTextPrimary,
    primaryContainer = AthleticSurfaceLight,
    onPrimaryContainer = AthleticTextPrimary,
    secondary = AthleticSuccess,
    onSecondary = AthleticTextPrimary,
    tertiary = AthleticWarning,
    background = AthleticBg,
    onBackground = AthleticTextPrimary,
    surface = AthleticSurface,
    onSurface = AthleticTextPrimary,
    surfaceVariant = AthleticSurfaceLight,
    onSurfaceVariant = AthleticTextSecondary,
    outline = AthleticBorder
)

private val CyberpunkColorScheme = darkColorScheme(
    primary = CyberNeonCyan,
    onPrimary = CyberBg,
    primaryContainer = CyberSurfaceVariant,
    onPrimaryContainer = CyberNeonCyan,
    secondary = CyberNeonPink,
    onSecondary = CyberBg,
    tertiary = CyberNeonYellow,
    background = CyberBg,
    onBackground = CyberTextPrimary,
    surface = CyberSurface,
    onSurface = CyberTextPrimary,
    surfaceVariant = CyberSurfaceVariant,
    onSurfaceVariant = CyberTextSecondary,
    outline = CyberNeonCyan.copy(alpha = 0.5f)
)

private val SkeuomorphismColorScheme = darkColorScheme(
    primary = SkeuoBronzeAccent,
    onPrimary = SkeuoTextPrimary,
    primaryContainer = SkeuoSurfaceRaised,
    onPrimaryContainer = SkeuoTextPrimary,
    secondary = SkeuoSuccess,
    onSecondary = SkeuoTextPrimary,
    tertiary = SkeuoBronzeAccent,
    background = SkeuoBg,
    onBackground = SkeuoTextPrimary,
    surface = SkeuoSurface,
    onSurface = SkeuoTextPrimary,
    surfaceVariant = SkeuoSurfaceRaised,
    onSurfaceVariant = SkeuoTextSecondary,
    outline = SkeuoBevelLight
)

private val TitaniumLightColorScheme = lightColorScheme(
    primary = TitaniumAccent,
    onPrimary = TitaniumSurface,
    primaryContainer = TitaniumSurfaceRaised,
    onPrimaryContainer = TitaniumTextPrimary,
    secondary = TitaniumSuccess,
    onSecondary = TitaniumSurface,
    tertiary = TitaniumWarning,
    background = TitaniumBg,
    onBackground = TitaniumTextPrimary,
    surface = TitaniumSurface,
    onSurface = TitaniumTextPrimary,
    surfaceVariant = TitaniumSurfaceRaised,
    onSurfaceVariant = TitaniumTextSecondary,
    outline = TitaniumBorder
)

@Composable
fun KettlebellCoachTheme(
    themeMode: AppThemeMode = AppThemeMode.DARK_ATHLETIC,
    content: @Composable () -> Unit
) {
    val colorScheme = when (themeMode) {
        AppThemeMode.DARK_ATHLETIC -> DarkAthleticColorScheme
        AppThemeMode.CYBERPUNK -> CyberpunkColorScheme
        AppThemeMode.SKEUOMORPHISM -> SkeuomorphismColorScheme
        AppThemeMode.TITANIUM_LIGHT -> TitaniumLightColorScheme
    }

    CompositionLocalProvider(LocalAppThemeMode provides themeMode) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
