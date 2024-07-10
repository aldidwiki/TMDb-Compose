package com.aldiprahasta.tmdb.utils

import com.aldiprahasta.tmdb.data.source.remote.response.CreditResponse
import com.aldiprahasta.tmdb.data.source.remote.response.ExternalIdResponse
import com.aldiprahasta.tmdb.data.source.remote.response.VideoResponse
import com.aldiprahasta.tmdb.data.source.remote.response.movie.MovieDetailResponse
import com.aldiprahasta.tmdb.data.source.remote.response.movie.MovieResponseModel
import com.aldiprahasta.tmdb.data.source.remote.response.person.PersonResponse
import com.aldiprahasta.tmdb.data.source.remote.response.tv.NetworksItemResponse
import com.aldiprahasta.tmdb.data.source.remote.response.tv.TvDetailResponse
import com.aldiprahasta.tmdb.data.source.remote.response.tv.TvEpisodeItemResponse
import com.aldiprahasta.tmdb.data.source.remote.response.tv.TvResponseModel
import com.aldiprahasta.tmdb.data.source.remote.response.tv.TvSeasonItemResponse
import com.aldiprahasta.tmdb.data.source.remote.response.tv.TvSeasonResponse
import com.aldiprahasta.tmdb.domain.model.CastDomainModel
import com.aldiprahasta.tmdb.domain.model.ContentDetailDomainModel
import com.aldiprahasta.tmdb.domain.model.ExternalIdDomainModel
import com.aldiprahasta.tmdb.domain.model.MovieDomainModel
import com.aldiprahasta.tmdb.domain.model.NetworkDomainModel
import com.aldiprahasta.tmdb.domain.model.PersonDomainModel
import com.aldiprahasta.tmdb.domain.model.TvDomainModel
import com.aldiprahasta.tmdb.domain.model.TvEpisodeDomainModel
import com.aldiprahasta.tmdb.domain.model.TvSeasonDetailDomainModel
import com.aldiprahasta.tmdb.domain.model.TvSeasonDomainModel
import com.aldiprahasta.tmdb.domain.model.VideoDomainModel

fun MovieResponseModel.mapMovieResponseToMovieDomainModel(): MovieDomainModel {
    return MovieDomainModel(
            title = title ?: "",
            posterPath = posterPath,
            releaseDate = releaseDate?.convertDate() ?: "",
            movieId = id
    )
}

fun MovieDetailResponse.mapMovieDetailResponseToMovieDetailDomainModel(): ContentDetailDomainModel {
    return ContentDetailDomainModel(
            id = id,
            title = title ?: "",
            posterPath = posterPath,
            releaseDate = releaseDate?.convertDate() ?: "",
            runtime = runtime.runtimeFormat(),
            tagline = tagline ?: "",
            overview = overview ?: "",
            voteAverage = voteAverage ?: 0.0,
            genres = genres.convertGenreToSingleText(),
            backdropPath = backdropPath,
            casts = credits?.mapCreditResponseToCastDomainModelList(MediaType.MOVIE_TYPE)
                    ?: emptyList(),
            certification = (releaseDates?.results?.firstOrNull { item ->
                item.iso31661 == "US"
            }?.releaseDates?.firstOrNull()?.certification ?: "NR").ifEmpty { "NR" },
            budget = (budget ?: 0L).formatCurrency(),
            revenue = (revenue ?: 0L).formatCurrency(),
            originalLanguage = (originalLanguage ?: "").getLanguageDisplayName(),
            status = status ?: "",
            externalId = externalIds.mapExternalIdResponseToExternalIdDomainModel(),
            videos = video?.mapVideoResponseToVideoDomainModelList() ?: emptyList(),
            networks = null,
            type = null
    )
}

fun PersonResponse.mapPersonResponseToPersonDomainModel(): PersonDomainModel {
    return PersonDomainModel(
            profilePath = profilePath,
            name = name ?: "",
            birthDay = birthday?.convertDate() ?: "",
            deathDay = deathday?.convertDate() ?: "",
            gender = gender?.formatGender() ?: "-",
            biography = biography ?: "-",
            knownFor = knownForDepartment ?: "-",
            age = birthday?.let { getAge(it, deathday) } ?: "-",
            placeOfBirth = placeOfBirth ?: "-",
            externalIds = externalIds.mapExternalIdResponseToExternalIdDomainModel(),
            credits = combinedCredits?.mapCreditResponseToCastDomainModelList(MediaType.PERSON_TYPE)
                    ?: emptyList()
    )
}

