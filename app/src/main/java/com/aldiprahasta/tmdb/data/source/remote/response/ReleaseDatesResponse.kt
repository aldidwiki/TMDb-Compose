package com.aldiprahasta.tmdb.data.source.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReleaseDatesResponse(
        @SerialName("results")
        val results: List<ResultsItem>?
)

@Serializable
data class ResultsItem(

        @SerialName("release_dates")
        val releaseDates: List<ReleaseDatesItem>?,

        @SerialName("iso_3166_1")
        val iso31661: String?
)

@Serializable
data class ReleaseDatesItem(

        @SerialName("certification")
        val certification: String?
)
