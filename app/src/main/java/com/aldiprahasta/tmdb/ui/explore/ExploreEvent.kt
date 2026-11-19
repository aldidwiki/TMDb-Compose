package com.aldiprahasta.tmdb.ui.explore

import com.aldiprahasta.tmdb.domain.model.ExploreFilters
import com.aldiprahasta.tmdb.domain.model.ExplorePreset
import com.aldiprahasta.tmdb.domain.model.ExploreSort
import com.aldiprahasta.tmdb.domain.model.ExploreTab

internal sealed interface ExploreEvent {
    data class SelectTab(val tab: ExploreTab) : ExploreEvent
    data class SelectPreset(val preset: ExplorePreset) : ExploreEvent
    data class ApplySort(val sort: ExploreSort) : ExploreEvent
    data class ApplyFilters(val filters: ExploreFilters) : ExploreEvent
    data object ResetFilters : ExploreEvent
    data object RetryGenres : ExploreEvent
}
