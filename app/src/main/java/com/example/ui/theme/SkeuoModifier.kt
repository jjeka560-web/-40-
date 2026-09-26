package com.example.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

val LocalAppThemeMode = staticCompositionLocalOf { AppThemeMode.DARK_ATHLETIC }

@Composable
fun Modifier.appSurfaceStyle(
    cornerRadius: Dp = 16.dp,
    elevation: Dp = 4.dp
): Modifier {
    val themeMode = LocalAppThemeMode.current
    return when (themeMode) {
        AppThemeMode.SKEUOMORPHISM -> {
            this
                .shadow(elevation = elevation + 2.dp, shape = RoundedCornerShape(cornerRadius), ambientColor = SkeuoBevelDark, spotColor = SkeuoBevelDark)
                .clip(RoundedCornerShape(cornerRadius))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(SkeuoSurfaceRaised, SkeuoSurface)
                    )
                )
                .border(
                    width = 1.dp,
                    brush = Brush.verticalGradient(
                        colors = listOf(SkeuoBevelLight, SkeuoBevelDark)
                    ),
                    shape = RoundedCornerShape(cornerRadius)
                )
        }
        AppThemeMode.CYBERPUNK -> {
            this
                .shadow(elevation = elevation, shape = RoundedCornerShape(8.dp), spotColor = CyberNeonCyan.copy(alpha = 0.4f))
                .clip(RoundedCornerShape(8.dp))
                .background(CyberSurface)
                .border(
                    width = 1.5.dp,
                    color = CyberNeonCyan.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(8.dp)
                )
        }
        AppThemeMode.TITANIUM_LIGHT -> {
            this
                .shadow(elevation = elevation, shape = RoundedCornerShape(cornerRadius))
                .clip(RoundedCornerShape(cornerRadius))
                .background(TitaniumSurface)
                .border(
                    width = 1.dp,
                    color = TitaniumBorder,
                    shape = RoundedCornerShape(cornerRadius)
                )
        }
        AppThemeMode.DARK_ATHLETIC -> {
            this
                .shadow(elevation = elevation, shape = RoundedCornerShape(cornerRadius))
                .clip(RoundedCornerShape(cornerRadius))
                .background(AthleticSurface)
                .border(
                    width = 1.dp,
                    color = AthleticBorder,
                    shape = RoundedCornerShape(cornerRadius)
                )
        }
    }
}
