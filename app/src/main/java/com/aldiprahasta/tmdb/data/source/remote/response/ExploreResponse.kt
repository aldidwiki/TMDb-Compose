package com.aldiprahasta.tmdb.data.source.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ExploreResponse(
        val page: Int = 1,
        val results: List<ExploreResultResponse> = emptyList(),
        @SerialName("total_pages") val totalPages: Int = 1,
        val dates: ExploreReleaseWindow? = null
)

@Serializable
data class ExploreReleaseWindow(val minimum: String, val maximum: String)

@Serializable
data class ExploreResultResponse(
        val id: Int,
        val title: String? = null,
        val name: String? = null,
        @SerialName("original_title") val originalTitle: String? = null,
        @SerialName("original_name") val originalName: String? = null,
        @SerialName("poster_path") val posterPath: String? = null,
        @SerialName("profile_path") val profilePath: String? = null,
        @SerialName("release_date") val releaseDate: String? = null,
        @SerialName("first_air_date") val firstAirDate: String? = null,
        @SerialName("vote_average") val voteAverage: Double? = null,
        @SerialName("vote_count") val voteCount: Int? = null,
        val popularity: Double? = null,
        @SerialName("genre_ids") val genreIds: List<Int> = emptyList(),
        val overview: String? = null,
        @SerialName("known_for_department") val department: String? = null,
        @SerialName("known_for") val knownFor: List<ExploreKnownWorkResponse> = emptyList()
)

@Serializable
data class ExploreKnownWorkResponse(@SerialName("media_type") val mediaType: String? = null)
