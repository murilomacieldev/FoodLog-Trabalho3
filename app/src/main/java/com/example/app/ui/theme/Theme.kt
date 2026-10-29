package com.example.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val FoodLogColors = lightColorScheme(
    primary = LaranjaFoodLog,
    onPrimary = Color.White,
    primaryContainer = RosaSuaveFoodLog,
    background = FundoFoodLog,
    onBackground = Color(0xFF232323),
    surface = Color.White,
    onSurface = Color(0xFF232323),
    secondary = Color(0xFF80574B)
)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    // Mantemos o laranja da identidade visual, independentemente das cores do celular.
    MaterialTheme(colorScheme = FoodLogColors, typography = Typography, content = content)
}
