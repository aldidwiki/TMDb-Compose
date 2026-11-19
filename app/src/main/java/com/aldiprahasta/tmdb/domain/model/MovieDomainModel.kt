package com.aldiprahasta.tmdb.domain.model

data class MovieDomainModel(
        val title: String,
        val posterPath: String?,
        val releaseDate: String,
        val movieId: Int,
        val backdropPath: String? = null,
        val releaseYear: String? = null,
        val voteAverage: Double? = null,
        val genreIds: List<Int> = emptyList(),
        val overview: String = ""
)
