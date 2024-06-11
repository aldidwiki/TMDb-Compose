package com.aldiprahasta.tmdb.domain.usecase

import com.aldiprahasta.tmdb.domain.model.ContentDetailDomainModel
import com.aldiprahasta.tmdb.domain.repository.MovieRepository
import com.aldiprahasta.tmdb.utils.UiState
import com.aldiprahasta.tmdb.utils.mapMovieDetailResponseToMovieDetailDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetMovieDetail(private val movieRepository: MovieRepository) {
    operator fun invoke(movieId: Int): Flow<UiState<ContentDetailDomainModel>> = movieRepository
            .getMovieDetail(movieId).map { state ->
                when (state) {
                    is UiState.Loading -> UiState.Loading
                    is UiState.Error -> UiState.Error(state.throwable, state.errorMessage)
                    is UiState.Success -> UiState.Success(
                            state.data.mapMovieDetailResponseToMovieDetailDomainModel()
                    )
                }
            }
}