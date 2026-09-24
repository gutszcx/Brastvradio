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

enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK,
    AMOLED
}

private val DarkColorScheme = darkColorScheme(
    primary = PrimeBlue,
    onPrimary = Color.Black,
    primaryContainer = PrimeBlueDark,
    onPrimaryContainer = PrimeBlueLight,
    secondary = PrimeGold,
    onSecondary = Color.Black,
    tertiary = BrasilGreen,
    background = PrimeNavy,
    surface = PrimeSurface,
    surfaceVariant = PrimeSurfaceCard,
    onBackground = PrimeTextWhite,
    onSurface = PrimeTextWhite,
    onSurfaceVariant = PrimeTextMuted,
    outline = PrimeSurfaceBorder,
    error = LiveRed
)

private val AmoledColorScheme = darkColorScheme(
    primary = BrasilGreen,
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF003822),
    onPrimaryContainer = BrasilGreenLight,
    secondary = BrasilYellow,
    onSecondary = Color.Black,
    tertiary = BrasilBlueLight,
    background = AmoledBackground,
    surface = AmoledSurface,
    surfaceVariant = AmoledSurfaceVariant,
    onBackground = Color(0xFFFFFFFF),
    onSurface = Color(0xFFFFFFFF),
    onSurfaceVariant = Color(0xFFAAAAAA),
    outline = AmoledBorder,
    error = LiveRed
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF008938),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD4F8E0),
    onPrimaryContainer = Color(0xFF00210A),
    secondary = Color(0xFFB57D00),
    onSecondary = Color.White,
    tertiary = BrasilBlueDark,
    background = LightBackground,
    surface = LightSurface,
    surfaceVariant = LightSurfaceVariant,
    onBackground = Color(0xFF1F2328),
    onSurface = Color(0xFF1F2328),
    onSurfaceVariant = Color(0xFF57606A),
    outline = LightBorder,
    error = LiveRed
)

@Composable
fun BrasilTvTheme(
    themeMode: ThemeMode = ThemeMode.DARK,
    highContrast: Boolean = false,
    content: @Composable () -> Unit
) {
    val systemInDark = isSystemInDarkTheme()
    val isDark = when (themeMode) {
        ThemeMode.SYSTEM -> systemInDark
        ThemeMode.LIGHT -> false
        ThemeMode.DARK, ThemeMode.AMOLED -> true
    }

    val baseScheme = when (themeMode) {
        ThemeMode.AMOLED -> AmoledColorScheme
        ThemeMode.LIGHT -> LightColorScheme
        ThemeMode.DARK -> DarkColorScheme
        ThemeMode.SYSTEM -> if (systemInDark) DarkColorScheme else LightColorScheme
    }

    val finalScheme = if (highContrast) {
        if (isDark) {
            baseScheme.copy(
                background = Color.Black,
                surface = Color(0xFF101010),
                outline = Color.White,
                onSurface = Color.White,
                onBackground = Color.White
            )
        } else {
            baseScheme.copy(
                background = Color.White,
                surface = Color.White,
                outline = Color.Black,
                onSurface = Color.Black,
                onBackground = Color.Black
            )
        }
    } else {
        baseScheme
    }

    MaterialTheme(
        colorScheme = finalScheme,
        typography = Typography,
        content = content
    )
}
