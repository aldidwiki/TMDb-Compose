package com.aldiprahasta.tmdb.ui.credit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aldiprahasta.tmdb.domain.model.CastDomainModel
import com.aldiprahasta.tmdb.domain.usecase.wrapper.CreditWrapper
import com.aldiprahasta.tmdb.utils.MediaType
import com.aldiprahasta.tmdb.utils.UiState
import com.aldiprahasta.tmdb.utils.delayAfterLoading
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

@OptIn(ExperimentalCoroutinesApi::class)
class CreditViewModel(private val creditWrapper: CreditWrapper) : ViewModel() {
    private val contentId = MutableStateFlow(Pair(0, MediaType.MOVIE_TYPE.name))
    fun setContentId(contentId: Pair<Int, String>) {
        this.contentId.value = contentId
    }

    val credits: StateFlow<UiState<List<CastDomainModel>>> = contentId.flatMapLatest { contentPair ->
        when (contentPair.second) {
            MediaType.MOVIE_TYPE.name -> creditWrapper.getMovieCredits(contentPair.first)
            MediaType.TV_TYPE.name -> creditWrapper.getTvCredits(contentPair.first)
            else -> creditWrapper.getPersonCredits(contentPair.first)// person type
        }
    }.delayAfterLoading(300L).stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            UiState.Loading
    )
}