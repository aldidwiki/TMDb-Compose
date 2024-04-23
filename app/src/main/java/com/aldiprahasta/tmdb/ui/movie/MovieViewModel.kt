package com.aldiprahasta.tmdb.ui.movie

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aldiprahasta.tmdb.domain.model.MovieDomainModel
import com.aldiprahasta.tmdb.domain.usecase.GetPopularMovieList
import com.aldiprahasta.tmdb.utils.UiState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class MovieViewModel(getPopularMovie: GetPopularMovieList) : ViewModel() {
    val popularMovieList: StateFlow<UiState<List<MovieDomainModel>>> = getPopularMovie().stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            UiState.Loading
    )
}