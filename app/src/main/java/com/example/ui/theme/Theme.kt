package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = DompetCyanAccent,
    onPrimary = Color.Black,
    primaryContainer = DompetBlueDark,
    onPrimaryContainer = Color.White,
    secondary = DompetGoldReward,
    onSecondary = Color.Black,
    tertiary = DompetGreenSuccess,
    background = DompetBackgroundDark,
    surface = DompetSurfaceDark,
    surfaceVariant = DompetSurfaceVariantDark,
    onBackground = DompetTextPrimaryDark,
    onSurface = DompetTextPrimaryDark,
    onSurfaceVariant = DompetTextSecondaryDark
)

private val LightColorScheme = lightColorScheme(
    primary = DompetBluePrimary,
    onPrimary = Color.White,
    primaryContainer = DompetBlueLight,
    onPrimaryContainer = DompetBlueDark,
    secondary = DompetGoldReward,
    onSecondary = Color.Black,
    tertiary = DompetGreenSuccess,
    background = DompetBackgroundLight,
    surface = DompetSurfaceLight,
    surfaceVariant = DompetBorderLight,
    onBackground = DompetTextPrimary,
    onSurface = DompetTextPrimary,
    onSurfaceVariant = DompetTextSecondary
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep branded fintech look by default
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
