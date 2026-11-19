package com.aldiprahasta.tmdb.data.source.remote

import com.aldiprahasta.tmdb.data.source.remote.network.RemoteService
import com.aldiprahasta.tmdb.data.source.remote.response.CreditResponse
import com.aldiprahasta.tmdb.data.source.remote.response.GenreResponse
import com.aldiprahasta.tmdb.data.source.remote.response.ImageResponse
import com.aldiprahasta.tmdb.data.source.remote.response.tv.TvDetailResponse
import com.aldiprahasta.tmdb.data.source.remote.response.tv.TvSeasonResponse
import com.aldiprahasta.tmdb.utils.UiState
import kotlinx.coroutines.flow.Flow

class TvRemoteDataSource(private val remoteService: RemoteService) {
    fun getTvDetail(tvId: Int): Flow<UiState<TvDetailResponse>> = remoteRequest {
        remoteService.getTvDetail(tvId)
    }

    fun getTvCredits(tvId: Int): Flow<UiState<CreditResponse>> = remoteRequest {
        remoteService.getTvCredits(tvId)
    }

    fun getTvSeasonDetail(tvId: Int, tvSeasonNumber: Int): Flow<UiState<TvSeasonResponse>> = remoteRequest {
        remoteService.getTvSeasonDetail(tvId, tvSeasonNumber)
    }

    fun getTvGenres(): Flow<UiState<GenreResponse>> = remoteRequest {
        remoteService.getTvGenres()
    }

    fun getTvImages(tvId: Int): Flow<UiState<ImageResponse>> = remoteRequest {
        remoteService.getTvImages(tvId)
    }
}
