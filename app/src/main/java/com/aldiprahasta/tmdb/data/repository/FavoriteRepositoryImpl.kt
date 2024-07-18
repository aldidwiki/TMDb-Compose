package com.aldiprahasta.tmdb.data.repository

import com.aldiprahasta.tmdb.data.source.local.database.FavoriteDao
import com.aldiprahasta.tmdb.data.source.local.entity.FavoriteEntity
import com.aldiprahasta.tmdb.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow

class FavoriteRepositoryImpl(
        private val favoriteDao: FavoriteDao
) : FavoriteRepository {
    override fun getAllFavorites(): Flow<List<FavoriteEntity>> {
        return favoriteDao.getAllFavorites()
    }

    override fun isContentFavorite(id: Int): Flow<Boolean> {
        return favoriteDao.isContentFavorite(id)
    }

    override suspend fun insertFavorite(favoriteEntity: FavoriteEntity) {
        favoriteDao.insertFavorite(favoriteEntity)
    }

    override suspend fun deleteFavorite(favoriteId: Int) {
        favoriteDao.deleteFavorite(favoriteId)
    }
}