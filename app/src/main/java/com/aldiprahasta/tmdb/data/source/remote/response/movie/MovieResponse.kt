package com.aldiprahasta.tmdb.data.source.remote.response.movie

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MovieResponse(
        @SerialName("results")
        val movieResponseModelList: List<MovieResponseModel>
)

@Serializable
data class MovieResponseModel(

        @SerialName("overview")
        val overview: String?,

        @SerialName("original_language")
        val originalLanguage: String?,

        @SerialName("original_title")
        val originalTitle: String?,

        @SerialName("release_date")
        val releaseDate: String?,

        @SerialName("popularity")
        val popularity: Double?,

        @SerialName("id")
        val id: Int,

        @SerialName("title")
        val title: String?,

        @SerialName("poster_path")
        val posterPath: String?
)
