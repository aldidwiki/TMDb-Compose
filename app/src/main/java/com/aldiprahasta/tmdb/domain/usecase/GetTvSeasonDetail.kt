package com.aldiprahasta.tmdb.domain.usecase

import com.aldiprahasta.tmdb.domain.model.TvSeasonDetailDomainModel
import com.aldiprahasta.tmdb.domain.repository.TvRepository
import com.aldiprahasta.tmdb.utils.UiState
import com.aldiprahasta.tmdb.utils.mapTvSeasonResponseToDomainModel
import com.aldiprahasta.tmdb.utils.mapSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetTvSeasonDetail(private val tvRepository: TvRepository) {
    operator fun invoke(tvId: Int, tvSeasonNumber: Int): Flow<UiState<TvSeasonDetailDomainModel>> =
            tvRepository.getTvSeasonDetail(tvId, tvSeasonNumber).map { state ->
                state.mapSuccess { it.mapTvSeasonResponseToDomainModel() }
            }
}
