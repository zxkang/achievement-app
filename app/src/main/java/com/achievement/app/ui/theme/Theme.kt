package com.achievement.app.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val PSNDarkColorScheme = darkColorScheme(
    primary = PSNBlue,
    onPrimary = Color.White,
    primaryContainer = PSNBlueDark,
    onPrimaryContainer = PSNBlueLight,
    secondary = PSNBlueLight,
    onSecondary = Color.White,
    secondaryContainer = PSNSurfaceVariant,
    onSecondaryContainer = PSNOnSurface,
    tertiary = Gold,
    background = PSNBackground,
    onBackground = PSNOnSurface,
    surface = PSNSurface,
    onSurface = PSNOnSurface,
    surfaceVariant = PSNSurfaceVariant,
    onSurfaceVariant = PSNOnSurfaceVariant,
    outline = Color(0xFF3A3A5C),
    error = Error,
    onError = Color.White
)

private val PSNLightColorScheme = lightColorScheme(
    primary = PSNBlue,
    onPrimary = Color.White,
    primaryContainer = PSNBlueLight,
    onPrimaryContainer = Color.White,
    secondary = PSNBlueDark,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE8F0FE),
    onSecondaryContainer = PSNBlueDark,
    tertiary = Gold,
    background = Color(0xFFF5F5F8),
    onBackground = Color(0xFF1A1A2E),
    surface = Color.White,
    onSurface = Color(0xFF1A1A2E),
    surfaceVariant = Color(0xFFE8F0FE),
    onSurfaceVariant = Color(0xFF5A5A7A),
    outline = Color(0xFFD0D0E0),
    error = Error,
    onError = Color.White
)

@Composable
fun AchievementAppTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) PSNDarkColorScheme else PSNLightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = PSNBackground.toArgb()
            window.navigationBarColor = PSNBackground.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