fun CreditResponse.mapCreditResponseToCastDomainModelList(mediaType: MediaType): List<CastDomainModel> {
    return casts?.sortedByDescending { it.releaseDate }?.map { castResponseModel ->
        when (mediaType) {
            MediaType.MOVIE_TYPE -> CastDomainModel(
                    name = castResponseModel.name ?: "",
                    characterName = castResponseModel.character ?: "",
                    imagePath = castResponseModel.profilePath ?: "",
                    order = castResponseModel.order,
                    id = castResponseModel.id,
                    mediaType = null,
                    releaseDate = null,
                    totalEpisodeCount = null
            )

            MediaType.PERSON_TYPE -> CastDomainModel(
                    name = castResponseModel.title ?: "",
                    characterName = castResponseModel.character ?: "",
                    imagePath = castResponseModel.posterPath ?: "",
                    order = castResponseModel.order,
                    id = castResponseModel.id,
                    mediaType = castResponseModel.mediaType,
                    releaseDate = castResponseModel.releaseDate?.convertDate(),
                    totalEpisodeCount = null
            )

            MediaType.TV_TYPE -> CastDomainModel(
                    name = castResponseModel.name ?: "",
                    characterName = castResponseModel.roles?.firstOrNull()?.character ?: "",
                    imagePath = castResponseModel.profilePath ?: "",
                    order = castResponseModel.order,
                    id = castResponseModel.id,
                    mediaType = null,
                    releaseDate = null,
                    totalEpisodeCount = castResponseModel.totalEpisodeCount
            )
        }

    } ?: emptyList()
}

fun TvResponseModel.mapTvResponseToTvDomainModel(): TvDomainModel {
    return TvDomainModel(
            posterPath = posterPath,
            tvId = id,
            title = name ?: "",
            releaseDate = firstAirDate?.convertDate() ?: ""
    )
}

fun TvDetailResponse.mapTvDetailResponseToContentDetailDomainModel(): ContentDetailDomainModel =
        ContentDetailDomainModel(
                title = name ?: "",
                posterPath = posterPath,
                releaseDate = firstAirDate?.convertDate() ?: "",
                runtime = "",
                tagline = tagline ?: "",
                overview = overview ?: "",
                id = id,
                voteAverage = voteAverage ?: 0.0,
                genres = genres.convertGenreToSingleText(),
                certification = contentRatings.results?.firstOrNull {
                    it.code == "US"
                }?.rating ?: "NR",
                backdropPath = backdropPath,
                budget = null,
                revenue = null,
                originalLanguage = (originalLanguage ?: "").getLanguageDisplayName(),
                status = status ?: "",
                externalId = externalIds.mapExternalIdResponseToExternalIdDomainModel(),
                casts = credits?.mapCreditResponseToCastDomainModelList(MediaType.TV_TYPE)
                        ?: emptyList(),
                videos = videos?.mapVideoResponseToVideoDomainModelList() ?: emptyList(),
                networks = networks?.mapNetworkItemsToNetworkDomainModel(),
                type = type,
                seasons = seasons?.mapTvSeasonItemResponseToDomainModelList()
        )

fun TvSeasonResponse.mapTvSeasonResponseToDomainModel(): TvSeasonDetailDomainModel {
    return TvSeasonDetailDomainModel(
            tvSeasonDomainModel = TvSeasonDomainModel(
                    seasonId = id,
                    seasonName = name ?: "",
                    seasonPosterPath = posterPath,
                    seasonAirDate = airDate?.convertDate() ?: "",
                    seasonVoteAverage = voteAverage ?: 0.0,
                    totalEpisodes = 0,
                    seasonNumber = seasonNumber ?: 0
            ),
            tvEpisodeList = episodes.mapTvEpisodeItemResponseToDomainModelList()
    )
}

private fun List<NetworksItemResponse>.mapNetworkItemsToNetworkDomainModel(): List<NetworkDomainModel> {
    return map { networksItem ->
        NetworkDomainModel(
                networkId = networksItem.id,
                networkLogoPath = networksItem.logoPath,
                networkName = networksItem.name ?: ""
        )
    }
}

private fun ExternalIdResponse.mapExternalIdResponseToExternalIdDomainModel(): ExternalIdDomainModel {
    return ExternalIdDomainModel(
            instragramId = instagramId,
            facebookId = facebookId,
            imdbId = imdbId,
            twitterId = twitterId
    )
}

private fun VideoResponse.mapVideoResponseToVideoDomainModelList(): List<VideoDomainModel> {
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

private fun List<TvSeasonItemResponse>.mapTvSeasonItemResponseToDomainModelList(): List<TvSeasonDomainModel> {
    return this.map { item ->
        TvSeasonDomainModel(
                seasonId = item.id,
                seasonName = item.name ?: "",
                seasonPosterPath = item.posterPath,
                seasonAirDate = item.airDate?.convertDate() ?: "",
                seasonVoteAverage = item.voteAverage ?: 0.0,
                totalEpisodes = item.episodeCount ?: 0,
                seasonNumber = item.seasonNumber ?: 0
        )
    }
}

private fun List<TvEpisodeItemResponse>.mapTvEpisodeItemResponseToDomainModelList(): List<TvEpisodeDomainModel> {
    return this.map { item ->
        TvEpisodeDomainModel(
                id = item.id,
                name = item.name ?: "",
                stillPath = item.stillPath,
                runtime = item.runtime ?: 0,
                overview = item.overview ?: "",
                voteAverage = item.voteAverage ?: 0.0,
                episodeNumber = item.episodeNumber ?: 0,
                airDate = item.airDate?.convertDate() ?: ""
        )
    }
}