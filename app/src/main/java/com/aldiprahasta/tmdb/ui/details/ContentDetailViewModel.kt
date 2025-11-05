package com.aldiprahasta.tmdb.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aldiprahasta.tmdb.domain.model.ContentDetailDomainModel
import com.aldiprahasta.tmdb.domain.usecase.wrapper.DetailWrapper
import com.aldiprahasta.tmdb.utils.MediaType
import com.aldiprahasta.tmdb.utils.UiState
import com.aldiprahasta.tmdb.utils.delayAfterLoading
import com.aldiprahasta.tmdb.utils.doIfSuccess
import com.aldiprahasta.tmdb.utils.mapDomainModelToEntity
import com.aldiprahasta.tmdb.utils.toStateFlow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class ContentDetailViewModel(private val detailWrapper: DetailWrapper) : ViewModel() {
    private val contentParam: MutableStateFlow<Pair<Int, String>> = MutableStateFlow(Pair(0, ""))
    fun setId(contentParam: Pair<Int, String>) {
        this.contentParam.value = contentParam
    }

    val getFavoriteStatus: StateFlow<Boolean> = contentParam.flatMapLatest { (contentId, _) ->
        detailWrapper.getFavoriteStatus(contentId)
    }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            false
    )

    val contentDetail: StateFlow<UiState<ContentDetailDomainModel>> = contentParam.flatMapLatest { (contentId, contentType) ->
        if (contentType == MediaType.MOVIE_TYPE.name) {
            detailWrapper.getMovieDetail(contentId)
        } else {
            detailWrapper.getTvDetail(contentId)
        }
    }
            .onEach { state ->
                state.doIfSuccess { detailModel ->
                    _contentDetailDomainModel.value = detailModel
                }
            }
            .delayAfterLoading(300L)
            .toStateFlow(viewModelScope)

    private val _contentDetailDomainModel = MutableStateFlow<ContentDetailDomainModel?>(null)
    val contentDetailDomainModel: StateFlow<ContentDetailDomainModel?> = _contentDetailDomainModel.asStateFlow()

    fun toggleFavorite(mediaType: String) {
        val currentStatus = getFavoriteStatus.value
        val currentDetailData = _contentDetailDomainModel.value

        viewModelScope.launch {
            currentDetailData?.let { detailModel ->
                if (currentStatus) {
                    detailWrapper.deleteFavorite(detailModel.id)
                } else {
                    detailWrapper.insertFavorite(detailModel.mapDomainModelToEntity(mediaType))
                }
            }
        }
    }
}