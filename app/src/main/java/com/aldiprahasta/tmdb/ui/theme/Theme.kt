package com.aldiprahasta.tmdb.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
        primary = TMDBPrimaryColor,
        secondary = TMDBSecondaryColor,
        tertiary = TMDBTertiaryColor
)

@Composable
fun TMDbTheme(
        content: @Composable () -> Unit
) {
    MaterialTheme(
            colorScheme = LightColorScheme,
            typography = Typography,
            content = content
    )
}