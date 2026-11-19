package com.aldiprahasta.tmdb.domain.usecase

import com.aldiprahasta.tmdb.domain.model.ImageDomainModel
import com.aldiprahasta.tmdb.domain.repository.TvRepository
import com.aldiprahasta.tmdb.utils.UiState
import com.aldiprahasta.tmdb.utils.mapBackdrops
import com.aldiprahasta.tmdb.utils.mapSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetTvImages(private val tvRepository: TvRepository) {
    operator fun invoke(tvId: Int): Flow<UiState<List<ImageDomainModel>>> =
            tvRepository.getTvImages(tvId).map { state ->
                state.mapSuccess { it.mapBackdrops() }
            }
}
