package com.aldiprahasta.tmdb.ui.details.similar

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.aldiprahasta.tmdb.domain.model.SimilarContent
import com.aldiprahasta.tmdb.domain.usecase.GetSimilarContent
import com.aldiprahasta.tmdb.navigation.SimilarContentListing
import com.aldiprahasta.tmdb.utils.MediaType
import com.aldiprahasta.tmdb.utils.UiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class SimilarContentViewModel(
        private val getSimilarContent: GetSimilarContent,
        savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val contentId = checkNotNull(savedStateHandle.get<Int>(SimilarContentListing.CONTENT_ID_ARG))
    private val mediaType = MediaType.valueOf(checkNotNull(savedStateHandle.get<String>(SimilarContentListing.CONTENT_TYPE_ARG)))
    private val _state = MutableStateFlow<UiState<SimilarContentFeed>>(UiState.Loading)
    val state = _state.asStateFlow()

    init {
        load()
    }

    fun retry() {
        if (_state.value is UiState.Error) load()
    }

    private fun load() {
        _state.value = UiState.Loading
        viewModelScope.launch {
            try {
                val firstPage = getSimilarContent.getFirstPage(contentId, mediaType)
                val items = getSimilarContent(contentId, mediaType, firstPage).cachedIn(viewModelScope)
                _state.value = UiState.Success(SimilarContentFeed(items, firstPage.totalResults))
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                Timber.e(error)
                _state.value = UiState.Error(error)
            }
        }
    }
}

data class SimilarContentFeed(val items: Flow<PagingData<SimilarContent>>, val totalResults: Int)
