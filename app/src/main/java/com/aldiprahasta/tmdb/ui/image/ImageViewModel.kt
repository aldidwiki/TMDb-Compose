package com.aldiprahasta.tmdb.ui.image

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aldiprahasta.tmdb.domain.model.ImageDomainModel
import com.aldiprahasta.tmdb.domain.usecase.GetPersonImages
import com.aldiprahasta.tmdb.utils.UiState
import com.aldiprahasta.tmdb.utils.toStateFlow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest

@OptIn(ExperimentalCoroutinesApi::class)
class ImageViewModel(getPersonImages: GetPersonImages) : ViewModel() {
    private val contentId = MutableStateFlow(0)
    fun setContentId(contentId: Int) {
        this.contentId.value = contentId
    }

    val images: StateFlow<UiState<List<ImageDomainModel>>> = contentId.flatMapLatest { id ->
        getPersonImages(id)
    }.toStateFlow(viewModelScope)
}