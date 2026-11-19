package com.aldiprahasta.tmdb.domain.usecase

import com.aldiprahasta.tmdb.domain.model.CastDomainModel
import com.aldiprahasta.tmdb.domain.repository.MovieRepository
import com.aldiprahasta.tmdb.utils.MediaType
import com.aldiprahasta.tmdb.utils.UiState
import com.aldiprahasta.tmdb.utils.mapCreditResponseToCastDomainModelList
import com.aldiprahasta.tmdb.utils.mapSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetMovieCredits(private val movieRepository: MovieRepository) {
    operator fun invoke(movieId: Int): Flow<UiState<List<CastDomainModel>>> = movieRepository
            .getMovieCredits(movieId).map { state ->
                state.mapSuccess { it.mapCreditResponseToCastDomainModelList(MediaType.MOVIE_TYPE) }
            }
}
