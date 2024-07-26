package com.aldiprahasta.tmdb.data.source.remote.response.tv

import com.aldiprahasta.tmdb.data.source.remote.response.CreditResponse
import com.aldiprahasta.tmdb.data.source.remote.response.ExternalIdResponse
import com.aldiprahasta.tmdb.data.source.remote.response.GenreResponseModel
import com.aldiprahasta.tmdb.data.source.remote.response.VideoResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TvDetailResponse(

        @SerialName("first_air_date")
        val firstAirDate: String?,

        @SerialName("overview")
        val overview: String?,

        @SerialName("original_language")
        val originalLanguage: String?,

        @SerialName("number_of_episodes")
        val numberOfEpisodes: Int?,

        @SerialName("languages")
        val languages: List<String>?,

        @SerialName("networks")
        val networks: List<NetworksItemResponse>?,

        @SerialName("type")
        val type: String?,

        @SerialName("poster_path")
        val posterPath: String?,

        @SerialName("backdrop_path")
        val backdropPath: String?,

        @SerialName("genres")
        val genres: List<GenreResponseModel>?,

        @SerialName("original_name")
        val originalName: String?,

        @SerialName("popularity")
        val popularity: Double?,

        @SerialName("vote_average")
        val voteAverage: Double?,

        @SerialName("name")
        val name: String?,

        @SerialName("tagline")
        val tagline: String?,

        @SerialName("id")
        val id: Int,

        @SerialName("number_of_seasons")
        val numberOfSeasons: Int?,

        @SerialName("last_air_date")
        val lastAirDate: String?,

        @SerialName("vote_count")
        val voteCount: Double?,

        @SerialName("homepage")
        val homepage: String?,

        @SerialName("status")
        val status: String?,

        @SerialName("videos")
        val videos: VideoResponse?,

        @SerialName("external_ids")
        val externalIds: ExternalIdResponse,

        @SerialName("content_ratings")
        val contentRatings: ContentRatingResponse,

        @SerialName("aggregate_credits")
        val credits: CreditResponse?,

        @SerialName("seasons")
        val seasons: List<TvSeasonItemResponse>?,
)

@Serializable
data class NetworksItemResponse(

        @SerialName("logo_path")
        val logoPath: String?,

        @SerialName("name")
        val name: String?,

        @SerialName("id")
        val id: Int,

        @SerialName("origin_country")
        val originCountry: String?
)

@Serializable
data class ContentRatingResponse(
        @SerialName("results")
        val results: List<ContentRatingResponseModel>?
)

@Serializable
data class ContentRatingResponseModel(
        @SerialName("iso_3166_1")
        val code: String?,

        @SerialName("rating")
        val rating: String?
)

@Serializable
data class TvSeasonItemResponse(

        @SerialName("air_date")
        val airDate: String?,

        @SerialName("overview")
        val overview: String?,

        @SerialName("episode_count")
        val episodeCount: Int?,

        @SerialName("vote_average")
        val voteAverage: Double?,

        @SerialName("name")
        val name: String?,

        @SerialName("season_number")
        val seasonNumber: Int?,

        @SerialName("id")
        val id: Int,

        @SerialName("poster_path")
        val posterPath: String?
)
