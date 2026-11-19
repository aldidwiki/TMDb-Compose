package com.aldiprahasta.tmdb.domain.usecase

import com.aldiprahasta.tmdb.domain.model.MovieDomainModel
import com.aldiprahasta.tmdb.domain.repository.MovieRepository
import com.aldiprahasta.tmdb.utils.UiState
import com.aldiprahasta.tmdb.utils.mapMovieResponseToMovieDomainModel
import com.aldiprahasta.tmdb.utils.mapSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetNowPlayingMovies(private val movieRepository: MovieRepository) {
    operator fun invoke(region: String): Flow<UiState<List<MovieDomainModel>>> = movieRepository
            .getNowPlayingMovies(region)
            .map { state ->
                state.mapSuccess { response ->
                    response.movieResponseModelList.map { it.mapMovieResponseToMovieDomainModel() }
                }
            }
}
