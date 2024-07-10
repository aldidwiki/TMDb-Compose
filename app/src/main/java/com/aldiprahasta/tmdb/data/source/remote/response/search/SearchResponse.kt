package com.aldiprahasta.tmdb.data.source.remote.response.search

import com.google.gson.annotations.SerializedName

data class SearchResponse(
        @field:SerializedName("total_pages")
        val totalPages: Int,

        @field:SerializedName("total_results")
        val totalResults: Int,

        @field:SerializedName("page")
        val page: Int,

        @field:SerializedName("results")
        val results: List<SearchResponseModel>
)

data class SearchResponseModel(

        @field:SerializedName("overview")
        val overview: String?,

        @field:SerializedName("original_language")
        val originalLanguage: String?,

        @field:SerializedName("original_title")
        val originalTitle: String?,

        @field:SerializedName("title")
        val title: String?,

        @field:SerializedName("poster_path")
        val posterPath: String?,

        @field:SerializedName("backdrop_path")
        val backdropPath: String?,

        @field:SerializedName("media_type")
        val mediaType: String?,

        @field:SerializedName("release_date")
        val releaseDate: String?,

        @field:SerializedName("popularity")
        val popularity: Double?,

        @field:SerializedName("vote_average")
        val voteAverage: Double?,

        @field:SerializedName("id")
        val id: Int,

        @field:SerializedName("vote_count")
        val voteCount: Int?,

        @field:SerializedName("name")
        val name: String?,

        @field:SerializedName("first_air_date")
        val firstAirDate: String?,

        @field:SerializedName("gender")
        val gender: Int?,

        @field:SerializedName("known_for_department")
        val knownForDepartment: String?,

        @field:SerializedName("profile_path")
        val profilePath: String?
)
