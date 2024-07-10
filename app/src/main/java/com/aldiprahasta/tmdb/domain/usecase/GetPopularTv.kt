package com.aldiprahasta.tmdb.domain.usecase

import androidx.paging.PagingData
import androidx.paging.map
import com.aldiprahasta.tmdb.domain.model.TvDomainModel
import com.aldiprahasta.tmdb.domain.repository.TvRepository
import com.aldiprahasta.tmdb.utils.mapTvResponseToTvDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetPopularTv(private val tvRepository: TvRepository) {
    operator fun invoke(): Flow<PagingData<TvDomainModel>> = tvRepository.getPopularTv()
            .map { pagingData ->
                pagingData.map { tvResponseModel ->
                    tvResponseModel.mapTvResponseToTvDomainModel()
                }
            }
}