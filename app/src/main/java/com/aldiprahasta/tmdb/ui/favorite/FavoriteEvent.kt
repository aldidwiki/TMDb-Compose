package com.aldiprahasta.tmdb.ui.favorite

import com.aldiprahasta.tmdb.domain.model.FavoriteDomainModel

sealed interface FavoriteEvent {
    data class ApplySort(val sortOrder: FavoriteSortOrder) : FavoriteEvent
    data class Remove(val favorite: FavoriteDomainModel) : FavoriteEvent
    data class UndoRemove(val favorite: FavoriteDomainModel) : FavoriteEvent
}

sealed interface FavoriteEffect {
    data class Removed(val favorite: FavoriteDomainModel) : FavoriteEffect
    data object WriteFailed : FavoriteEffect
}
