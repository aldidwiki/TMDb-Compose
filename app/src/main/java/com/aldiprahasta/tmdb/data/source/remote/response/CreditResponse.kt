package com.aldiprahasta.tmdb.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class CreditResponse(
        @field:SerializedName("cast")
        val casts: List<CastResponseModel>?
)

data class CastResponseModel(

        @field:SerializedName("cast_id")
        val castId: Int?,

        @field:SerializedName("character")
        val character: String?,

        @field:SerializedName("gender")
        val gender: Int?,

        @field:SerializedName("credit_id")
        val creditId: String?,

        @field:SerializedName("known_for_department")
        val knownForDepartment: String?,

        @field:SerializedName("original_name")
        val originalName: String?,

        @field:SerializedName("popularity")
        val popularity: Double?,

        @field:SerializedName("name")
        val name: String?,

        @field:SerializedName("profile_path")
        val profilePath: String?,

        @field:SerializedName("id")
        val id: Int,

        @field:SerializedName("order")
        val order: Int,

        @field:SerializedName("media_type")
        val mediaType: String?,

        @field:SerializedName("title")
        val title: String?,

        @field:SerializedName("poster_path")
        val posterPath: String?
)
