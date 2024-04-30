package com.aldiprahasta.tmdb.data.source.remote

import com.aldiprahasta.tmdb.data.source.remote.network.RemoteService
import com.aldiprahasta.tmdb.data.source.remote.response.tv.TvResponse
import com.aldiprahasta.tmdb.utils.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.HttpException
import timber.log.Timber

class TvRemoteDataSource(private val remoteService: RemoteService) {
    fun getPopularTv(): Flow<UiState<TvResponse>> = flow {
        emit(UiState.Loading)
        val response = remoteService.getPopularTv()
        if (!response.isSuccessful) throw HttpException(response)
        else response.body()?.let { tvResponse ->
            emit(UiState.Success(tvResponse))
        }
    }.catch { t ->
        Timber.e(t)
        emit(UiState.Error(t))
    }.flowOn(Dispatchers.IO)
}