package com.aldiprahasta.tmdb.data.source.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ImageResponse(
    @SerialName("id")
    val id: Int,

    @SerialName("backdrops")
    val backdrops: List<ImageResponseModel>?,

    @SerialName("posters")
    val posters: List<ImageResponseModel>?
)

@Serializable
data class ImageResponseModel(

    @SerialName("aspect_ratio")
    val aspectRatio: Double?,

    @SerialName("file_path")
    val filePath: String?,

    @SerialName("vote_average")
    val voteAverage: Double?,

    @SerialName("width")
    val width: Int?,

    @SerialName("iso_639_1")
    val iso6391: String?,

    @SerialName("vote_count")
    val voteCount: Int?,

    @SerialName("height")
    val height: Int?
)
