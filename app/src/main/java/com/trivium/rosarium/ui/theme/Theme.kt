package com.trivium.rosarium.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val RosariumDarkColorScheme = darkColorScheme(
    primary = MedievalGoldBright,
    onPrimary = Color(0xFF0D1926),
    primaryContainer = Color(0xFF132235),
    onPrimaryContainer = Color(0xFFF7F3EA),
    secondary = UltramarineLapis,
    onSecondary = Color(0xFFF7F3EA),
    tertiary = OxbloodRed,
    background = Color(0xFF09121D),
    onBackground = Color(0xFFF7F3EA),
    surface = Color(0xFF0F1A26),
    onSurface = Color(0xFFF7F3EA),
    surfaceVariant = Color(0xFF162536),
    onSurfaceVariant = Color(0xFF9FB2C8)
)

private val RosariumLightColorScheme = lightColorScheme(
    primary = MedievalGold,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFF4ECD8),
    onPrimaryContainer = Color(0xFF1E2836),
    secondary = UltramarineLapis,
    onSecondary = Color.White,
    tertiary = OxbloodRed,
    background = Color(0xFFF7F3EA),
    onBackground = Color(0xFF1E2836),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1E2836),
    surfaceVariant = Color(0xFFF4ECD8),
    onSurfaceVariant = Color(0xFF6B7A8D)
)

@Composable
fun RosariumTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) RosariumDarkColorScheme else RosariumLightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            window?.let {
                WindowCompat.setDecorFitsSystemWindows(it, false)
                val insetsController = WindowCompat.getInsetsController(it, view)
                insetsController.isAppearanceLightStatusBars = !darkTheme
                insetsController.isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = RosariumTypography,
        content = content
    )
}
