package com.aldiprahasta.tmdb.data.source.remote.response.tv

import com.aldiprahasta.tmdb.data.source.remote.response.ExternalIdResponse
import com.aldiprahasta.tmdb.data.source.remote.response.GenreResponse
import com.aldiprahasta.tmdb.data.source.remote.response.VideoResponse
import com.google.gson.annotations.SerializedName

data class TvDetailResponse(

        @field:SerializedName("first_air_date")
        val firstAirDate: String?,

        @field:SerializedName("overview")
        val overview: String?,

        @field:SerializedName("original_language")
        val originalLanguage: String?,

        @field:SerializedName("number_of_episodes")
        val numberOfEpisodes: Int?,

        @field:SerializedName("languages")
        val languages: List<String>?,

        @field:SerializedName("networks")
        val networks: List<NetworksItem>?,

        @field:SerializedName("type")
        val type: String?,

        @field:SerializedName("poster_path")
        val posterPath: String?,

        @field:SerializedName("backdrop_path")
        val backdropPath: String?,

        @field:SerializedName("genres")
        val genres: List<GenreResponse>?,

        @field:SerializedName("original_name")
        val originalName: String?,

        @field:SerializedName("popularity")
        val popularity: Double?,

        @field:SerializedName("vote_average")
        val voteAverage: Double?,

        @field:SerializedName("name")
        val name: String?,

        @field:SerializedName("tagline")
        val tagline: String?,

        @field:SerializedName("id")
        val id: Int,

        @field:SerializedName("number_of_seasons")
        val numberOfSeasons: Int?,

        @field:SerializedName("last_air_date")
        val lastAirDate: String?,

        @field:SerializedName("vote_count")
        val voteCount: Double?,

        @field:SerializedName("homepage")
        val homepage: String?,

        @field:SerializedName("status")
        val status: String?,

        @field:SerializedName("videos")
        val videos: VideoResponse?,

        @field:SerializedName("external_ids")
        val externalIds: ExternalIdResponse,

        @field:SerializedName("content_ratings")
        val contentRatings: ContentRatingResponse
)

data class NetworksItem(

        @field:SerializedName("logo_path")
        val logoPath: String?,

        @field:SerializedName("name")
        val name: String?,

        @field:SerializedName("id")
        val id: Int,

        @field:SerializedName("origin_country")
        val originCountry: String?
)

data class ContentRatingResponse(
        @field:SerializedName("results")
        val results: List<ContentRatingResponseModel>?
)

data class ContentRatingResponseModel(
        @field:SerializedName("iso_3166_1")
        val code: String?,

        @field:SerializedName("rating")
        val rating: String?
)
