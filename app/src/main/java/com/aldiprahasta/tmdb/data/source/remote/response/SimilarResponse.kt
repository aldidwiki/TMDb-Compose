package com.aldiprahasta.tmdb.data.source.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SimilarResponse(
        val results: List<SimilarItemResponse>,
        @SerialName("total_pages") val totalPages: Int,
        @SerialName("total_results") val totalResults: Int
)

@Serializable
data class SimilarItemResponse(
        val id: Int,
        val title: String? = null,
        val name: String? = null,
        @SerialName("poster_path") val posterPath: String? = null,
        @SerialName("release_date") val releaseDate: String? = null,
        @SerialName("first_air_date") val firstAirDate: String? = null,
        @SerialName("vote_average") val voteAverage: Double? = null,
        @SerialName("vote_count") val voteCount: Int? = null
)
