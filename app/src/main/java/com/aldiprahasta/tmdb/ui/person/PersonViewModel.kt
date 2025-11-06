package com.aldiprahasta.tmdb.ui.person

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aldiprahasta.tmdb.domain.model.PersonDomainModel
import com.aldiprahasta.tmdb.domain.usecase.wrapper.PersonDetailWrapper
import com.aldiprahasta.tmdb.utils.delayAfterLoading
import com.aldiprahasta.tmdb.utils.doIfError
import com.aldiprahasta.tmdb.utils.doIfLoading
import com.aldiprahasta.tmdb.utils.doIfSuccess
import com.aldiprahasta.tmdb.utils.mapDomainModelToEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class PersonViewModel(private val personDetailWrapper: PersonDetailWrapper) : ViewModel() {
    private val _uiState = MutableStateFlow(PersonDetailState())
    val uiState: StateFlow<PersonDetailState> = _uiState.asStateFlow()

    private var _isDataFetched = false

    fun onEvent(event: PersonDetailEvent) {
        when (event) {
            is PersonDetailEvent.Initialize -> init(event.personId)

            is PersonDetailEvent.OnFavoriteClicked -> toggleFavorite(
                    event.isFavorite,
                    event.personDomainModel
            )
        }
    }

    private fun init(personId: Int) {
        if (_isDataFetched) return

        _isDataFetched = true

        personDetailWrapper.getPersonDetail(personId)
                .delayAfterLoading(300L)
                .onEach { state ->
                    state.doIfLoading {
                        _uiState.update { it.copy(isLoading = true) }
                    }

                    state.doIfError { throwable, errorMessage ->
                        _uiState.update {
                            it.copy(
                                    isLoading = false,
                                    personError = throwable,
                                    personErrorMsg = errorMessage
                            )
                        }
                    }

                    state.doIfSuccess { personDetail ->
                        _uiState.update {
                            it.copy(
                                    isLoading = false,
                                    personDomainModel = personDetail
                            )
                        }
                    }
                }.launchIn(viewModelScope)

        personDetailWrapper.getFavoriteStatus(personId)
                .onEach { isFavorite ->
                    _uiState.update { it.copy(isFavorite = isFavorite) }
                }.launchIn(viewModelScope)
    }

    private fun toggleFavorite(isFavorite: Boolean, personDomainModel: PersonDomainModel?) {
        _uiState.update { it.copy(isFavorite = isFavorite) }

        viewModelScope.launch {
            personDomainModel?.let { personModel ->
                if (isFavorite) {
                    personDetailWrapper.deleteFavorite(personModel.id)
                } else {
                    personDetailWrapper.insertFavorite(personModel.mapDomainModelToEntity())
                }
            }
        }
    }
}