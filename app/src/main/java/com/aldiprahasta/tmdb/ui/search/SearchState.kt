package com.aldiprahasta.tmdb.ui.search

import androidx.compose.runtime.Immutable

@Immutable
data class SearchState(
        val query: String = "",
)
