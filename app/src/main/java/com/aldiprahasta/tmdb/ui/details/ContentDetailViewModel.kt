package com.aldiprahasta.tmdb.ui.details

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aldiprahasta.tmdb.domain.model.ContentDetailDomainModel
import com.aldiprahasta.tmdb.domain.usecase.wrapper.DetailWrapper
import com.aldiprahasta.tmdb.utils.MediaType
import com.aldiprahasta.tmdb.utils.UiState
import com.aldiprahasta.tmdb.utils.delayAfterLoading
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class ContentDetailViewModel(private val detailWrapper: DetailWrapper) : ViewModel() {
    private val contentParam: MutableStateFlow<Pair<Int, String>> = MutableStateFlow(Pair(0, ""))
    fun setId(contentParam: Pair<Int, String>) {
        this.contentParam.value = contentParam
    }

    var isFavorite by mutableStateOf(false)
        private set

    fun updateFavoriteState(isFavorite: Boolean) {
        this.isFavorite = isFavorite
    }

    val getFavoriteStatus: StateFlow<Boolean> = contentParam.flatMapLatest { (contentId, _) ->
        detailWrapper.getFavoriteStatus(contentId)
    }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            false
    )

    val contentDetail: StateFlow<UiState<ContentDetailDomainModel>> = contentParam.flatMapLatest { param ->
        val contentId = param.first
        val contentType = param.second

        if (contentType == MediaType.MOVIE_TYPE.name) {
            detailWrapper.getMovieDetail(contentId)
        } else {
            detailWrapper.getTvDetail(contentId)
        }
    }.delayAfterLoading(300L).stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            UiState.Loading
    )

    fun addToFavorite(contentDetailDomainModel: ContentDetailDomainModel, mediaType: String) {
        viewModelScope.launch {
            detailWrapper.insertFavorite(contentDetailDomainModel, mediaType)
        }
    }

    fun deleteFavorite(id: Int) {
        viewModelScope.launch {
            detailWrapper.deleteFavorite(id)
        }
    }
}