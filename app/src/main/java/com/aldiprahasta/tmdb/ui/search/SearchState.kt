package com.aldiprahasta.tmdb.ui.search

import androidx.compose.runtime.Immutable
import androidx.paging.PagingData
import com.aldiprahasta.tmdb.domain.model.SearchCategory
import com.aldiprahasta.tmdb.domain.model.SearchDomainModel
import com.aldiprahasta.tmdb.utils.UiState
import kotlinx.coroutines.flow.Flow

@Immutable
data class SearchState(
        val query: String = "",
        val category: SearchCategory = SearchCategory.ALL,
        val feeds: Map<SearchCategory, UiState<SearchResultFeed>> = emptyMap(),
        val movieGenreNames: Map<Int, String> = emptyMap()
)

data class SearchResultFeed(
        val totalResults: Int,
        val results: Flow<PagingData<SearchDomainModel>>
)
