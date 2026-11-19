package com.aldiprahasta.tmdb.ui.details

import com.aldiprahasta.tmdb.utils.MediaType

sealed interface ContentDetailEvent {
    data class Initialize(val contentId: Int, val mediaType: MediaType) : ContentDetailEvent
    data class SelectSeason(val seasonNumber: Int) : ContentDetailEvent
    data class OnFavoriteClicked(val checked: Boolean) : ContentDetailEvent
    data object RetryDetail : ContentDetailEvent
    data object RetrySimilar : ContentDetailEvent
    data object RetrySeason : ContentDetailEvent
}
