package com.aldiprahasta.tmdb.ui.image

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aldiprahasta.tmdb.domain.usecase.GetMovieImages
import com.aldiprahasta.tmdb.domain.usecase.GetPersonImages
import com.aldiprahasta.tmdb.domain.usecase.GetTvImages
import com.aldiprahasta.tmdb.navigation.ImageGallery
import com.aldiprahasta.tmdb.utils.MediaType
import com.aldiprahasta.tmdb.utils.UiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

@OptIn(ExperimentalCoroutinesApi::class)
class ImageViewModel(
        getPersonImages: GetPersonImages,
        getMovieImages: GetMovieImages,
        getTvImages: GetTvImages,
        savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val contentId = checkNotNull(savedStateHandle.get<Int>(ImageGallery.CONTENT_ID_ARG))
    val mediaType = savedStateHandle.get<String>(ImageGallery.CONTENT_TYPE_ARG)?.let(MediaType::valueOf)
            ?: MediaType.PERSON_TYPE
    private val loadRevision = MutableStateFlow(0)
    val images = loadRevision.flatMapLatest {
        val request = when (mediaType) {
            MediaType.PERSON_TYPE -> getPersonImages(contentId)
            MediaType.MOVIE_TYPE -> getMovieImages(contentId)
            MediaType.TV_TYPE -> getTvImages(contentId)
        }
        request.catch { emit(UiState.Error(it)) }
    }.stateIn(viewModelScope, SharingStarted.Lazily, UiState.Loading)

    fun retry() {
        if (images.value is UiState.Error) loadRevision.update { it + 1 }
    }
}
