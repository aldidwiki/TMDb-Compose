package com.aldiprahasta.tmdb.data.source.remote.response.tv

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TvSeasonResponse(
        @SerialName("id")
        val id: Int,

        @SerialName("air_date")
        val airDate: String?,

        @SerialName("name")
        val name: String?,

        @SerialName("overview")
        val overview: String?,

        @SerialName("poster_path")
        val posterPath: String?,

        @SerialName("season_number")
        val seasonNumber: Int?,

        @SerialName("vote_average")
        val voteAverage: Double?,

        @SerialName("episodes")
        val episodes: List<TvEpisodeItemResponse>
)

@Serializable
data class TvEpisodeItemResponse(

        @SerialName("episode_type")
        val episodeType: String?,

        @SerialName("production_code")
        val productionCode: String?,

        @SerialName("overview")
        val overview: String?,

        @SerialName("show_id")
        val showId: Int?,

        @SerialName("season_number")
        val seasonNumber: Int?,

        @SerialName("runtime")
        val runtime: Int?,

        @SerialName("still_path")
        val stillPath: String?,

        @SerialName("air_date")
        val airDate: String?,

        @SerialName("episode_number")
        val episodeNumber: Int?,

        @SerialName("vote_average")
        val voteAverage: Double?,

        @SerialName("name")
        val name: String?,

        @SerialName("id")
        val id: Int,

        @SerialName("vote_count")
        val voteCount: Int?
)
