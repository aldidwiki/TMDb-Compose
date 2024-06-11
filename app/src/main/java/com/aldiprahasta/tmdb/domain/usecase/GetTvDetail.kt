package com.aldiprahasta.tmdb.domain.usecase

import com.aldiprahasta.tmdb.domain.model.ContentDetailDomainModel
import com.aldiprahasta.tmdb.domain.repository.TvRepository
import com.aldiprahasta.tmdb.utils.UiState
import com.aldiprahasta.tmdb.utils.mapTvDetailResponseToContentDetailDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetTvDetail(private val tvRepository: TvRepository) {
    operator fun invoke(tvId: Int): Flow<UiState<ContentDetailDomainModel>> = tvRepository.getTvDetail(tvId)
            .map { state ->
                when (state) {
                    is UiState.Loading -> UiState.Loading

                    is UiState.Error -> UiState.Error(
                            state.throwable,
                            state.errorMessage
                    )

                    is UiState.Success -> UiState.Success(
                            state.data.mapTvDetailResponseToContentDetailDomainModel()
                    )
                }
            }
}