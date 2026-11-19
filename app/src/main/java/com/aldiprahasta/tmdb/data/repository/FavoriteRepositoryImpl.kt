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

    override fun isContentFavorite(id: Int, mediaType: String): Flow<Boolean> {
        return favoriteDao.isContentFavorite(id, mediaType)
    }

    override suspend fun insertFavorite(favoriteEntity: FavoriteEntity) {
        favoriteDao.insertFavorite(favoriteEntity)
    }

    override suspend fun deleteFavorite(favoriteId: Int, mediaType: String): Boolean {
        return favoriteDao.deleteFavorite(favoriteId, mediaType) != 0
    }
}
