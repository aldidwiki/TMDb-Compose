package com.aldiprahasta.tmdb.data.repository

import com.aldiprahasta.tmdb.data.source.remote.TvRemoteDataSource
import com.aldiprahasta.tmdb.data.source.remote.response.CreditResponse
import com.aldiprahasta.tmdb.data.source.remote.response.GenreResponse
import com.aldiprahasta.tmdb.data.source.remote.response.ImageResponse
import com.aldiprahasta.tmdb.data.source.remote.response.tv.TvDetailResponse
import com.aldiprahasta.tmdb.data.source.remote.response.tv.TvSeasonResponse
import com.aldiprahasta.tmdb.domain.repository.TvRepository
import com.aldiprahasta.tmdb.utils.UiState
import kotlinx.coroutines.flow.Flow

class TvRepositoryImpl(private val tvRemoteDataSource: TvRemoteDataSource) : TvRepository {
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

    override fun getTvImages(tvId: Int): Flow<UiState<ImageResponse>> {
        return tvRemoteDataSource.getTvImages(tvId)
    }
}
