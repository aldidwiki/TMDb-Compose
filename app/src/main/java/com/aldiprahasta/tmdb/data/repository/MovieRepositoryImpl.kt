package com.aldiprahasta.tmdb.data.repository

import com.aldiprahasta.tmdb.data.source.remote.MovieRemoteDataSource
import com.aldiprahasta.tmdb.data.source.remote.response.CreditResponse
import com.aldiprahasta.tmdb.data.source.remote.response.movie.MovieDetailResponse
import com.aldiprahasta.tmdb.data.source.remote.response.movie.MovieResponse
import com.aldiprahasta.tmdb.domain.repository.MovieRepository
import com.aldiprahasta.tmdb.utils.UiState
import kotlinx.coroutines.flow.Flow

class MovieRepositoryImpl(
        private val movieRemoteDataSource: MovieRemoteDataSource
) : MovieRepository {
    override fun getPopularMovie(): Flow<UiState<MovieResponse>> {
        return movieRemoteDataSource.getPopularMovies()
    }

    override fun getMovieDetail(movieId: Int): Flow<UiState<MovieDetailResponse>> {
        return movieRemoteDataSource.getMovieDetail(movieId)
    }

    override fun getMovieCredits(movieId: Int): Flow<UiState<CreditResponse>> {
        return movieRemoteDataSource.getMovieCredits(movieId)
    }
}