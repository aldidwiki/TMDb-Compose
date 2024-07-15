package com.aldiprahasta.tmdb.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.aldiprahasta.tmdb.data.source.paging.PopularTvPagingSource
import com.aldiprahasta.tmdb.data.source.remote.TvRemoteDataSource
import com.aldiprahasta.tmdb.data.source.remote.network.RemoteService
import com.aldiprahasta.tmdb.data.source.remote.response.CreditResponse
import com.aldiprahasta.tmdb.data.source.remote.response.GenreResponse
import com.aldiprahasta.tmdb.data.source.remote.response.tv.TvDetailResponse
import com.aldiprahasta.tmdb.data.source.remote.response.tv.TvResponseModel
import com.aldiprahasta.tmdb.data.source.remote.response.tv.TvSeasonResponse
import com.aldiprahasta.tmdb.domain.repository.TvRepository
import com.aldiprahasta.tmdb.utils.UiState
import kotlinx.coroutines.flow.Flow

class TvRepositoryImpl(
        private val tvRemoteDataSource: TvRemoteDataSource,
        private val remoteService: RemoteService
) : TvRepository {
    override fun getPopularTv(): Flow<PagingData<TvResponseModel>> {
        return Pager(
                config = PagingConfig(pageSize = 10),
                pagingSourceFactory = {
                    PopularTvPagingSource(remoteService)
                }
        ).flow
    }

    override fun getTvDetail(tvId: Int): Flow<UiState<TvDetailResponse>> {
        return tvRemoteDataSource.getTvDetail(tvId)
    }

    override fun getTvCredits(tvId: Int): Flow<UiState<CreditResponse>> {
        return tvRemoteDataSource.getTvCredits(tvId)
    }

    override fun getTvSeasonDetail(tvId: Int, tvSeasonNumber: Int): Flow<UiState<TvSeasonResponse>> {
        return tvRemoteDataSource.getTvSeasonDetail(tvId, tvSeasonNumber)
    }

    override fun getTvGenres(): Flow<UiState<GenreResponse>> {
        return tvRemoteDataSource.getTvGenres()
    }
}