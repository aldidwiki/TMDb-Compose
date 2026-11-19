package com.aldiprahasta.tmdb.domain.usecase.favorite

import com.aldiprahasta.tmdb.domain.model.FavoriteDomainModel
import com.aldiprahasta.tmdb.domain.repository.FavoriteRepository
import com.aldiprahasta.tmdb.utils.UiState
import com.aldiprahasta.tmdb.utils.mapEntityToDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import timber.log.Timber

class GetAllFavoriteList(private val repository: FavoriteRepository) {
    operator fun invoke(): Flow<UiState<List<FavoriteDomainModel>>> = flow {
        emit(UiState.Loading)
        emitAll(repository.getAllFavorites().map { entities ->
            UiState.Success(entities.map { it.mapEntityToDomainModel() })
        })
    }.catch { error ->
        Timber.e(error)
        emit(UiState.Error(error))
    }
}
