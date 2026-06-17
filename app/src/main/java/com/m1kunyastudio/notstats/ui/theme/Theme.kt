package com.m1kunyastudio.notstats.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Color.White,
    secondary = SubTextGray,
    tertiary = AccentGray,
    background = DarkGray,
    surface = SurfaceGray,
    onPrimary = Color.Black,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = TextGray,
    onSurface = TextGray
)

private val LightColorScheme = lightColorScheme(
    primary = Color.Black,
    secondary = Gray40,
    tertiary = Gray80,
    background = Color(0xFFF0F0F0),
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onTertiary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black
)

@Composable
fun NotstatsTheme(
    themePreference: String = "system",
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val isDark = when (themePreference) {
        "light" -> false
        "dark" -> true
        else -> darkTheme
    }

    val colorScheme = if (isDark) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        val window = (view.context as Activity).window
        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !isDark
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
