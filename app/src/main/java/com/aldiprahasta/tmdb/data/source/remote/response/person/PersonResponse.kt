package com.aldiprahasta.tmdb.data.source.remote.response.person

import com.aldiprahasta.tmdb.data.source.remote.response.ExternalIdResponse
import com.google.gson.annotations.SerializedName

data class PersonResponse(

        @field:SerializedName("birthday")
        val birthday: String?,

        @field:SerializedName("gender")
        val gender: Int?,

        @field:SerializedName("imdb_id")
        val imdbId: String?,

        @field:SerializedName("known_for_department")
        val knownForDepartment: String?,

        @field:SerializedName("profile_path")
        val profilePath: String?,

        @field:SerializedName("biography")
        val biography: String?,

        @field:SerializedName("deathday")
        val deathday: String?,

        @field:SerializedName("place_of_birth")
        val placeOfBirth: String?,

        @field:SerializedName("popularity")
        val popularity: Double?,

        @field:SerializedName("name")
        val name: String?,

        @field:SerializedName("id")
        val id: Int,

        @field:SerializedName("external_ids")
        val externalIds: ExternalIdResponse
)
