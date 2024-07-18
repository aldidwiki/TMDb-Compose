package com.aldiprahasta.tmdb.domain.repository

import com.aldiprahasta.tmdb.data.source.local.entity.FavoriteEntity
import com.aldiprahasta.tmdb.utils.UiState
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {
    fun getAllFavorites(): Flow<UiState<List<FavoriteEntity>>>

    suspend fun insertFavorite(favoriteEntity: FavoriteEntity)

    suspend fun deleteFavorite(favoriteId: Int)
}