package com.aldiprahasta.tmdb.data.repository

import com.aldiprahasta.tmdb.data.source.local.FavoriteDataSource
import com.aldiprahasta.tmdb.data.source.local.database.FavoriteDao
import com.aldiprahasta.tmdb.data.source.local.entity.FavoriteEntity
import com.aldiprahasta.tmdb.domain.repository.FavoriteRepository
import com.aldiprahasta.tmdb.utils.UiState
import kotlinx.coroutines.flow.Flow

class FavoriteRepositoryImpl(
        private val favoriteDataSource: FavoriteDataSource,
        private val favoriteDao: FavoriteDao
) : FavoriteRepository {
    override fun getAllFavorites(): Flow<UiState<List<FavoriteEntity>>> {
        return favoriteDataSource.getAllFavorites()
    }

    override fun isContentFavorite(id: Int): Flow<Boolean> {
        return favoriteDataSource.isContentFavorite(id)
    }

    override suspend fun insertFavorite(favoriteEntity: FavoriteEntity) {
        favoriteDao.insertFavorite(favoriteEntity)
    }

    override suspend fun deleteFavorite(favoriteId: Int) {
        favoriteDao.deleteFavorite(favoriteId)
    }
}