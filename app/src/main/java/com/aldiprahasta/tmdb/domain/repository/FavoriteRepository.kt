package com.aldiprahasta.tmdb.domain.repository

import com.aldiprahasta.tmdb.data.source.local.entity.FavoriteEntity
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {
    fun getAllFavorites(): Flow<List<FavoriteEntity>>

    fun isContentFavorite(id: Int): Flow<Boolean>

    suspend fun insertFavorite(favoriteEntity: FavoriteEntity)

    suspend fun deleteFavorite(favoriteId: Int)
}