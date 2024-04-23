package com.aldiprahasta.tmdb.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class ReleaseDatesResponse(
        @field:SerializedName("results")
        val results: List<ResultsItem>?
)

data class ResultsItem(

        @field:SerializedName("release_dates")
        val releaseDates: List<ReleaseDatesItem>?,

        @field:SerializedName("iso_3166_1")
        val iso31661: String?
)

data class ReleaseDatesItem(

        @field:SerializedName("certification")
        val certification: String?
)
