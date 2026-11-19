package com.aldiprahasta.tmdb.ui.genre

import com.aldiprahasta.tmdb.domain.model.MovieSortOrder

sealed interface GenreMovieEvent {
    data class ApplySort(val sortOrder: MovieSortOrder) : GenreMovieEvent
    data class ApplyReleaseYear(val year: Int?) : GenreMovieEvent
    data class ApplyMinimumRating(val rating: Int?) : GenreMovieEvent
}
