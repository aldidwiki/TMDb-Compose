package com.aldiprahasta.tmdb.domain.usecase.favorite

import com.aldiprahasta.tmdb.domain.model.FavoriteDomainModel
import com.aldiprahasta.tmdb.domain.repository.FavoriteRepository
import com.aldiprahasta.tmdb.utils.UiState
import com.aldiprahasta.tmdb.utils.mapEntityToDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform

class GetAllFavoriteList(private val repository: FavoriteRepository) {
    operator fun invoke(): Flow<UiState<List<FavoriteDomainModel>>> = repository.getAllFavorites().transform { favoriteEntities ->
        emit(UiState.Loading)

        val favorites = favoriteEntities.map { it.mapEntityToDomainModel() }

        if (favorites.isNotEmpty()) emit(UiState.Success(favorites))
        else emit(UiState.Error(NullPointerException(), "No Favorites Found"))
    }
}