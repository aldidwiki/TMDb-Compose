package com.aldiprahasta.tmdb.ui.person.credits

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aldiprahasta.tmdb.domain.usecase.GetPersonCredits
import com.aldiprahasta.tmdb.navigation.PersonCredits
import com.aldiprahasta.tmdb.utils.UiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

@OptIn(ExperimentalCoroutinesApi::class)
class PersonCreditsViewModel(
        getPersonCredits: GetPersonCredits,
        savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val personId = checkNotNull(savedStateHandle.get<Int>(PersonCredits.PERSON_ID_ARG))
    private val loadRevision = MutableStateFlow(0)
    val credits = loadRevision.flatMapLatest { getPersonCredits(personId) }
            .stateIn(viewModelScope, SharingStarted.Lazily, UiState.Loading)

    fun retry() {
        if (credits.value is UiState.Error) loadRevision.update { it + 1 }
    }
}
