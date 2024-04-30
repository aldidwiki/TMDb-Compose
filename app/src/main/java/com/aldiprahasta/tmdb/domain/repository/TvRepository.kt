package com.aldiprahasta.tmdb.domain.repository

import com.aldiprahasta.tmdb.data.source.remote.response.tv.TvResponse
import com.aldiprahasta.tmdb.utils.UiState
import kotlinx.coroutines.flow.Flow

interface TvRepository {
    fun getPopularTv(): Flow<UiState<TvResponse>>
}