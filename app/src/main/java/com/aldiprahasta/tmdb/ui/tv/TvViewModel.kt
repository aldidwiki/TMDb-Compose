package com.aldiprahasta.tmdb.ui.tv

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aldiprahasta.tmdb.domain.model.TvDomainModel
import com.aldiprahasta.tmdb.domain.usecase.GetPopularTv
import com.aldiprahasta.tmdb.utils.UiState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class TvViewModel(getPopularTv: GetPopularTv) : ViewModel() {
    val popularTv: StateFlow<UiState<List<TvDomainModel>>> = getPopularTv()
            .stateIn(
                    viewModelScope,
                    SharingStarted.WhileSubscribed(5000),
                    UiState.Loading
            )
}