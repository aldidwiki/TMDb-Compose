package com.aldiprahasta.tmdb.ui.person

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

    val personDetail: StateFlow<UiState<PersonDomainModel>> = personId.flatMapLatest { id ->
        getPersonDetail(id)
    }.delayAfterLoading(300L).stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            UiState.Loading
    )
}