package com.aldiprahasta.tmdb.ui.person

import com.aldiprahasta.tmdb.domain.model.PersonDomainModel

sealed interface PersonDetailEvent {
    data class Initialize(val personId: Int) : PersonDetailEvent
    data class OnFavoriteClicked(
            val isFavorite: Boolean,
            val personDomainModel: PersonDomainModel?
    ) : PersonDetailEvent
}