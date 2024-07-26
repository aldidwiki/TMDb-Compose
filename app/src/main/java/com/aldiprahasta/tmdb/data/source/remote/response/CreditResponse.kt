package com.aldiprahasta.tmdb.data.source.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreditResponse(
        @SerialName("cast")
        val casts: List<CastResponseModel>?
)

@Serializable
data class CastResponseModel(

        @SerialName("cast_id")
        val castId: Int?,

        @SerialName("character")
        val character: String?,

        @SerialName("gender")
        val gender: Int?,

        @SerialName("credit_id")
        val creditId: String?,

        @SerialName("known_for_department")
        val knownForDepartment: String?,

        @SerialName("original_name")
        val originalName: String?,

        @SerialName("popularity")
        val popularity: Double?,

        @SerialName("name")
        val name: String?,

        @SerialName("profile_path")
        val profilePath: String?,

        @SerialName("id")
        val id: Int,

        @SerialName("order")
        val order: Int?,

        @SerialName("media_type")
        val mediaType: String?,

        @SerialName("title")
        val title: String?,

        @SerialName("poster_path")
        val posterPath: String?,

        @SerialName("release_date")
        val releaseDate: String?,

        @SerialName("roles")
        val roles: List<RolesResponseModel>?,

        @SerialName("total_episode_count")
        val totalEpisodeCount: Int?,

        @SerialName("first_air_date")
        val firstAirDate: String?,

        @SerialName("episode_count")
        val episodeCount: Int?,

        @SerialName("genre_ids")
        val genreIds: List<Int>?
)

@Serializable
data class RolesResponseModel(
        @SerialName("credit_id")
        val creditId: String,

        @SerialName("character")
        val character: String?,

        @SerialName("episode_count")
        val episodeCount: Int?
)
