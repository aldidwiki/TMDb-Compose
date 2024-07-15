package com.aldiprahasta.tmdb.domain.repository

import androidx.paging.PagingData
import com.aldiprahasta.tmdb.data.source.remote.response.CreditResponse
import com.aldiprahasta.tmdb.data.source.remote.response.GenreResponse
import com.aldiprahasta.tmdb.data.source.remote.response.movie.MovieDetailResponse
import com.aldiprahasta.tmdb.data.source.remote.response.movie.MovieResponseModel
import com.aldiprahasta.tmdb.utils.UiState
import kotlinx.coroutines.flow.Flow

interface MovieRepository {
    fun getPopularMovie(): Flow<PagingData<MovieResponseModel>>
    fun getMovieDetail(movieId: Int): Flow<UiState<MovieDetailResponse>>
    fun getMovieCredits(movieId: Int): Flow<UiState<CreditResponse>>
    fun getMovieGenres(): Flow<UiState<GenreResponse>>
}