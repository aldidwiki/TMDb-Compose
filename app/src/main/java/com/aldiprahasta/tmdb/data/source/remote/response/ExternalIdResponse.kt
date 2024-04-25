package com.aldiprahasta.tmdb.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class ExternalIdResponse(
        @field:SerializedName("imdb_id")
        val imdbId: String?,

        @field:SerializedName("wikidata_id")
        val wikidataId: String?,

        @field:SerializedName("twitter_id")
        val twitterId: String?,

        @field:SerializedName("facebook_id")
        val facebookId: String?,

        @field:SerializedName("instagram_id")
        val instagramId: String?
)
