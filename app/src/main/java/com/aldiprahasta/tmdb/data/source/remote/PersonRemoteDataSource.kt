package com.aldiprahasta.tmdb.data.source.remote

import com.aldiprahasta.tmdb.data.source.remote.network.RemoteService
import com.aldiprahasta.tmdb.data.source.remote.response.CreditResponse
import com.aldiprahasta.tmdb.data.source.remote.response.ImageResponse
import com.aldiprahasta.tmdb.data.source.remote.response.person.PersonResponse
import com.aldiprahasta.tmdb.utils.UiState
import kotlinx.coroutines.flow.Flow

class PersonRemoteDataSource(private val remoteService: RemoteService) {
    fun getPersonDetail(personId: Int): Flow<UiState<PersonResponse>> = remoteRequest {
        remoteService.getPersonDetail(personId)
    }

    fun getPersonCredits(personId: Int): Flow<UiState<CreditResponse>> = remoteRequest {
        remoteService.getPersonCredits(personId)
    }

    fun getPersonImages(personId: Int): Flow<UiState<ImageResponse>> = remoteRequest {
        remoteService.getPersonImages(personId)
    }
}
