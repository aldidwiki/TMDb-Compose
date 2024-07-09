package com.aldiprahasta.tmdb.domain.model

data class TvEpisodeDomainModel(
        val id: Int,
        val name: String,
        val stillPath: String?,
        val runtime: Int,
        val overview: String,
        val voteAverage: Double,
        val episodeNumber: Int,
        val airDate: String
)
