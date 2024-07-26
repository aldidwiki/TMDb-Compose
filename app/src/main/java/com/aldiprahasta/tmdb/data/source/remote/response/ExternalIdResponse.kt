package com.aldiprahasta.tmdb.data.source.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ExternalIdResponse(
        @SerialName("imdb_id")
        val imdbId: String?,

        @SerialName("wikidata_id")
        val wikidataId: String?,

        @SerialName("twitter_id")
        val twitterId: String?,

        @SerialName("facebook_id")
        val facebookId: String?,

        @SerialName("instagram_id")
        val instagramId: String?
)
