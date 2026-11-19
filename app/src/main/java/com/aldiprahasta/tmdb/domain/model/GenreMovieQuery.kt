package com.aldiprahasta.tmdb.domain.model

data class GenreMovieQuery(
        val genreId: Int,
        val sortOrder: MovieSortOrder,
        val releaseYear: Int?,
        val minimumRating: Int?
)
