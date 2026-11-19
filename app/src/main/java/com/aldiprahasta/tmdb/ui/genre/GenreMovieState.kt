package com.aldiprahasta.tmdb.ui.genre

import androidx.compose.runtime.Immutable
import com.aldiprahasta.tmdb.domain.model.MovieSortOrder

@Immutable
data class GenreMovieState(
        val sortOrder: MovieSortOrder = MovieSortOrder.POPULARITY,
        val releaseYear: Int? = null,
        val minimumRating: Int? = null
)
