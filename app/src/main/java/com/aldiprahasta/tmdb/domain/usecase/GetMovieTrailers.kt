package com.aldiprahasta.tmdb.domain.usecase

import com.aldiprahasta.tmdb.domain.model.VideoDomainModel
import com.aldiprahasta.tmdb.domain.repository.MovieRepository
import com.aldiprahasta.tmdb.utils.UiState
import com.aldiprahasta.tmdb.utils.mapSuccess
import com.aldiprahasta.tmdb.utils.mapVideoResponseToVideoDomainModelList
import com.aldiprahasta.tmdb.utils.youtubeTrailers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetMovieTrailers(private val movieRepository: MovieRepository) {
    operator fun invoke(movieId: Int): Flow<UiState<List<VideoDomainModel>>> =
            movieRepository.getMovieVideos(movieId).map { state ->
                state.mapSuccess { it.mapVideoResponseToVideoDomainModelList().youtubeTrailers() }
            }
}
