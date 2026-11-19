package com.aldiprahasta.tmdb.domain.usecase

import com.aldiprahasta.tmdb.domain.model.ImageDomainModel
import com.aldiprahasta.tmdb.domain.repository.MovieRepository
import com.aldiprahasta.tmdb.utils.UiState
import com.aldiprahasta.tmdb.utils.mapBackdrops
import com.aldiprahasta.tmdb.utils.mapSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetMovieImages(private val movieRepository: MovieRepository) {
    operator fun invoke(movieId: Int): Flow<UiState<List<ImageDomainModel>>> = movieRepository
            .getMovieImages(movieId).map { state ->
                state.mapSuccess { it.mapBackdrops() }
            }
}
