package com.aldiprahasta.tmdb.domain.model

data class SimilarContent(
        val id: Int,
        val title: String,
        val posterPath: String?,
        val releaseYear: String?,
        val voteAverage: Double?
)

data class SimilarPage(
        val items: List<SimilarContent>,
        val totalPages: Int,
        val totalResults: Int
)
