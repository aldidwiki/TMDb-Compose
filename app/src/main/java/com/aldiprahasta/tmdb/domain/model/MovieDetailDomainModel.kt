package com.aldiprahasta.tmdb.domain.model

data class MovieDetailDomainModel(
        val title: String,
        val posterPath: String?,
        val releaseDate: String,
        val runtime: String,
        val tagline: String,
        val overview: String,
        val id: Int,
        val voteAverage: Double,
        val genres: String,
        val certification: String,
        val backdropPath: String?,
        val casts: List<CastDomainModel>
)