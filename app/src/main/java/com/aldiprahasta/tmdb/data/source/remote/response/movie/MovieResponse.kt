package com.aldiprahasta.tmdb.data.source.remote.response.movie

import com.google.gson.annotations.SerializedName

data class MovieResponse(
        @field:SerializedName("results")
        val movieResponseModelList: List<MovieResponseModel>
)

data class MovieResponseModel(

        @field:SerializedName("overview")
        val overview: String?,

        @field:SerializedName("original_language")
        val originalLanguage: String?,

        @field:SerializedName("original_title")
        val originalTitle: String?,

        @field:SerializedName("release_date")
        val releaseDate: String?,

        @field:SerializedName("popularity")
        val popularity: Double?,

        @field:SerializedName("id")
        val id: Int,

        @field:SerializedName("title")
        val title: String?,

        @field:SerializedName("poster_path")
        val posterPath: String?
)
