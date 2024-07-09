package com.aldiprahasta.tmdb.domain.model

data class TvSeasonDetailDomainModel(
        val tvSeasonDomainModel: TvSeasonDomainModel,
        val tvEpisodeList: List<TvEpisodeDomainModel>
)
