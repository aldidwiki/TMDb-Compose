package com.aldiprahasta.tmdb.ui.person

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aldiprahasta.tmdb.domain.usecase.wrapper.PersonDetailWrapper
import com.aldiprahasta.tmdb.utils.MediaType
import com.aldiprahasta.tmdb.utils.UiState
import com.aldiprahasta.tmdb.utils.mapDomainModelToEntity
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

class PersonViewModel(private val personDetailWrapper: PersonDetailWrapper) : ViewModel() {
    private val _uiState = MutableStateFlow(PersonDetailState())
    val uiState = _uiState.asStateFlow()
    private val favoriteErrorChannel = Channel<Unit>(Channel.CONFLATED)
    val favoriteErrors = favoriteErrorChannel.receiveAsFlow()

    private var personId: Int? = null
    private var personJob: Job? = null
    private var favoriteStatusJob: Job? = null
    private var confirmedFavorite = false

    fun onEvent(event: PersonDetailEvent) {
        when (event) {
            is PersonDetailEvent.Initialize -> initialize(event.personId)
            PersonDetailEvent.Retry -> personId?.let { id ->
                if (_uiState.value.person is UiState.Error) loadPerson(id)
            }
            is PersonDetailEvent.OnFavoriteClicked -> setFavorite(event.checked)
        }
    }

    private fun initialize(id: Int) {
        if (personId == id) return

        personId = id
        favoriteStatusJob?.cancel()
        confirmedFavorite = false
        _uiState.value = PersonDetailState()
        loadPerson(id)

        favoriteStatusJob = viewModelScope.launch {
            personDetailWrapper.getFavoriteStatus(id, MediaType.PERSON_TYPE.name)
                    .catch { error ->
                        Timber.e(error)
                        favoriteErrorChannel.trySend(Unit)
                    }
                    .collect { favorite ->
                        confirmedFavorite = favorite
                        _uiState.update { state ->
                            state.copy(
                                    isFavoriteAvailable = true,
                                    isFavorite = if (state.isSavingFavorite) state.isFavorite else favorite
                            )
                        }
                    }
        }
    }

    private fun loadPerson(id: Int) {
        personJob?.cancel()
        _uiState.update { it.copy(person = UiState.Loading) }
        personJob = viewModelScope.launch {
            personDetailWrapper.getPersonDetail(id)
                    .catch { error ->
                        Timber.e(error)
                        emit(UiState.Error(error))
                    }
                    .collect { person -> _uiState.update { it.copy(person = person) } }
        }
    }

    private fun setFavorite(checked: Boolean) {
        val state = _uiState.value
        val person = (state.person as? UiState.Success)?.data ?: return
        if (!state.isFavoriteAvailable || state.isSavingFavorite || checked == state.isFavorite) return

        _uiState.update { it.copy(isFavorite = checked, isSavingFavorite = true) }
        viewModelScope.launch {
            try {
                if (checked) {
                    personDetailWrapper.insertFavorite(person.mapDomainModelToEntity())
                } else {
                    personDetailWrapper.deleteFavorite(person.id, MediaType.PERSON_TYPE.name)
                }
                if (personId == person.id) {
                    confirmedFavorite = checked
                    _uiState.update { it.copy(isSavingFavorite = false) }
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                Timber.e(error)
                if (personId == person.id) {
                    _uiState.update { it.copy(isFavorite = confirmedFavorite, isSavingFavorite = false) }
                    favoriteErrorChannel.trySend(Unit)
                }
            }
        }
    }
}
