package com.aldiprahasta.tmdb.ui.genre

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.aldiprahasta.tmdb.domain.model.GenreMovieQuery
import com.aldiprahasta.tmdb.domain.model.MovieSortOrder
import com.aldiprahasta.tmdb.domain.usecase.GetMoviesByGenre
import com.aldiprahasta.tmdb.navigation.GenreMovieListing
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update

@OptIn(ExperimentalCoroutinesApi::class)
class GenreMovieViewModel(
        private val savedStateHandle: SavedStateHandle,
        getMoviesByGenre: GetMoviesByGenre
) : ViewModel() {
    private val genreId = checkNotNull(savedStateHandle.get<Int>(GenreMovieListing.GENRE_ID_ARG))
    private val _uiState = MutableStateFlow(
            GenreMovieState(
                    sortOrder = savedStateHandle[SORT_ORDER_KEY] ?: MovieSortOrder.POPULARITY,
                    releaseYear = savedStateHandle[RELEASE_YEAR_KEY],
                    minimumRating = savedStateHandle[MINIMUM_RATING_KEY]
            )
    )
    val uiState: StateFlow<GenreMovieState> = _uiState.asStateFlow()

    val movies = uiState.flatMapLatest { state ->
        getMoviesByGenre(GenreMovieQuery(
                genreId = genreId,
                sortOrder = state.sortOrder,
                releaseYear = state.releaseYear,
                minimumRating = state.minimumRating
        ))
    }
            .cachedIn(viewModelScope)

    fun onEvent(event: GenreMovieEvent) {
        when (event) {
            is GenreMovieEvent.ApplySort -> {
                savedStateHandle[SORT_ORDER_KEY] = event.sortOrder
                _uiState.update { it.copy(sortOrder = event.sortOrder) }
            }

            is GenreMovieEvent.ApplyReleaseYear -> {
                savedStateHandle[RELEASE_YEAR_KEY] = event.year
                _uiState.update { it.copy(releaseYear = event.year) }
            }

            is GenreMovieEvent.ApplyMinimumRating -> {
                savedStateHandle[MINIMUM_RATING_KEY] = event.rating
                _uiState.update { it.copy(minimumRating = event.rating) }
            }
        }
    }

    private companion object {
        const val SORT_ORDER_KEY = "genre_movie_sort_order"
        const val RELEASE_YEAR_KEY = "genre_movie_release_year"
        const val MINIMUM_RATING_KEY = "genre_movie_minimum_rating"
    }
}
