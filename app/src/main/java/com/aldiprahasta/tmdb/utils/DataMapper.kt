package com.aldiprahasta.tmdb.utils

import com.aldiprahasta.tmdb.data.source.remote.response.CreditResponse
import com.aldiprahasta.tmdb.data.source.remote.response.ExternalIdResponse
import com.aldiprahasta.tmdb.data.source.remote.response.VideoResponse
import com.aldiprahasta.tmdb.data.source.remote.response.movie.MovieDetailResponse
import com.aldiprahasta.tmdb.data.source.remote.response.movie.MovieResponse
import com.aldiprahasta.tmdb.data.source.remote.response.person.PersonResponse
import com.aldiprahasta.tmdb.domain.model.CastDomainModel
import com.aldiprahasta.tmdb.domain.model.ExternalIdDomainModel
import com.aldiprahasta.tmdb.domain.model.MovieDetailDomainModel
import com.aldiprahasta.tmdb.domain.model.MovieDomainModel
import com.aldiprahasta.tmdb.domain.model.PersonDomainModel
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

fun MovieDetailResponse.mapMovieDetailResponseToMovieDetailDomainModel(): MovieDetailDomainModel {
    return MovieDetailDomainModel(
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
            videos = video?.mapVideoResponseToVideoDomainModelList() ?: emptyList()
    )
}

fun PersonResponse.mapPersonResponseToPersonDomainModel(): PersonDomainModel {
    return PersonDomainModel(
            profilePath = profilePath,
            name = name ?: "",
            birthDay = birthday ?: "",
            deathDay = deathday ?: "",
            gender = gender?.formatGender() ?: "-",
            biography = biography ?: "-",
            knownFor = knownForDepartment ?: "-",
            age = getAge(birthday ?: "", deathday),
            placeOfBirth = placeOfBirth ?: "-"
    )
}

private fun CreditResponse.mapCreditResponseToCastDomainModelList(): List<CastDomainModel> {
    return casts?.map { castResponseModel ->
        CastDomainModel(
                name = castResponseModel.name ?: "",
                characterName = castResponseModel.character ?: "",
                profilePath = castResponseModel.profilePath,
                order = castResponseModel.order
        )
    } ?: emptyList()
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