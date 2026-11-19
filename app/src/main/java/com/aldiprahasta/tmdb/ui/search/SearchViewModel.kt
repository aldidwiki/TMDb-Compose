package com.aldiprahasta.tmdb.ui.search

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.aldiprahasta.tmdb.domain.model.SearchCategory
import com.aldiprahasta.tmdb.domain.usecase.GetMovieGenreList
import com.aldiprahasta.tmdb.domain.usecase.GetSearchResult
import com.aldiprahasta.tmdb.utils.UiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

class SearchViewModel(
        private val getSearchResult: GetSearchResult,
        getMovieGenreList: GetMovieGenreList,
        private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _uiState = MutableStateFlow(SearchState(
            query = savedStateHandle[QUERY_KEY] ?: "",
            category = savedStateHandle[CATEGORY_KEY] ?: SearchCategory.ALL
    ))
    val uiState: StateFlow<SearchState> = _uiState.asStateFlow()

    private val searchQuery = MutableStateFlow(Query(_uiState.value.query.trim(), revision = 0))
    private val retries = MutableSharedFlow<SearchCategory>(extraBufferCapacity = 1)

    init {
        viewModelScope.launch {
            searchQuery.collectLatest { query ->
                if (query.text.isEmpty()) return@collectLatest
                delay(300)

                coroutineScope {
                    SearchCategory.entries.forEach { category ->
                        launch {
                            retries.filter { it == category }.onStart { emit(category) }.collect {
                                updateFeed(query, category, UiState.Loading)

                                val result = try {
                                    val firstPage = getSearchResult.getFirstPage(query.text, category)
                                    UiState.Success(SearchResultFeed(
                                            totalResults = firstPage.totalResults,
                                            results = getSearchResult(query.text, category, firstPage)
                                                    .cachedIn(this)
                                    ))
                                } catch (exception: CancellationException) {
                                    throw exception
                                } catch (exception: Exception) {
                                    Timber.e(exception)
                                    UiState.Error(exception)
                                }

                                updateFeed(query, category, result)
                            }
                        }
                    }
                }
            }
        }

        viewModelScope.launch {
            searchQuery.first { it.text.isNotEmpty() }
            getMovieGenreList().collect { result ->
                if (result is UiState.Success) {
                    _uiState.update { it.copy(movieGenreNames = result.data.associate { genre -> genre.id to genre.name }) }
                }
            }
        }
    }

    fun onEvent(event: SearchEvent) {
        when (event) {
            is SearchEvent.OnSearchQueryChanged -> updateQuery(event.query)
            is SearchEvent.OnCategorySelected -> {
                savedStateHandle[CATEGORY_KEY] = event.category
                _uiState.update { it.copy(category = event.category) }
            }
            SearchEvent.Retry -> {
                val state = _uiState.value
                if (state.feeds[state.category] is UiState.Error) retries.tryEmit(state.category)
            }
        }
    }

    private fun updateQuery(query: String) {
        savedStateHandle[QUERY_KEY] = query
        val normalizedQuery = query.trim()
        val currentQuery = searchQuery.value

        if (currentQuery.text == normalizedQuery) {
            _uiState.update { it.copy(query = query) }
            return
        }

        _uiState.update { it.copy(query = query, feeds = emptyMap()) }
        searchQuery.value = Query(normalizedQuery, currentQuery.revision + 1)
    }

    private fun updateFeed(query: Query, category: SearchCategory, feed: UiState<SearchResultFeed>) {
        if (searchQuery.value == query) {
            _uiState.update { it.copy(feeds = it.feeds + (category to feed)) }
        }
    }

    private data class Query(val text: String, val revision: Int)

    private companion object {
        const val QUERY_KEY = "search_query"
        const val CATEGORY_KEY = "search_category"
    }
}
