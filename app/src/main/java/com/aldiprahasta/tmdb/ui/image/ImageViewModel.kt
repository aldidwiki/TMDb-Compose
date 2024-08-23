package com.aldiprahasta.tmdb.ui.image

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aldiprahasta.tmdb.domain.model.ImageDomainModel
import com.aldiprahasta.tmdb.domain.usecase.GetMovieImages
import com.aldiprahasta.tmdb.utils.UiState
import com.aldiprahasta.tmdb.utils.toStateFlow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest

@OptIn(ExperimentalCoroutinesApi::class)
class ImageViewModel(getMovieImages: GetMovieImages) : ViewModel() {
    private val contentId = MutableSharedFlow<Int>(1)
    fun setContentId(contentId: Int) {
        this.contentId.tryEmit(contentId)
    }

    val images: StateFlow<UiState<List<ImageDomainModel>>> = contentId.flatMapLatest { id ->
        getMovieImages(id)
    }.toStateFlow(viewModelScope)
}