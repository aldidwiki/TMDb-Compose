package com.aldiprahasta.tmdb.data.source.remote

import com.aldiprahasta.tmdb.data.source.remote.network.RemoteService
import com.aldiprahasta.tmdb.data.source.remote.response.CreditResponse
import com.aldiprahasta.tmdb.data.source.remote.response.GenreResponse
import com.aldiprahasta.tmdb.data.source.remote.response.ImageResponse
import com.aldiprahasta.tmdb.data.source.remote.response.movie.MovieDetailResponse
import com.aldiprahasta.tmdb.utils.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber

class MovieRemoteDataSource(private val remoteService: RemoteService) {
    fun getMovieDetail(movieId: Int): Flow<UiState<MovieDetailResponse>> = flow {
        emit(UiState.Loading)
        val response = remoteService.getMovieDetail(movieId)
        response?.let { movieDetailResponse ->
            emit(UiState.Success(movieDetailResponse))
        }
    }.catch { t ->
        Timber.e(t)
        emit(UiState.Error(t))
    }.flowOn(Dispatchers.IO)

    fun getMovieCredits(movieId: Int): Flow<UiState<CreditResponse>> = flow {
        emit(UiState.Loading)
        val response = remoteService.getMovieCredits(movieId)
        response?.let { creditResponse ->
            emit(UiState.Success(creditResponse))
        }
    }.catch { t ->
        Timber.e(t)
        emit(UiState.Error(t))
    }.flowOn(Dispatchers.IO)

    fun getMovieGenres(): Flow<UiState<GenreResponse>> = flow {
        emit(UiState.Loading)
        val response = remoteService.getMovieGenres()
        response?.let { genreResponse ->
            emit(UiState.Success(genreResponse))
        }
    }.catch { t ->
        Timber.e(t)
        emit(UiState.Error(t))
    }.flowOn(Dispatchers.IO)

    fun getMovieImages(movieId: Int): Flow<UiState<ImageResponse>> = flow {
        emit(UiState.Loading)
        val response = remoteService.getMovieImages(movieId)
        response?.let { imageResponse ->
            emit(UiState.Success(imageResponse))
        }
    }.catch { t ->
        Timber.e(t)
        emit(UiState.Error(t))
    }.flowOn(Dispatchers.IO)
}