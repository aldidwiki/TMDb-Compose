package com.aldiprahasta.tmdb.data.source.remote

import com.aldiprahasta.tmdb.data.source.remote.network.RemoteService
import com.aldiprahasta.tmdb.data.source.remote.response.CreditResponse
import com.aldiprahasta.tmdb.data.source.remote.response.ImageResponse
import com.aldiprahasta.tmdb.data.source.remote.response.person.PersonResponse
import com.aldiprahasta.tmdb.utils.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber

class PersonRemoteDataSource(private val remoteService: RemoteService) {
    fun getPersonDetail(personId: Int): Flow<UiState<PersonResponse>> = request {
        remoteService.getPersonDetail(personId)
    }

    fun getPersonCredits(personId: Int): Flow<UiState<CreditResponse>> = request {
        remoteService.getPersonCredits(personId)
    }

    fun getPersonImages(personId: Int): Flow<UiState<ImageResponse>> = request {
        remoteService.getPersonImages(personId)
    }

    private fun <T : Any> request(call: suspend () -> T?): Flow<UiState<T>> = flow {
        emit(UiState.Loading)
        emit(UiState.Success(checkNotNull(call()) { "TMDb returned an empty response" }))
    }.catch { error ->
        Timber.e(error)
        emit(UiState.Error(error))
    }.flowOn(Dispatchers.IO)
}
