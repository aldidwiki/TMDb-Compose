package com.aldiprahasta.tmdb.ui.person

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aldiprahasta.tmdb.domain.model.PersonDomainModel
import com.aldiprahasta.tmdb.domain.usecase.GetPersonDetail
import com.aldiprahasta.tmdb.utils.UiState
import com.aldiprahasta.tmdb.utils.delayAfterLoading
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

@OptIn(ExperimentalCoroutinesApi::class)
class PersonViewModel(getPersonDetail: GetPersonDetail) : ViewModel() {
    private val personId = MutableStateFlow(0)
    fun setPersonId(personId: Int) {
        this.personId.value = personId
    }

    var isFavorite by mutableStateOf(false)
        private set

    fun updateFavoriteState(isFavorite: Boolean) {
        this.isFavorite = isFavorite
    }

    val personDetail: StateFlow<UiState<PersonDomainModel>> = personId.flatMapLatest { id ->
        getPersonDetail(id)
    }.delayAfterLoading(300L).stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            UiState.Loading
    )
}