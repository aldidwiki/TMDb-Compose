package com.aldiprahasta.tmdb.domain.usecase.favorite

import com.aldiprahasta.tmdb.domain.model.ContentDetailDomainModel
import com.aldiprahasta.tmdb.domain.repository.FavoriteRepository
import com.aldiprahasta.tmdb.utils.MediaType
import com.aldiprahasta.tmdb.utils.mapDomainModelToEntity

class InsertFavorite(private val favoriteRepository: FavoriteRepository) {
    suspend operator fun invoke(contentDetailDomainModel: ContentDetailDomainModel, mediaType: String) {
        favoriteRepository.insertFavorite(contentDetailDomainModel.mapDomainModelToEntity(mediaType))
    }
}