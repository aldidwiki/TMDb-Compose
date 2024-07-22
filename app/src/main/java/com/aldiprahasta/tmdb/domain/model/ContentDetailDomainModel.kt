package com.aldiprahasta.tmdb.domain.model

data class ContentDetailDomainModel(
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
        val budget: String?,
        val revenue: String?,
        val originalLanguage: String,
        val status: String,
        val externalId: ExternalIdDomainModel,
        var casts: List<CastDomainModel>,
        val videos: List<VideoDomainModel>,
        val type: String?,
        val networks: List<NetworkDomainModel>?,
        val seasons: List<TvSeasonDomainModel>? = null
)