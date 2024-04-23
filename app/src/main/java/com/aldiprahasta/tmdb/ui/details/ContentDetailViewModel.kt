package com.aldiprahasta.tmdb.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aldiprahasta.tmdb.domain.model.MovieDetailDomainModel
import com.aldiprahasta.tmdb.domain.usecase.GetMovieDetail
import com.aldiprahasta.tmdb.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ContentDetailViewModel @Inject constructor(getMovieDetail: GetMovieDetail) : ViewModel() {
    private val id: MutableStateFlow<Int> = MutableStateFlow(0)
    fun setId(contentId: Int) {
        id.value = contentId
    }

    val movieDetail: StateFlow<UiState<MovieDetailDomainModel>> = id.flatMapLatest { contentId ->
        getMovieDetail(contentId)
    }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            UiState.Loading
    )
}