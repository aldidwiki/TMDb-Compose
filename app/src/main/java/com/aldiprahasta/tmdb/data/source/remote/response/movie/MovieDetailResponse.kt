package com.aldiprahasta.tmdb.data.source.remote.response.movie

import com.aldiprahasta.tmdb.data.source.remote.response.CreditResponse
import com.aldiprahasta.tmdb.data.source.remote.response.ExternalIdResponse
import com.aldiprahasta.tmdb.data.source.remote.response.GenreResponse
import com.aldiprahasta.tmdb.data.source.remote.response.ReleaseDatesResponse
import com.aldiprahasta.tmdb.data.source.remote.response.VideoResponse
import com.google.gson.annotations.SerializedName

data class MovieDetailResponse(
        @field:SerializedName("overview")
        val overview: String?,

        @field:SerializedName("original_language")
        val originalLanguage: String?,

        @field:SerializedName("original_title")
        val originalTitle: String?,

        @field:SerializedName("runtime")
        val runtime: Int?,

        @field:SerializedName("title")
        val title: String?,

        @field:SerializedName("poster_path")
        val posterPath: String?,

        @field:SerializedName("backdrop_path")
        val backdropPath: String?,

        @field:SerializedName("spoken_languages")
        val spokenLanguages: List<SpokenLanguagesItem>?,

        @field:SerializedName("revenue")
        val revenue: Long?,

        @field:SerializedName("release_date")
        val releaseDate: String?,

        @field:SerializedName("genres")
        val genres: List<GenreResponse>?,

        @field:SerializedName("popularity")
        val popularity: Double?,

        @field:SerializedName("vote_average")
        val voteAverage: Double?,

        @field:SerializedName("tagline")
        val tagline: String?,

        @field:SerializedName("id")
        val id: Int,

        @field:SerializedName("budget")
        val budget: Long?,

        @field:SerializedName("status")
        val status: String?,

        @field:SerializedName("release_dates")
        val releaseDates: ReleaseDatesResponse?,

        @field:SerializedName("credits")
        val credits: CreditResponse?,

        @field:SerializedName("external_ids")
        val externalIds: ExternalIdResponse,

        @field:SerializedName("videos")
        val video: VideoResponse?
)

data class SpokenLanguagesItem(
        @field:SerializedName("name")
        val name: String?,

        @field:SerializedName("iso_639_1")
        val iso6391: String?,

        @field:SerializedName("english_name")
        val englishName: String?
)
