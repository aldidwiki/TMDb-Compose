package com.aldiprahasta.tmdb.domain.usecase

import com.aldiprahasta.tmdb.domain.model.CastDomainModel
import com.aldiprahasta.tmdb.domain.repository.TvRepository
import com.aldiprahasta.tmdb.utils.MediaType
import com.aldiprahasta.tmdb.utils.UiState
import com.aldiprahasta.tmdb.utils.mapCreditResponseToCastDomainModelList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetTvCredits(private val tvRepository: TvRepository) {
    operator fun invoke(tvId: Int): Flow<UiState<List<CastDomainModel>>> = tvRepository.getTvCredits(tvId)
            .map { state ->
                when (state) {
                    is UiState.Loading -> UiState.Loading

                    is UiState.Error -> UiState.Error(
                            state.throwable,
                            state.errorMessage
                    )

                    is UiState.Success -> UiState.Success(
                            state.data.mapCreditResponseToCastDomainModelList(MediaType.TV_TYPE)
                    )
                }
            }
}