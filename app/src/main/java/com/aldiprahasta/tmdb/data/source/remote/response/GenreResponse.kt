package com.aldiprahasta.tmdb.data.source.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GenreResponse(
        @SerialName("genres")
        val genres: List<GenreResponseModel>
)

@Serializable
data class GenreResponseModel(

        @SerialName("name")
        val name: String,

        @SerialName("id")
        val id: Int
)
