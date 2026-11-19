package com.aldiprahasta.tmdb.utils

import com.aldiprahasta.tmdb.data.source.remote.response.ImageResponse
import com.aldiprahasta.tmdb.data.source.remote.response.VideoResponse
import com.aldiprahasta.tmdb.domain.model.ImageDomainModel
import com.aldiprahasta.tmdb.domain.model.VideoDomainModel

fun ImageResponse.mapBackdrops(): List<ImageDomainModel> = backdrops.orEmpty().map { image ->
    val width = image.width?.takeIf { it > 0 }
    val height = image.height?.takeIf { it > 0 }
    val ratio = image.aspectRatio?.toFloat()?.takeIf { it.isFinite() && it > 0f }
            ?: if (width != null && height != null) width.toFloat() / height else null
    ImageDomainModel(id, image.filePath, ratio)
}

fun ImageResponse.mapProfiles(): List<ImageDomainModel> = profiles.orEmpty().map { image ->
    ImageDomainModel(id, image.filePath, image.aspectRatio?.toFloat())
}

fun VideoResponse.mapVideoResponseToVideoDomainModelList(): List<VideoDomainModel> {
    return results?.map { model ->
        VideoDomainModel(
                id = model.id,
                name = model.name ?: "",
                site = model.site ?: "",
                type = model.type ?: "",
                key = model.key ?: "",
                isOfficial = model.official ?: false
        )
    } ?: emptyList()
}
