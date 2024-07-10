package com.aldiprahasta.tmdb.ui.movie

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.aldiprahasta.tmdb.domain.model.MovieDomainModel
import com.aldiprahasta.tmdb.domain.usecase.GetPopularMovieList
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class MovieViewModel(getPopularMovie: GetPopularMovieList) : ViewModel() {
    val popularMovieList: StateFlow<PagingData<MovieDomainModel>> = getPopularMovie()
            .cachedIn(viewModelScope)
            .stateIn(
                    viewModelScope,
                    SharingStarted.WhileSubscribed(5000L),
                    PagingData.empty()
            )
}