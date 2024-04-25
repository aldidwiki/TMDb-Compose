package com.aldiprahasta.tmdb.utils

import com.aldiprahasta.tmdb.data.source.remote.response.CreditResponse
import com.aldiprahasta.tmdb.data.source.remote.response.ExternalIdResponse
import com.aldiprahasta.tmdb.data.source.remote.response.movie.MovieDetailResponse
import com.aldiprahasta.tmdb.data.source.remote.response.movie.MovieResponse
import com.aldiprahasta.tmdb.domain.model.CastDomainModel
import com.aldiprahasta.tmdb.domain.model.ExternalIdDomainModel
import com.aldiprahasta.tmdb.domain.model.MovieDetailDomainModel
import com.aldiprahasta.tmdb.domain.model.MovieDomainModel

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
            externalId = externalIds.mapExternalIdResponseToExternalIdDomainModel()
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