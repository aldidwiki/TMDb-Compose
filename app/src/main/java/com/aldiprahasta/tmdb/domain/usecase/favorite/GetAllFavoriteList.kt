package com.aldiprahasta.tmdb.domain.usecase.favorite

import com.aldiprahasta.tmdb.domain.model.FavoriteDomainModel
import com.aldiprahasta.tmdb.domain.repository.FavoriteRepository
import com.aldiprahasta.tmdb.utils.UiState
import com.aldiprahasta.tmdb.utils.mapEntityToDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetAllFavoriteList(private val repository: FavoriteRepository) {
    operator fun invoke(): Flow<UiState<List<FavoriteDomainModel>>> = repository.getAllFavorites().map { state ->
        when (state) {
            is UiState.Loading -> UiState.Loading
            is UiState.Error -> UiState.Error(
                    state.throwable,
                    state.errorMessage
            )

            is UiState.Success -> UiState.Success(
                    state.data.map { it.mapEntityToDomainModel() }
            )
        }
    }
}