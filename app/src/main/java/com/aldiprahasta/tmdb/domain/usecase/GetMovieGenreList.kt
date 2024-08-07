package com.aldiprahasta.tmdb.domain.usecase

import com.aldiprahasta.tmdb.domain.model.GenreDomainModel
import com.aldiprahasta.tmdb.domain.repository.MovieRepository
import com.aldiprahasta.tmdb.utils.UiState
import com.aldiprahasta.tmdb.utils.mapGenreResponseToDomainModelList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetMovieGenreList(private val movieRepository: MovieRepository) {
    operator fun invoke(): Flow<UiState<List<GenreDomainModel>>> = movieRepository.getMovieGenres()
            .map { state ->
                when (state) {
                    is UiState.Loading -> UiState.Loading

                    is UiState.Error -> UiState.Error(
                            state.throwable,
                            state.errorMessage
                    )

                    is UiState.Success -> UiState.Success(
                            state.data.mapGenreResponseToDomainModelList()
                    )
                }
            }
}