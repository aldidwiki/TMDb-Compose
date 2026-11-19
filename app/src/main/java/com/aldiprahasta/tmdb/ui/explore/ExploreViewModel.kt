package com.aldiprahasta.tmdb.ui.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.aldiprahasta.tmdb.domain.model.ExploreFilters
import com.aldiprahasta.tmdb.domain.model.ExplorePreset
import com.aldiprahasta.tmdb.domain.model.ExploreQuery
import com.aldiprahasta.tmdb.domain.model.ExploreSort
import com.aldiprahasta.tmdb.domain.model.ExploreTab
import com.aldiprahasta.tmdb.domain.model.GenreDomainModel
import com.aldiprahasta.tmdb.domain.usecase.GetExploreContent
import com.aldiprahasta.tmdb.domain.usecase.GetMovieGenreList
import com.aldiprahasta.tmdb.domain.usecase.GetTvGenreList
import com.aldiprahasta.tmdb.utils.UiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
internal class ExploreViewModel(
        getExploreContent: GetExploreContent,
        private val getMovieGenreList: GetMovieGenreList,
        private val getTvGenreList: GetTvGenreList
) : ViewModel() {
    private val _state = MutableStateFlow(ExploreState())
    val state = _state.asStateFlow()
    val results = state.map { it.query }.distinctUntilChanged().flatMapLatest(getExploreContent::invoke).cachedIn(viewModelScope)

    init {
        loadGenres(isMovie = true)
        loadGenres(isMovie = false)
    }

    fun onEvent(event: ExploreEvent) {
        when (event) {
            is ExploreEvent.SelectTab -> _state.update { it.copy(tab = event.tab) }
            is ExploreEvent.SelectPreset -> updateQuery { query ->
                val isPeople = query.tab == ExploreTab.PEOPLE
                val isActive = query.preset == event.preset && (!isPeople || query.sort == null)
                query.copy(preset = event.preset.takeUnless { isActive }, sort = query.sort.takeUnless { isPeople })
            }
            is ExploreEvent.ApplySort -> updateQuery { it.copy(sort = event.sort) }
            is ExploreEvent.ApplyFilters -> updateQuery { it.copy(filters = event.filters) }
            ExploreEvent.ResetFilters -> updateQuery { it.copy(filters = ExploreFilters()) }
            ExploreEvent.RetryGenres -> retryGenres()
        }
    }

    private fun updateQuery(transform: (ExploreQuery) -> ExploreQuery) {
        _state.update { state -> state.copy(queries = state.queries + (state.tab to transform(state.query))) }
    }

    private fun retryGenres() {
        val state = _state.value
        if (state.tab != ExploreTab.TV && state.movieGenres is UiState.Error) loadGenres(isMovie = true)
        if (state.tab != ExploreTab.MOVIES && state.tvGenres is UiState.Error) loadGenres(isMovie = false)
    }

    private fun loadGenres(isMovie: Boolean) {
        updateGenres(isMovie, UiState.Loading)
        viewModelScope.launch {
            val moviesOrShows = if (isMovie) getMovieGenreList() else getTvGenreList()
            moviesOrShows.catch { emit(UiState.Error(it)) }.collect { updateGenres(isMovie, it) }
        }
    }

    private fun updateGenres(isMovie: Boolean, genres: UiState<List<GenreDomainModel>>) {
        _state.update { if (isMovie) it.copy(movieGenres = genres) else it.copy(tvGenres = genres) }
    }
}
