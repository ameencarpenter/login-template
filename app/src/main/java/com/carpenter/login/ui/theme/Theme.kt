package com.carpenter.login.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorPalette = darkColors(
    primary = Color(0xFF6200EE),
    primaryVariant = Color(0xFF3700B3),
    onPrimary = Color(0xFFffffff),
    secondary = Color(0xFF03DAC6),
    secondaryVariant = Color(0xFF018786),
    onSecondary = Color(0xFF000000),
    background = Color(0xFFffffff),
    onBackground = Color(0xFF000000),
    surface = Color(0xFFffffff),
    onSurface = Color(0xFF000000),
    onError = Color(0xFFffffff),
    error = Color(0xFFB00020)
)

private val DarkColorPalette = lightColors(
    primary = Color(0xFFF9AA33),
    primaryVariant = Color(0xfff48226),
    onPrimary = Color(0xFF000000),
    secondary = Color(0xFF344955),
    secondaryVariant = Color(0xff23343e),
    onSecondary = Color(0xFFffffff),
    background = Color(0xFF344955),
    onBackground = Color(0xFFffffff),
    surface = Color(0xFF344955),
    onSurface = Color(0xFFffffff),
    onError = Color(0xFFffffff),
    error = Color(0xFFB00020)
)

@Composable
fun CarpenterLoginTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}