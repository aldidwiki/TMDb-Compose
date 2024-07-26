package com.aldiprahasta.tmdb.data.source.remote.response.search

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SearchResponse(
        @SerialName("total_pages")
        val totalPages: Int,

        @SerialName("total_results")
        val totalResults: Int,

        @SerialName("page")
        val page: Int,

        @SerialName("results")
        val results: List<SearchResponseModel>
)

@Serializable
data class SearchResponseModel(

        @SerialName("overview")
        val overview: String?,

        @SerialName("original_language")
        val originalLanguage: String?,

        @SerialName("original_title")
        val originalTitle: String?,

        @SerialName("title")
        val title: String?,

        @SerialName("poster_path")
        val posterPath: String?,

        @SerialName("backdrop_path")
        val backdropPath: String?,

        @SerialName("media_type")
        val mediaType: String?,

        @SerialName("release_date")
        val releaseDate: String?,

        @SerialName("popularity")
        val popularity: Double?,

        @SerialName("vote_average")
        val voteAverage: Double?,

        @SerialName("id")
        val id: Int,

        @SerialName("vote_count")
        val voteCount: Int?,

        @SerialName("name")
        val name: String?,

        @SerialName("first_air_date")
        val firstAirDate: String?,

        @SerialName("gender")
        val gender: Int?,

        @SerialName("known_for_department")
        val knownForDepartment: String?,

        @SerialName("profile_path")
        val profilePath: String?
)
