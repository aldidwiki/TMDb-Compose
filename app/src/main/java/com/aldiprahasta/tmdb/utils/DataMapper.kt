package com.aldiprahasta.tmdb.utils

import com.aldiprahasta.tmdb.data.source.remote.response.movie.MovieResponse
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