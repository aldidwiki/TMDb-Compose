package com.aldiprahasta.tmdb.data.source.remote

import com.aldiprahasta.tmdb.data.source.remote.network.RemoteService
import com.aldiprahasta.tmdb.data.source.remote.response.CreditResponse
import com.aldiprahasta.tmdb.data.source.remote.response.GenreResponse
import com.aldiprahasta.tmdb.data.source.remote.response.tv.TvDetailResponse
import com.aldiprahasta.tmdb.data.source.remote.response.tv.TvSeasonResponse
import com.aldiprahasta.tmdb.utils.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber

class TvRemoteDataSource(private val remoteService: RemoteService) {
    fun getTvDetail(tvId: Int): Flow<UiState<TvDetailResponse>> = request {
        remoteService.getTvDetail(tvId)
    }

    fun getTvCredits(tvId: Int): Flow<UiState<CreditResponse>> = request {
        remoteService.getTvCredits(tvId)
    }

    fun getTvSeasonDetail(tvId: Int, tvSeasonNumber: Int): Flow<UiState<TvSeasonResponse>> = request {
        remoteService.getTvSeasonDetail(tvId, tvSeasonNumber)
    }

    fun getTvGenres(): Flow<UiState<GenreResponse>> = request {
        remoteService.getTvGenres()
    }

    private fun <T : Any> request(call: suspend () -> T?): Flow<UiState<T>> = flow {
        emit(UiState.Loading)
        emit(UiState.Success(checkNotNull(call()) { "TMDb returned an empty response" }))
    }.catch { throwable ->
        Timber.e(throwable)
        emit(UiState.Error(throwable))
    }.flowOn(Dispatchers.IO)
}
