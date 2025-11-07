package com.aldiprahasta.tmdb.ui.details

import com.aldiprahasta.tmdb.domain.model.ContentDetailDomainModel

sealed interface ContentDetailEvent {
    data class Initialize(val contentId: Int, val contentType: String) : ContentDetailEvent
    data class OnFavoriteClicked(
            val isFavorite: Boolean,
            val contentType: String,
            val contentDetailDomainModel: ContentDetailDomainModel?
    ) : ContentDetailEvent
}