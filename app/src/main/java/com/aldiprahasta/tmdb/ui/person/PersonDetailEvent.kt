package com.aldiprahasta.tmdb.ui.person

sealed interface PersonDetailEvent {
    data class Initialize(val personId: Int) : PersonDetailEvent
    data object Retry : PersonDetailEvent
    data class OnFavoriteClicked(val checked: Boolean) : PersonDetailEvent
}
