package com.aldiprahasta.tmdb.domain.usecase

import androidx.paging.PagingData
import androidx.paging.map
import com.aldiprahasta.tmdb.domain.model.MovieDomainModel
import com.aldiprahasta.tmdb.domain.repository.MovieRepository
import com.aldiprahasta.tmdb.utils.mapMovieResponseToMovieDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetPopularMovieList(private val movieRepository: MovieRepository) {
    operator fun invoke(): Flow<PagingData<MovieDomainModel>> = movieRepository.getPopularMovie()
            .map { pagingData ->
                pagingData.map { movieResponseModel ->
                    movieResponseModel.mapMovieResponseToMovieDomainModel()
                }
            }
}