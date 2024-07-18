package com.aldiprahasta.tmdb.domain.usecase.favorite

import com.aldiprahasta.tmdb.data.source.local.entity.FavoriteEntity
import com.aldiprahasta.tmdb.domain.repository.FavoriteRepository

class InsertFavorite(private val favoriteRepository: FavoriteRepository) {
    suspend operator fun invoke(favoriteEntity: FavoriteEntity) {
        favoriteRepository.insertFavorite(favoriteEntity)
    }
}