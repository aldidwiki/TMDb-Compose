package com.aldiprahasta.tmdb.domain.usecase

import com.aldiprahasta.tmdb.domain.model.ContentDetailDomainModel
import com.aldiprahasta.tmdb.domain.repository.MovieRepository
import com.aldiprahasta.tmdb.utils.UiState
import com.aldiprahasta.tmdb.utils.mapMovieDetailResponseToMovieDetailDomainModel
import com.aldiprahasta.tmdb.utils.mapSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetMovieDetail(private val movieRepository: MovieRepository) {
    operator fun invoke(movieId: Int): Flow<UiState<ContentDetailDomainModel>> = movieRepository
            .getMovieDetail(movieId).map { state ->
                state.mapSuccess { it.mapMovieDetailResponseToMovieDetailDomainModel() }
            }
}
