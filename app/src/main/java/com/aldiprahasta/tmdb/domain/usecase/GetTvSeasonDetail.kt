package com.aldiprahasta.tmdb.domain.usecase

import com.aldiprahasta.tmdb.domain.model.TvSeasonDetailDomainModel
import com.aldiprahasta.tmdb.domain.repository.TvRepository
import com.aldiprahasta.tmdb.utils.UiState
import com.aldiprahasta.tmdb.utils.mapTvSeasonResponseToDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetTvSeasonDetail(private val tvRepository: TvRepository) {
    operator fun invoke(tvId: Int, tvSeasonNumber: Int): Flow<UiState<TvSeasonDetailDomainModel>> =
            tvRepository.getTvSeasonDetail(tvId, tvSeasonNumber).map { state ->
                when (state) {
                    is UiState.Loading -> UiState.Loading

                    is UiState.Error -> UiState.Error(
                            state.throwable, state.errorMessage
                    )

                    is UiState.Success -> UiState.Success(
                            state.data.mapTvSeasonResponseToDomainModel()
                    )
                }
            }
}