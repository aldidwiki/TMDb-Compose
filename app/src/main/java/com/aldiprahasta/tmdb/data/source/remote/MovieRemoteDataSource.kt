package com.aldiprahasta.tmdb.data.source.remote

import com.aldiprahasta.tmdb.data.source.remote.network.RemoteService
import com.aldiprahasta.tmdb.data.source.remote.response.CreditResponse
import com.aldiprahasta.tmdb.data.source.remote.response.movie.MovieDetailResponse
import com.aldiprahasta.tmdb.utils.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.HttpException
import timber.log.Timber

class MovieRemoteDataSource(private val remoteService: RemoteService) {
    fun getMovieDetail(movieId: Int): Flow<UiState<MovieDetailResponse>> = flow {
        emit(UiState.Loading)
        val response = remoteService.getMovieDetail(movieId)
        if (!response.isSuccessful) throw HttpException(response)
        else response.body()?.let { movieDetailResponse ->
            emit(UiState.Success(movieDetailResponse))
        }
    }.catch { t ->
        Timber.e(t)
        emit(UiState.Error(t))
    }.flowOn(Dispatchers.IO)

    fun getMovieCredits(movieId: Int): Flow<UiState<CreditResponse>> = flow {
        emit(UiState.Loading)
        val response = remoteService.getMovieCredits(movieId)
        if (!response.isSuccessful) throw HttpException(response)
        else response.body()?.let { creditResponse ->
            emit(UiState.Success(creditResponse))
        }
    }.catch { t ->
        Timber.e(t)
        emit(UiState.Error(t))
    }.flowOn(Dispatchers.IO)
}