package com.aldiprahasta.tmdb.domain.model

data class MovieDomainModel(
        val title: String,
        val posterPath: String?,
        val releaseDate: String,
        val movieId: Int
)