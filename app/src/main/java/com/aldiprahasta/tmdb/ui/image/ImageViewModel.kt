package com.aldiprahasta.tmdb.ui.image

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aldiprahasta.tmdb.domain.usecase.GetPersonImages
import com.aldiprahasta.tmdb.navigation.ImageGallery
import com.aldiprahasta.tmdb.utils.UiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

@OptIn(ExperimentalCoroutinesApi::class)
class ImageViewModel(getPersonImages: GetPersonImages, savedStateHandle: SavedStateHandle) : ViewModel() {
    private val personId = checkNotNull(savedStateHandle.get<Int>(ImageGallery.CONTENT_ID_ARG))
    private val loadRevision = MutableStateFlow(0)
    val images = loadRevision.flatMapLatest { getPersonImages(personId) }
            .stateIn(viewModelScope, SharingStarted.Lazily, UiState.Loading)

    fun retry() {
        if (images.value is UiState.Error) loadRevision.update { it + 1 }
    }
}
