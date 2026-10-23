package com.example.evalapp_grupo12.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val ColoresApp = lightColorScheme(
    primary = Verde,
    onPrimary = Blanco,
    primaryContainer = VerdeClaro,
    onPrimaryContainer = VerdeOscuro,
    secondary = VerdeOscuro,
    secondaryContainer = VerdeClaro,
    background = Blanco,
    surface = Blanco,
    surfaceContainerLow = VerdeFondo,
    surfaceContainer = VerdeFondo,
    surfaceContainerHigh = VerdeFondo,
    surfaceContainerHighest = VerdeFondo
)

@Composable
fun EvalAppTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ColoresApp,
        typography = Typography,
        content = content
    )
}
