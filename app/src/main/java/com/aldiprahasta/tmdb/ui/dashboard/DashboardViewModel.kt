package com.aldiprahasta.tmdb.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aldiprahasta.tmdb.domain.usecase.GetMovieGenreList
import com.aldiprahasta.tmdb.domain.usecase.GetMovieTrailers
import com.aldiprahasta.tmdb.domain.usecase.GetNowPlayingMovies
import com.aldiprahasta.tmdb.domain.usecase.GetTrendingMovies
import com.aldiprahasta.tmdb.utils.UiState
import com.aldiprahasta.tmdb.utils.mapSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

class DashboardViewModel(
        private val getTrendingMovies: GetTrendingMovies,
        private val getNowPlayingMovies: GetNowPlayingMovies,
        private val getMovieGenreList: GetMovieGenreList,
        private val getMovieTrailers: GetMovieTrailers
) : ViewModel() {
    private val _uiState = MutableStateFlow(DashboardState())
    val uiState: StateFlow<DashboardState> = _uiState.asStateFlow()

    init {
        loadTrending()
        loadNowPlaying()
        loadGenres()
    }

    fun onEvent(event: DashboardEvent) {
        when (event) {
            DashboardEvent.RetryTrending -> if (_uiState.value.trendingMovies is UiState.Error) loadTrending()
            DashboardEvent.RetryNowPlaying -> if (_uiState.value.nowPlayingMovies is UiState.Error) loadNowPlaying()
            DashboardEvent.RetryGenres -> if (_uiState.value.genres is UiState.Error) loadGenres()
            is DashboardEvent.OnWatchTrailerClicked -> loadTrailers(event.movieId)
            DashboardEvent.DismissTrailers -> _uiState.update { it.copy(trailers = null) }
        }
    }

    private fun loadTrending() {
        _uiState.update { it.copy(trendingMovies = UiState.Loading) }
        getTrendingMovies().onEach { state ->
            _uiState.update { it.copy(trendingMovies = state.mapSuccess { movies -> movies.take(5) }) }
        }.launchIn(viewModelScope)
    }

    private fun loadNowPlaying() {
        _uiState.update { it.copy(nowPlayingMovies = UiState.Loading) }
        getNowPlayingMovies(region = "ID").onEach { state ->
            _uiState.update { it.copy(nowPlayingMovies = state.mapSuccess { movies -> movies.take(10) }) }
        }.launchIn(viewModelScope)
    }

    private fun loadGenres() {
        _uiState.update { it.copy(genres = UiState.Loading) }
        getMovieGenreList().onEach { state ->
            _uiState.update { it.copy(genres = state) }
        }.launchIn(viewModelScope)
    }

    private fun loadTrailers(movieId: Int) {
        if (_uiState.value.trailers != null) return

        _uiState.update { it.copy(trailers = UiState.Loading) }
        getMovieTrailers(movieId).onEach { state ->
            _uiState.update { it.copy(trailers = state) }
        }.launchIn(viewModelScope)
    }
}
