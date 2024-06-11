package com.aldiprahasta.tmdb.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aldiprahasta.tmdb.domain.model.ContentDetailDomainModel
import com.aldiprahasta.tmdb.domain.usecase.wrapper.DetailWrapper
import com.aldiprahasta.tmdb.utils.MediaType
import com.aldiprahasta.tmdb.utils.UiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

@OptIn(ExperimentalCoroutinesApi::class)
class ContentDetailViewModel(detailWrapper: DetailWrapper) : ViewModel() {
    private val contentParam: MutableStateFlow<Pair<Int, String>> = MutableStateFlow(Pair(0, ""))
    fun setId(contentParam: Pair<Int, String>) {
        this.contentParam.value = contentParam
    }

    val contentDetail: StateFlow<UiState<ContentDetailDomainModel>> = contentParam.flatMapLatest { param ->
        val contentId = param.first
        val contentType = param.second

        if (contentType == MediaType.MOVIE_TYPE.name) {
            detailWrapper.getMovieDetail(contentId)
        } else {
            detailWrapper.getTvDetail(contentId)
        }
    }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            UiState.Loading
    )
}