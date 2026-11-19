package com.aldiprahasta.tmdb.utils

import com.aldiprahasta.tmdb.data.source.local.entity.FavoriteEntity
import com.aldiprahasta.tmdb.domain.model.ContentDetailDomainModel
import com.aldiprahasta.tmdb.domain.model.FavoriteDomainModel
import com.aldiprahasta.tmdb.domain.model.PersonDomainModel

fun FavoriteEntity.mapEntityToDomainModel(): FavoriteDomainModel = FavoriteDomainModel(
        favoriteId = id,
        title = name,
        releaseDate = releaseDate,
        imagePoster = imagePath,
        mediaType = mediaType,
        addedAt = addedAt,
        voteAverage = voteAverage,
        voteCount = voteCount,
        genres = genres,
        seasonCount = seasonCount,
        knownFor = knownFor
)

fun FavoriteDomainModel.mapDomainModelToEntity(): FavoriteEntity = FavoriteEntity(
        id = favoriteId,
        name = title,
        releaseDate = releaseDate,
        imagePath = imagePoster,
        mediaType = mediaType,
        addedAt = addedAt,
        voteAverage = voteAverage,
        voteCount = voteCount,
        genres = genres,
        seasonCount = seasonCount,
        knownFor = knownFor
)

fun ContentDetailDomainModel.mapDomainModelToEntity(mediaType: String): FavoriteEntity {
    return FavoriteEntity(
            id = id,
            name = title,
            releaseDate = releaseDate,
            imagePath = posterPath,
            mediaType = mediaType,
            addedAt = System.currentTimeMillis(),
            voteAverage = voteAverage.takeUnless { voteCount == 0 },
            voteCount = voteCount,
            genres = genres,
            seasonCount = seasonCount
    )
}

fun PersonDomainModel.mapDomainModelToEntity(): FavoriteEntity {
    return FavoriteEntity(
            id = id,
            name = name,
            releaseDate = "",
            imagePath = profilePath,
            mediaType = MediaType.PERSON_TYPE.name,
            addedAt = System.currentTimeMillis(),
            knownFor = knownFor.takeUnless { it.isBlank() || it == "-" }
    )
}
