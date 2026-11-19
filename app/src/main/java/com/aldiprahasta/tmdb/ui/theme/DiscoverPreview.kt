package com.aldiprahasta.tmdb.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable

@Composable
internal fun DiscoverPreview(content: @Composable () -> Unit) {
    DiscoverTheme {
        Surface(color = MaterialTheme.colorScheme.background, content = content)
    }
}
