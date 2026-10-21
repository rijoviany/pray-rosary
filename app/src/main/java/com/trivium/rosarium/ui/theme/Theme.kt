package com.trivium.rosarium.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val RosariumColorScheme = darkColorScheme(
    primary = MedievalGold,
    onPrimary = InkBrownDark,
    primaryContainer = MedievalGoldDark,
    onPrimaryContainer = ParchmentLight,
    secondary = UltramarineLapis,
    onSecondary = ParchmentLight,
    tertiary = OxbloodRed,
    background = BackgroundVignette,
    onBackground = ParchmentLight,
    surface = BackgroundSurface,
    onSurface = ParchmentBase,
    surfaceVariant = InkBrownDark,
    onSurfaceVariant = ParchmentDark
)

@Composable
fun RosariumTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            window?.let {
                WindowCompat.setDecorFitsSystemWindows(it, false)
                it.statusBarColor = android.graphics.Color.TRANSPARENT
                it.navigationBarColor = android.graphics.Color.TRANSPARENT
            }
        }
    }

    MaterialTheme(
        colorScheme = RosariumColorScheme,
        typography = RosariumTypography,
        content = content
    )
}
