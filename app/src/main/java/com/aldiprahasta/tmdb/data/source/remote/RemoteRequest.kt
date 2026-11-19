package com.aldiprahasta.tmdb.data.source.remote

import com.aldiprahasta.tmdb.utils.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber

internal fun <T : Any> remoteRequest(call: suspend () -> T?): Flow<UiState<T>> =
        flow<UiState<T>> {
            emit(UiState.Loading)
            emit(UiState.Success(checkNotNull(call()) { "TMDb returned an empty response" }))
        }.catch { error ->
            Timber.e(error)
            emit(UiState.Error(error))
        }.flowOn(Dispatchers.IO)
