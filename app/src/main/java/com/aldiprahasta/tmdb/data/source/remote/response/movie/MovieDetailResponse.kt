package com.aldiprahasta.tmdb.data.source.remote.response.movie

import com.aldiprahasta.tmdb.data.source.remote.response.CreditResponse
import com.aldiprahasta.tmdb.data.source.remote.response.ExternalIdResponse
import com.aldiprahasta.tmdb.data.source.remote.response.GenreResponseModel
import com.aldiprahasta.tmdb.data.source.remote.response.ReleaseDatesResponse
import com.aldiprahasta.tmdb.data.source.remote.response.VideoResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MovieDetailResponse(
        @SerialName("overview")
        val overview: String?,

        @SerialName("original_language")
        val originalLanguage: String?,

        @SerialName("original_title")
        val originalTitle: String?,

        @SerialName("runtime")
        val runtime: Int?,

        @SerialName("title")
        val title: String?,

        @SerialName("poster_path")
        val posterPath: String?,

        @SerialName("backdrop_path")
        val backdropPath: String?,

        @SerialName("spoken_languages")
        val spokenLanguages: List<SpokenLanguagesItem>?,

        @SerialName("revenue")
        val revenue: Long?,

        @SerialName("release_date")
        val releaseDate: String?,

        @SerialName("genres")
        val genres: List<GenreResponseModel>?,

        @SerialName("popularity")
        val popularity: Double?,

        @SerialName("vote_average")
        val voteAverage: Double?,

        @SerialName("tagline")
        val tagline: String?,

        @SerialName("id")
        val id: Int,

        @SerialName("budget")
        val budget: Long?,

        @SerialName("status")
        val status: String?,

        @SerialName("release_dates")
        val releaseDates: ReleaseDatesResponse?,

        @SerialName("credits")
        val credits: CreditResponse?,

        @SerialName("external_ids")
        val externalIds: ExternalIdResponse,

        @SerialName("videos")
        val video: VideoResponse?
)

@Serializable
data class SpokenLanguagesItem(
        @SerialName("name")
        val name: String?,

        @SerialName("iso_639_1")
        val iso6391: String?,

        @SerialName("english_name")
        val englishName: String?
)
