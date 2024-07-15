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
import retrofit2.HttpException
import timber.log.Timber

class TvRemoteDataSource(private val remoteService: RemoteService) {
    fun getTvDetail(tvId: Int): Flow<UiState<TvDetailResponse>> = flow {
        emit(UiState.Loading)
        val response = remoteService.getTvDetail(tvId)
        if (!response.isSuccessful) throw HttpException(response)
        else response.body()?.let { tvDetailResponse ->
            emit(UiState.Success(tvDetailResponse))
        }
    }.catch { t ->
        Timber.e(t)
        emit(UiState.Error(t))
    }.flowOn(Dispatchers.IO)

    fun getTvCredits(tvId: Int): Flow<UiState<CreditResponse>> = flow {
        emit(UiState.Loading)
        val response = remoteService.getTvCredits(tvId)
        if (!response.isSuccessful) throw HttpException(response)
        else response.body()?.let { creditResponse ->
            emit(UiState.Success(creditResponse))
        }
    }.catch { t ->
        Timber.e(t)
        UiState.Error(t)
    }.flowOn(Dispatchers.IO)

    fun getTvSeasonDetail(tvId: Int, tvSeasonNumber: Int): Flow<UiState<TvSeasonResponse>> = flow {
        emit(UiState.Loading)
        val response = remoteService.getTvSeasonDetail(tvId, tvSeasonNumber)
        if (!response.isSuccessful) throw HttpException(response)
        else response.body()?.let { tvSeasonResponse ->
            emit(UiState.Success(tvSeasonResponse))
        }
    }.catch { t ->
        Timber.e(t)
        UiState.Error(t)
    }.flowOn(Dispatchers.IO)

    fun getTvGenres(): Flow<UiState<GenreResponse>> = flow {
        emit(UiState.Loading)
        val response = remoteService.getTvGenres()
        if (!response.isSuccessful) throw HttpException(response)
        else response.body()?.let { genreResponse ->
            emit(UiState.Success(genreResponse))
        }
    }.catch { t ->
        Timber.e(t)
        UiState.Error(t)
    }.flowOn(Dispatchers.IO)
}