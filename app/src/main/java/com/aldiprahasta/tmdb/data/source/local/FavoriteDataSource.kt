package com.aldiprahasta.tmdb.data.source.local

import com.aldiprahasta.tmdb.data.source.local.database.FavoriteDao
import com.aldiprahasta.tmdb.data.source.local.entity.FavoriteEntity
import com.aldiprahasta.tmdb.utils.UiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavoriteDataSource(private val favoriteDao: FavoriteDao) {
    fun getAllFavorites(): Flow<UiState<List<FavoriteEntity>>> = flow {
        emit(UiState.Loading)
        val favorites = favoriteDao.getAllFavorites()
        emit(UiState.Success(favorites))
    }
}