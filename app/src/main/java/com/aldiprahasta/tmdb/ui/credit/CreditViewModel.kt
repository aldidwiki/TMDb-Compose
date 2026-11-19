package com.aldiprahasta.tmdb.ui.credit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aldiprahasta.tmdb.domain.model.CastDomainModel
import com.aldiprahasta.tmdb.domain.usecase.wrapper.CreditWrapper
import com.aldiprahasta.tmdb.navigation.CreditDetail
import com.aldiprahasta.tmdb.utils.MediaType
import com.aldiprahasta.tmdb.utils.UiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import timber.log.Timber

@OptIn(ExperimentalCoroutinesApi::class)
class CreditViewModel(creditWrapper: CreditWrapper, savedStateHandle: SavedStateHandle) : ViewModel() {
    private val id = checkNotNull(savedStateHandle.get<Int>(CreditDetail.CONTENT_ID_ARG))
    private val isTv = savedStateHandle.get<String>(CreditDetail.CONTENT_TYPE_ARG) == MediaType.TV_TYPE.name
    private val request = MutableStateFlow(0)
    val credits: StateFlow<UiState<List<CastDomainModel>>> = request.flatMapLatest {
        val source = if (isTv) creditWrapper.getTvCredits(id) else creditWrapper.getMovieCredits(id)
        source.catch { error ->
            Timber.e(error)
            emit(UiState.Error(error))
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, UiState.Loading)

    fun retry() {
        if (credits.value is UiState.Error) request.update { it + 1 }
    }
}
