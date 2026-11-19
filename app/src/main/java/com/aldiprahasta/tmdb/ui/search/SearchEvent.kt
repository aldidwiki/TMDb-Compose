package com.aldiprahasta.tmdb.ui.search

import com.aldiprahasta.tmdb.domain.model.SearchCategory

sealed interface SearchEvent {
    data class OnSearchQueryChanged(val query: String) : SearchEvent
    data class OnCategorySelected(val category: SearchCategory) : SearchEvent
    data object Retry : SearchEvent
}
