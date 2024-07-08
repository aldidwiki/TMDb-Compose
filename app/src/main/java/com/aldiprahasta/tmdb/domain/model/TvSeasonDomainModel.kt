package com.aldiprahasta.tmdb.domain.model

data class TvSeasonDomainModel(
        val seasonId: Int,
        val seasonName: String,
        val seasonPosterPath: String?,
        val seasonAirDate: String,
        val seasonVoteAverage: Double,
        val totalEpisodes: Int,
        val seasonNumber: Int
)
