package com.aldiprahasta.tmdb.domain.usecase.favorite

import com.aldiprahasta.tmdb.domain.repository.FavoriteRepository

class DeleteFavorite(private val favoriteRepository: FavoriteRepository) {
    suspend operator fun invoke(id: Int, mediaType: String): Boolean =
            favoriteRepository.deleteFavorite(id, mediaType)
}
