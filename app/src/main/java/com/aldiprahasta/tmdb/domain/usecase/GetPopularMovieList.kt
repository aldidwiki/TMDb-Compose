package com.aldiprahasta.tmdb.domain.usecase

import com.aldiprahasta.tmdb.domain.model.MovieDomainModel
import com.aldiprahasta.tmdb.domain.repository.MovieRepository
import com.aldiprahasta.tmdb.utils.UiState
import com.aldiprahasta.tmdb.utils.mapMovieResponseToMovieDomainModelList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetPopularMovieList @Inject constructor(private val movieRepository: MovieRepository) {
    operator fun invoke(): Flow<UiState<List<MovieDomainModel>>> = movieRepository.getPopularMovie()
            .map { state ->
                when (state) {
                    is UiState.Loading -> UiState.Loading
                    is UiState.Error -> UiState.Error(state.throwable, state.errorMessage)
                    is UiState.Success -> UiState.Success(
                            state.data.mapMovieResponseToMovieDomainModelList()
                    )
                }
            }
}