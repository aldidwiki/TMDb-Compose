package com.aldiprahasta.tmdb.ui.person

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aldiprahasta.tmdb.domain.model.PersonDomainModel
import com.aldiprahasta.tmdb.domain.usecase.wrapper.PersonDetailWrapper
import com.aldiprahasta.tmdb.utils.UiState
import com.aldiprahasta.tmdb.utils.delayAfterLoading
import com.aldiprahasta.tmdb.utils.mapDomainModelToEntity
import com.aldiprahasta.tmdb.utils.toStateFlow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class PersonViewModel(private val personDetailWrapper: PersonDetailWrapper) : ViewModel() {
    private val personId = MutableStateFlow(0)
    fun setPersonId(personId: Int) {
        this.personId.value = personId
    }

    var isFavorite by mutableStateOf(false)
        private set

    fun updateFavoriteState(isFavorite: Boolean) {
        this.isFavorite = isFavorite
    }

    val getFavoriteStatus: StateFlow<Boolean> = personId.flatMapLatest {
        personDetailWrapper.getFavoriteStatus(it)
    }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            false
    )

    val personDetail: StateFlow<UiState<PersonDomainModel>> = personId.flatMapLatest { id ->
        personDetailWrapper.getPersonDetail(id)
    }.delayAfterLoading(300L).toStateFlow(viewModelScope)

    fun addToFavorite(personDomainModel: PersonDomainModel) {
        viewModelScope.launch {
            personDetailWrapper.insertFavorite(
                    personDomainModel.mapDomainModelToEntity()
            )
        }
    }

    fun deleteFavorite(personId: Int) {
        viewModelScope.launch {
            personDetailWrapper.deleteFavorite(personId)
        }
    }
}