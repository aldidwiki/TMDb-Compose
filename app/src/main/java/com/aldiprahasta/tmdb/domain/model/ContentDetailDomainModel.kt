package com.aldiprahasta.tmdb.domain.model

data class ContentDetailDomainModel(
        val title: String,
        val posterPath: String?,
        val releaseDate: String,
        val overview: String,
        val id: Int,
        val voteAverage: Double,
        val genres: String,
        val backdropPath: String?,
        val status: String,
        val casts: List<CastDomainModel>,
        val videos: List<VideoDomainModel>,
        val seasons: List<TvSeasonDomainModel>? = null,
        val voteCount: Int? = null,
        val seasonCount: Int? = null,
        val releaseYear: String? = null,
        val runtimeMinutes: Int? = null,
        val usReleaseDate: String? = null,
        val directors: List<String> = emptyList(),
        val composers: List<String> = emptyList(),
        val creators: List<String> = emptyList(),
        val executiveProducers: List<String> = emptyList()
)
