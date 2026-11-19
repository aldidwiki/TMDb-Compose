package com.aldiprahasta.tmdb.domain.usecase

import com.aldiprahasta.tmdb.domain.model.CastDomainModel
import com.aldiprahasta.tmdb.domain.repository.TvRepository
import com.aldiprahasta.tmdb.utils.MediaType
import com.aldiprahasta.tmdb.utils.UiState
import com.aldiprahasta.tmdb.utils.mapCreditResponseToCastDomainModelList
import com.aldiprahasta.tmdb.utils.mapSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetTvCredits(private val tvRepository: TvRepository) {
    operator fun invoke(tvId: Int): Flow<UiState<List<CastDomainModel>>> = tvRepository.getTvCredits(tvId)
            .map { state ->
                state.mapSuccess { it.mapCreditResponseToCastDomainModelList(MediaType.TV_TYPE) }
            }
}
