package com.aldiprahasta.tmdb.data.source.remote.response.tv

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TvResponse(
        @SerialName("results")
        val tvList: List<TvResponseModel>
)

@Serializable
data class TvResponseModel(

        @SerialName("first_air_date")
        val firstAirDate: String?,

        @SerialName("overview")
        val overview: String?,

        @SerialName("original_language")
        val originalLanguage: String?,

        @SerialName("poster_path")
        val posterPath: String?,

        @SerialName("backdrop_path")
        val backdropPath: String?,

        @SerialName("original_name")
        val originalName: String?,

        @SerialName("popularity")
        val popularity: Double?,

        @SerialName("vote_average")
        val voteAverage: Double?,

        @SerialName("name")
        val name: String?,

        @SerialName("id")
        val id: Int,

        @SerialName("vote_count")
        val voteCount: Int?
)
