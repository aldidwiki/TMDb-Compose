package com.aldiprahasta.tmdb.utils

import com.aldiprahasta.tmdb.data.source.remote.response.CreditResponse
import com.aldiprahasta.tmdb.data.source.remote.response.ExternalIdResponse
import com.aldiprahasta.tmdb.data.source.remote.response.VideoResponse
import com.aldiprahasta.tmdb.data.source.remote.response.movie.MovieDetailResponse
import com.aldiprahasta.tmdb.data.source.remote.response.movie.MovieResponse
import com.aldiprahasta.tmdb.data.source.remote.response.person.PersonResponse
import com.aldiprahasta.tmdb.data.source.remote.response.tv.NetworksItem
import com.aldiprahasta.tmdb.data.source.remote.response.tv.TvDetailResponse
import com.aldiprahasta.tmdb.data.source.remote.response.tv.TvResponse
import com.aldiprahasta.tmdb.domain.model.CastDomainModel
import com.aldiprahasta.tmdb.domain.model.ContentDetailDomainModel
import com.aldiprahasta.tmdb.domain.model.ExternalIdDomainModel
import com.aldiprahasta.tmdb.domain.model.MovieDomainModel
import com.aldiprahasta.tmdb.domain.model.NetworkDomainModel
import com.aldiprahasta.tmdb.domain.model.PersonDomainModel
import com.aldiprahasta.tmdb.domain.model.TvDomainModel
import com.aldiprahasta.tmdb.domain.model.VideoDomainModel

fun MovieResponse.mapMovieResponseToMovieDomainModelList(): List<MovieDomainModel> {
    return this.movieResponseModelList.map { movieResponseModel ->
        MovieDomainModel(
                title = movieResponseModel.title ?: "",
                posterPath = movieResponseModel.posterPath,
                releaseDate = movieResponseModel.releaseDate?.convertDate() ?: "",
                movieId = movieResponseModel.id
        )
    }
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
            casts = credits?.mapCreditResponseToCastDomainModelList() ?: emptyList(),
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
            credits = combinedCredits?.mapCreditResponseToCastDomainModelList() ?: emptyList()
    )
}

fun CreditResponse.mapCreditResponseToCastDomainModelList(): List<CastDomainModel> {
    return casts?.map { castResponseModel ->
        CastDomainModel(
                name = castResponseModel.name ?: castResponseModel.title ?: "",
                characterName = castResponseModel.character ?: "",
                imagePath = castResponseModel.profilePath ?: castResponseModel.posterPath ?: "",
                order = castResponseModel.order,
                id = castResponseModel.id,
                mediaType = castResponseModel.mediaType,
                releaseDate = castResponseModel.releaseDate?.convertDate()
        )
    } ?: emptyList()
}

fun TvResponse.mapTvResponseToTvDomainModelList(): List<TvDomainModel> {
    return tvList.map { tvResponseModel ->
        TvDomainModel(
                posterPath = tvResponseModel.posterPath,
                tvId = tvResponseModel.id,
                title = tvResponseModel.name ?: "",
                releaseDate = tvResponseModel.firstAirDate?.convertDate() ?: ""
        )
    }
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
                certification = "",
                backdropPath = backdropPath,
                budget = null,
                revenue = null,
                originalLanguage = (originalLanguage ?: "").getLanguageDisplayName(),
                status = status ?: "",
                externalId = externalIds.mapExternalIdResponseToExternalIdDomainModel(),
                casts = emptyList(),
                videos = videos?.mapVideoResponseToVideoDomainModelList() ?: emptyList(),
                networks = networks?.mapNetworkItemsToNetworkDomainModel(),
                type = type
        )

private fun List<NetworksItem>.mapNetworkItemsToNetworkDomainModel(): List<NetworkDomainModel> {
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