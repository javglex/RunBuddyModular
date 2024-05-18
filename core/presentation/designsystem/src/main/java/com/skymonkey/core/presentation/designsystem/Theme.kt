package com.skymonkey.core.presentation.designsystem

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

val DarkColorScheme = darkColorScheme(
    primary = RunbuddyGreen,
    background = RunbuddyBlack,
    surface = RunbuddyDarkGray,
    secondary = RunbuddyWhite,
    tertiary = RunbuddyWhite,
    primaryContainer = RunbuddyGreen30,
    onPrimary = RunbuddyBlack,
    onBackground = RunbuddyWhite,
    onSurface = RunbuddyWhite,
    onSurfaceVariant = RunbuddyGray
)

@Composable
fun RunBuddyTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect { // calls non-compose code after ever recomposition
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false // false sets icons (battery percentage, notification etc) will be shown as light icons
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}