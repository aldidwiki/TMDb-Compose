package com.aldiprahasta.tmdb.domain.repository

import androidx.paging.PagingData
import com.aldiprahasta.tmdb.data.source.remote.response.CreditResponse
import com.aldiprahasta.tmdb.data.source.remote.response.GenreResponse
import com.aldiprahasta.tmdb.data.source.remote.response.tv.TvDetailResponse
import com.aldiprahasta.tmdb.data.source.remote.response.tv.TvResponseModel
import com.aldiprahasta.tmdb.data.source.remote.response.tv.TvSeasonResponse
import com.aldiprahasta.tmdb.utils.UiState
import kotlinx.coroutines.flow.Flow

interface TvRepository {
    fun getPopularTv(): Flow<PagingData<TvResponseModel>>

    fun getTvDetail(tvId: Int): Flow<UiState<TvDetailResponse>>

    fun getTvCredits(tvId: Int): Flow<UiState<CreditResponse>>

    fun getTvSeasonDetail(tvId: Int, tvSeasonNumber: Int): Flow<UiState<TvSeasonResponse>>

    fun getTvGenres(): Flow<UiState<GenreResponse>>
}