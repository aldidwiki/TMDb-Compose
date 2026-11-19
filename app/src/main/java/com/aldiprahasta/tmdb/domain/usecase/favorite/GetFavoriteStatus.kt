package com.aldiprahasta.tmdb.domain.usecase.favorite

import com.aldiprahasta.tmdb.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow

class GetFavoriteStatus(private val favoriteRepository: FavoriteRepository) {
    operator fun invoke(id: Int, mediaType: String): Flow<Boolean> =
            favoriteRepository.isContentFavorite(id, mediaType)
}
