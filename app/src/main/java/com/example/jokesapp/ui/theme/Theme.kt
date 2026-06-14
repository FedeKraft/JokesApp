package com.example.jokesapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary              = BrandGold,
    onPrimary            = Color(0xFF1A1200),
    primaryContainer     = Color(0xFF3D2E00),
    onPrimaryContainer   = BrandGold,
    secondary            = BrandOrange,
    onSecondary          = Color(0xFF1A0800),
    secondaryContainer   = Color(0xFF3D1800),
    onSecondaryContainer = BrandOrange,
    background           = BrandDark,
    onBackground         = Color(0xFFEEEEEE),
    surface              = BrandSurface,
    onSurface            = Color(0xFFEEEEEE),
    surfaceVariant       = BrandSurfaceVar,
    onSurfaceVariant     = Color(0xFFAAAAAA),
    outline              = Color(0xFF555555),
)

private val LightColorScheme = lightColorScheme(
    primary              = Color(0xFF795700), // dark amber — 5.1:1 contrast on white
    onPrimary            = Color.White,
    primaryContainer     = Color(0xFFFFF4C2),
    onPrimaryContainer   = Color(0xFF3D2E00),
    secondary            = Color(0xFFBF4A1A), // darker orange — readable on white
    onSecondary          = Color.White,
    secondaryContainer   = Color(0xFFFFDDD0),
    onSecondaryContainer = Color(0xFF3D1800),
)

@Composable
fun JokesAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content,
    )
}
