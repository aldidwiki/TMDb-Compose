package com.aldiprahasta.tmdb.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.aldiprahasta.tmdb.domain.model.SearchDomainModel
import com.aldiprahasta.tmdb.domain.usecase.GetSearchResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModel(getSearchResult: GetSearchResult) : ViewModel() {
    private val query = MutableSharedFlow<String>(replay = 1)
    fun setSearchQuery(query: String) {
        this.query.tryEmit(query)
    }

    val searchResults: StateFlow<PagingData<SearchDomainModel>> = query.flatMapLatest { q ->
        getSearchResult(q)
    }
            .cachedIn(viewModelScope)
            .stateIn(
                    viewModelScope,
                    SharingStarted.WhileSubscribed(5000L),
                    PagingData.empty()
            )
}