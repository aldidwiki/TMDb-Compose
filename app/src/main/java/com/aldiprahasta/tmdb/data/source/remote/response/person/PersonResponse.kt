package com.aldiprahasta.tmdb.data.source.remote.response.person

import com.aldiprahasta.tmdb.data.source.remote.response.CreditResponse
import com.aldiprahasta.tmdb.data.source.remote.response.ExternalIdResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PersonResponse(

        @SerialName("birthday")
        val birthday: String?,

        @SerialName("gender")
        val gender: Int?,

        @SerialName("imdb_id")
        val imdbId: String?,

        @SerialName("known_for_department")
        val knownForDepartment: String?,

        @SerialName("profile_path")
        val profilePath: String?,

        @SerialName("biography")
        val biography: String?,

        @SerialName("deathday")
        val deathday: String?,

        @SerialName("place_of_birth")
        val placeOfBirth: String?,

        @SerialName("popularity")
        val popularity: Double?,

        @SerialName("name")
        val name: String?,

        @SerialName("id")
        val id: Int,

        @SerialName("external_ids")
        val externalIds: ExternalIdResponse,

        @SerialName("combined_credits")
        val combinedCredits: CreditResponse?
)
