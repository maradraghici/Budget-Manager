package com.example.budgetmanager.ui.theme

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
    primary = Primary1,
    secondary = Neutral1,
    surface = Neutral2,
    background = Neutral2,
    tertiary = Neutral3,
    inversePrimary = Red
)

private val LightColorScheme = lightColorScheme(
    primary = Primary1,
    secondary = Neutral1,
    surface = Neutral2,
    background = Neutral2,
    tertiary = Neutral3,
    inversePrimary = Red
)

@Composable
fun BudgetManagerTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}