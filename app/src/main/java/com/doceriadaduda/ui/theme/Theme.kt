package com.doceriadaduda.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf

private val DarkColorScheme = darkColorScheme(
    primary = Primary,
    secondary = Secondary,
    tertiary = TextColor,
    background = Background,
    surface = CardBackground,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = TextColor,
    onSurface = TextColor,
)

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    secondary = Secondary,
    tertiary = TextColor,
    background = Background,
    surface = CardBackground,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = TextColor,
    onSurface = TextColor,
)

class DynamicThemeState(
    initialColor: Color = Primary,
    initialBgColor: Color = Background
) {
    var primaryColor by mutableStateOf(initialColor)
    var backgroundColor by mutableStateOf(initialBgColor)
    var companyName by mutableStateOf("Pai D’égua Hub")
    var isLoading by mutableStateOf(false)
}

val LocalDynamicThemeState = staticCompositionLocalOf { DynamicThemeState() }

@Composable
fun DoceriaDaDudaAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicThemeState: DynamicThemeState = LocalDynamicThemeState.current,
    content: @Composable () -> Unit
) {
    // A cor observada do estado
    val currentPrimary = dynamicThemeState.primaryColor
    val currentBackground = dynamicThemeState.backgroundColor
    
    // Recalcula o esquema de cores sempre que 'currentPrimary' ou 'currentBackground' mudar
    val colorScheme = remember(currentPrimary, currentBackground, darkTheme) {
        if (currentPrimary == Primary) {
            if (darkTheme) DarkColorScheme else LightColorScheme
        } else {
            if (darkTheme) {
                darkColorScheme(
                    primary = currentPrimary,
                    onPrimary = Color.White,
                    secondary = currentPrimary,
                    onSecondary = Color.White,
                    background = currentBackground,
                    surface = currentBackground.copy(alpha = 0.9f)
                )
            } else {
                lightColorScheme(
                    primary = currentPrimary,
                    onPrimary = Color.White,
                    secondary = currentPrimary,
                    onSecondary = Color.White,
                    background = currentBackground,
                    surface = Color.White,
                    primaryContainer = currentPrimary.copy(alpha = 0.12f),
                    onPrimaryContainer = currentPrimary
                )
            }
        }
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    CompositionLocalProvider(LocalDynamicThemeState provides dynamicThemeState) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
