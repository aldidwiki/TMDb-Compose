package com.aldiprahasta.tmdb.ui.person

import androidx.compose.runtime.Immutable
import com.aldiprahasta.tmdb.domain.model.PersonDomainModel
import com.aldiprahasta.tmdb.utils.UiState

@Immutable
data class PersonDetailState(
        val person: UiState<PersonDomainModel> = UiState.Loading,
        val isFavorite: Boolean = false,
        val isFavoriteAvailable: Boolean = false,
        val isSavingFavorite: Boolean = false
)
