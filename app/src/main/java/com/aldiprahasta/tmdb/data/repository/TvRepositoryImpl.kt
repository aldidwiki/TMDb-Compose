package com.aldiprahasta.tmdb.data.repository

import com.aldiprahasta.tmdb.data.source.remote.TvRemoteDataSource
import com.aldiprahasta.tmdb.data.source.remote.response.tv.TvResponse
import com.aldiprahasta.tmdb.domain.repository.TvRepository
import com.aldiprahasta.tmdb.utils.UiState
import kotlinx.coroutines.flow.Flow

class TvRepositoryImpl(private val tvRemoteDataSource: TvRemoteDataSource) : TvRepository {
    override fun getPopularTv(): Flow<UiState<TvResponse>> {
        return tvRemoteDataSource.getPopularTv()
    }
}