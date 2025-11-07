package com.aldiprahasta.tmdb.ui.search

sealed interface SearchEvent {
    data class OnSearchQueryChanged(val query: String): SearchEvent
}