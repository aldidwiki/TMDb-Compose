package com.aldiprahasta.tmdb.utils

import com.aldiprahasta.tmdb.data.source.remote.response.movie.MovieDetailResponse
import com.aldiprahasta.tmdb.data.source.remote.response.movie.MovieResponse
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
            voteAverage = voteAverage ?: 0.0
    )
}