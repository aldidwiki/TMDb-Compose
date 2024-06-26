package com.aldiprahasta.tmdb.domain.repository

import com.aldiprahasta.tmdb.data.source.remote.response.CreditResponse
import com.aldiprahasta.tmdb.data.source.remote.response.movie.MovieDetailResponse
import com.aldiprahasta.tmdb.data.source.remote.response.movie.MovieResponse
import com.aldiprahasta.tmdb.utils.UiState
import kotlinx.coroutines.flow.Flow

interface MovieRepository {
    fun getPopularMovie(): Flow<UiState<MovieResponse>>
    fun getMovieDetail(movieId: Int): Flow<UiState<MovieDetailResponse>>
    fun getMovieCredits(movieId: Int): Flow<UiState<CreditResponse>>
}