package com.aldiprahasta.tmdb.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aldiprahasta.tmdb.domain.model.ContentDetailDomainModel
import com.aldiprahasta.tmdb.domain.usecase.wrapper.DetailWrapper
import com.aldiprahasta.tmdb.utils.UiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

@OptIn(ExperimentalCoroutinesApi::class)
class ContentDetailViewModel(detailWrapper: DetailWrapper) : ViewModel() {
    private val id: MutableStateFlow<Int> = MutableStateFlow(0)
    fun setId(contentId: Int) {
        id.value = contentId
    }

    val movieDetail: StateFlow<UiState<ContentDetailDomainModel>> = id.flatMapLatest { contentId ->
        detailWrapper.getMovieDetail(contentId)
    }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            UiState.Loading
    )
}