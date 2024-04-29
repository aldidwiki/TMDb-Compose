package com.aldiprahasta.tmdb.ui.credit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aldiprahasta.tmdb.domain.model.CastDomainModel
import com.aldiprahasta.tmdb.domain.usecase.GetMovieCredits
import com.aldiprahasta.tmdb.utils.UiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

@OptIn(ExperimentalCoroutinesApi::class)
class CreditViewModel(private val getMovieCredits: GetMovieCredits) : ViewModel() {
    private val contentId = MutableStateFlow(0)
    fun setContentId(contentId: Int) {
        this.contentId.value = contentId
    }

    val movieCredits: StateFlow<UiState<List<CastDomainModel>>> = contentId.flatMapLatest { id ->
        getMovieCredits(id)
    }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            UiState.Loading
    )
}