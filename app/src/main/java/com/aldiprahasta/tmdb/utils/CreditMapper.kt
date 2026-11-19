package com.aldiprahasta.tmdb.utils

import com.aldiprahasta.tmdb.data.source.remote.response.CreditResponse
import com.aldiprahasta.tmdb.domain.model.CastDomainModel

fun CreditResponse.mapCreditResponseToCastDomainModelList(mediaType: MediaType): List<CastDomainModel> {
    val cast = casts.orEmpty()
    val orderedCast = if (mediaType == MediaType.PERSON_TYPE) cast else cast.sortedBy { it.order ?: Int.MAX_VALUE }
    return orderedCast.map { castResponseModel ->
        when (mediaType) {
            MediaType.MOVIE_TYPE -> CastDomainModel(
                    name = castResponseModel.name ?: "",
                    characterName = castResponseModel.character ?: "",
                    imagePath = castResponseModel.profilePath ?: "",
                    order = castResponseModel.order ?: 0,
                    id = castResponseModel.id,
                    mediaType = null,
                    releaseDate = null,
                    totalEpisodeCount = null
            )

            MediaType.PERSON_TYPE -> CastDomainModel(
                    name = castResponseModel.title ?: castResponseModel.name ?: "",
                    characterName = castResponseModel.character?.ifEmpty { "Self" } ?: "",
                    imagePath = castResponseModel.posterPath ?: "",
                    order = castResponseModel.order ?: 0,
                    id = castResponseModel.id,
                    mediaType = castResponseModel.mediaType,
                    releaseDate = castResponseModel.releaseDate?.convertDate()
                            ?: castResponseModel.firstAirDate?.convertDate() ?: "",
                    totalEpisodeCount = castResponseModel.episodeCount,
                    genreIds = castResponseModel.genreIds,
                    popularity = castResponseModel.popularity,
                    voteAverage = castResponseModel.voteAverage?.takeUnless { castResponseModel.voteCount == 0 }
            )

            MediaType.TV_TYPE -> CastDomainModel(
                    name = castResponseModel.name ?: "",
                    characterName = castResponseModel.roles.orEmpty()
                            .firstNotNullOfOrNull { it.character?.takeIf(String::isNotBlank) }
                            ?: castResponseModel.character.orEmpty(),
                    imagePath = castResponseModel.profilePath ?: "",
                    order = castResponseModel.order ?: 0,
                    id = castResponseModel.id,
                    mediaType = null,
                    releaseDate = null,
                    totalEpisodeCount = castResponseModel.totalEpisodeCount
            )
        }
    }
}
