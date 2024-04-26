package com.aldiprahasta.tmdb.data.source.remote

import com.aldiprahasta.tmdb.data.source.remote.network.RemoteService
import com.aldiprahasta.tmdb.data.source.remote.response.person.PersonResponse
import com.aldiprahasta.tmdb.utils.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.HttpException
import timber.log.Timber

class PersonRemoteDataSource(private val remoteService: RemoteService) {
    fun getPersonDetail(personId: Int): Flow<UiState<PersonResponse>> = flow {
        emit(UiState.Loading)
        val response = remoteService.getPersonDetail(personId)
        if (!response.isSuccessful) throw HttpException(response)
        else response.body()?.let { personResponse ->
            emit(UiState.Success(personResponse))
        }
    }.catch { t ->
        Timber.e(t)
        emit(UiState.Error(t))
    }.flowOn(Dispatchers.IO)
}