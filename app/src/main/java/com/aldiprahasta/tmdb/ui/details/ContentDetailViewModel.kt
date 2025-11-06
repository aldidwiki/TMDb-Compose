package com.aldiprahasta.tmdb.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aldiprahasta.tmdb.domain.model.ContentDetailDomainModel
import com.aldiprahasta.tmdb.domain.usecase.wrapper.DetailWrapper
import com.aldiprahasta.tmdb.utils.MediaType
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
class ContentDetailViewModel(private val detailWrapper: DetailWrapper) : ViewModel() {
    private val _uiState = MutableStateFlow(ContentDetailState())
    val uiState: StateFlow<ContentDetailState> = _uiState.asStateFlow()

    private var _isDataFetched = false

    fun onEvent(event: ContentDetailEvent) {
        when (event) {
            is ContentDetailEvent.Initialize -> init(
                    event.contentId,
                    event.contentType
            )

            is ContentDetailEvent.OnFavoriteClicked -> toggleFavorite(
                    event.isFavorite,
                    event.contentType,
                    event.contentDetailDomainModel
            )
        }
    }

    private fun init(contentId: Int, contentType: String) {
        if (_isDataFetched) return

        _isDataFetched = true

        if (contentType == MediaType.MOVIE_TYPE.name) {
            detailWrapper.getMovieDetail(contentId)
        } else {
            detailWrapper.getTvDetail(contentId)
        }
                .delayAfterLoading(300L)
                .onEach { state ->
                    state.doIfLoading {
                        _uiState.update { it.copy(isLoading = true) }
                    }

                    state.doIfError { throwable, errorMessage ->
                        _uiState.update {
                            it.copy(
                                    isLoading = false,
                                    contentError = throwable,
                                    contentErrorMsg = errorMessage
                            )
                        }
                    }

                    state.doIfSuccess { contentDetail ->
                        _uiState.update {
                            it.copy(
                                    isLoading = false,
                                    contentDetailDomainModel = contentDetail
                            )
                        }
                    }
                }.launchIn(viewModelScope)

        detailWrapper.getFavoriteStatus(contentId)
                .onEach { isFavorite ->
                    _uiState.update { it.copy(isFavorite = isFavorite) }
                }.launchIn(viewModelScope)
    }

    private fun toggleFavorite(isFavorite: Boolean, mediaType: String, contentDetailDomainModel: ContentDetailDomainModel?) {
        _uiState.update { it.copy(isFavorite = isFavorite) }

        viewModelScope.launch {
            contentDetailDomainModel?.let { detailModel ->
                if (isFavorite) {
                    detailWrapper.deleteFavorite(detailModel.id)
                } else {
                    detailWrapper.insertFavorite(detailModel.mapDomainModelToEntity(mediaType))
                }
            }
        }
    }
}