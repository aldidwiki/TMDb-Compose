package com.aldiprahasta.tmdb.domain.usecase

import com.aldiprahasta.tmdb.domain.model.GenreDomainModel
import com.aldiprahasta.tmdb.domain.repository.TvRepository
import com.aldiprahasta.tmdb.utils.UiState
import com.aldiprahasta.tmdb.utils.mapGenreResponseToDomainModelList
import com.aldiprahasta.tmdb.utils.mapSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetTvGenreList(private val tvRepository: TvRepository) {
    operator fun invoke(): Flow<UiState<List<GenreDomainModel>>> = tvRepository.getTvGenres()
            .map { state ->
                state.mapSuccess { it.mapGenreResponseToDomainModelList() }
            }
}
