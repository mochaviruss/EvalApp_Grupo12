package com.example.evalapp_grupo12.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = Verde80,
    secondary = VerdeGris80,
    tertiary = Dorado80
)

private val LightColorScheme = lightColorScheme(
    primary = Verde40,
    secondary = VerdeGris40,
    tertiary = Dorado40
)

@Composable
fun EvalAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
