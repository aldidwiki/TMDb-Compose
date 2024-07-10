package com.aldiprahasta.tmdb.ui.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.aldiprahasta.tmdb.domain.model.SearchDomainModel
import com.aldiprahasta.tmdb.domain.usecase.GetSearchResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModel(getSearchResult: GetSearchResult) : ViewModel() {
    var searchQuery by mutableStateOf("")
        private set

    fun onSearchQueryChange(newQuery: String) {
        searchQuery = newQuery
    }

    val searchResults: StateFlow<PagingData<SearchDomainModel>> = snapshotFlow { searchQuery }
            .flatMapLatest { q -> getSearchResult(q) }
            .cachedIn(viewModelScope)
            .stateIn(
                    viewModelScope,
                    SharingStarted.WhileSubscribed(5000),
                    PagingData.empty()
            )
}